package org.bluo.client;

import cn.hutool.core.util.ObjectUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.bluo.cache.ClientCache;
import org.bluo.common.*;
import org.bluo.config.ClientConfig;
import org.bluo.config.ConfigLoader;
import org.bluo.filter.client.ClientFilter;
import org.bluo.filter.client.ClientFilterChain;
import org.bluo.register.Register;
import org.bluo.router.Router;
import org.bluo.serializer.Serializer;
import org.bluo.spi.ExtraLoader;

import java.net.InetSocketAddress;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.bluo.cache.CachePool.RESULT_CACHE;
import static org.bluo.cache.CachePool.extraLoader;

/**
 * 用户端
 *
 * @author boluo
 * @date 2023/11/30
 */
@Slf4j
public class Client {
    private static Bootstrap bootstrap = new Bootstrap();

    static {
        initConfiguration();
        bootstrap.group(new NioEventLoopGroup());
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.RCVBUF_ALLOCATOR,
                new AdaptiveRecvByteBufAllocator(2048, 1024 * 10, 1024 * 10));
        bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel channel) throws Exception {
                channel.pipeline().addLast(new MessageEncoder());
                channel.pipeline().addLast(new MessageDecoder(ClientCache.serializer));
                channel.pipeline().addLast(new ClientChannelInboundHandler());
            }
        });
    }

    public static void initConfiguration() {
        // 加载扩展类
        try {
            ClientConfig clientConfig = ConfigLoader.loadClientProperties();
            extraLoader.loadExtension(Register.class);
            extraLoader.loadExtension(Serializer.class);
            extraLoader.loadExtension(Router.class);
            extraLoader.loadExtension(ClientFilter.class);
            LinkedHashMap<String, Class> registerClass = ExtraLoader.EXTENSION_LOADER_CLASS_CACHE.get(Register.class.getName());
            LinkedHashMap<String, Class> serializeClass = ExtraLoader.EXTENSION_LOADER_CLASS_CACHE.get(Serializer.class.getName());
            LinkedHashMap<String, Class> routerClass = ExtraLoader.EXTENSION_LOADER_CLASS_CACHE.get(Router.class.getName());
            // 过滤器
            LinkedHashMap<String, Class> clientFilterClass = ExtraLoader.EXTENSION_LOADER_CLASS_CACHE.get(ClientFilter.class.getName());
            if (ObjectUtil.isNotEmpty(clientFilterClass)) {
                Set<String> clientFilterKeys = clientFilterClass.keySet();
                for (String key : clientFilterKeys) {
                    if (!key.toLowerCase().contains("after")) {
                        ClientFilterChain.addBeforeFilter((ClientFilter) clientFilterClass.get(key).newInstance());
                    } else {
                        ClientFilterChain.addAfterFilter((ClientFilter) clientFilterClass.get(key).newInstance());
                    }
                }
            }
            // 注册中心
            Class regCls = registerClass.get(clientConfig.getRegisterType());
            if (ObjectUtil.isEmpty(regCls)) {
                throw new RuntimeException("注册中心类型不存在");
            }
            ClientCache.register = (Register) regCls.getConstructor(String.class, String.class)
                    .newInstance(clientConfig.getRegisterAddress(),
                            clientConfig.getRegisterPassword());
            // 序列化
            Class serCls = serializeClass.get(clientConfig.getClientSerialize());
            if (ObjectUtil.isEmpty(serCls)) {
                throw new RuntimeException("序列化类型不存在");
            }
            ClientCache.serializer = (Serializer) serCls.newInstance();
            // 负载均衡
            Class routerCls = routerClass.get(clientConfig.getRouterType());
            if (ObjectUtil.isEmpty(routerCls)) {
                throw new RuntimeException("负载均衡类型不存在");
            }
            ClientCache.router = (Router) routerCls.newInstance();
            ClientCache.clientConfig = clientConfig;
        } catch (Exception e) {
            log.error("加载扩展类失败", e);
        }
    }

    public static ChannelFuture getChannelFuture(ServiceWrapper router) throws InterruptedException {
        return bootstrap.connect(new InetSocketAddress(router.getDomain(), router.getPort())).sync();
    }

    public static CompletableFuture<Object> seedMessage(ServiceWrapper router, RpcInvocation rpcInvocation) {
        Channel channel = null;
        CompletableFuture<Object> future = new CompletableFuture<>();
        RpcProtocol rpcProtocol = new RpcProtocol();
        try {
            byte[] body = ClientCache.serializer.serialize(rpcInvocation);
            rpcProtocol.setContentLength(body.length);
            rpcProtocol.setContent(body);
            channel = getChannelFuture(router).channel();
            RESULT_CACHE.put(rpcInvocation.getUuid(), future);
            if (!channel.isActive()) {
                bootstrap.group().shutdownGracefully();
                return null;
            }
            channel.writeAndFlush(rpcProtocol).addListener((ChannelFutureListener) future1 -> {
                if (!future1.isSuccess()) {
                    future1.channel().close();
                    future.completeExceptionally(future1.cause());
                }
            });
        } catch (Exception e) {
            RESULT_CACHE.remove(rpcInvocation.getUuid());
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return future;
    }
}
