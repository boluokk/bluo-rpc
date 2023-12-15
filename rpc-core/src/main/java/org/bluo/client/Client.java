package org.bluo.client;

import cn.hutool.core.util.ObjectUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.bluo.cache.CachePool;
import org.bluo.cache.ClientCache;
import org.bluo.common.*;
import org.bluo.config.ClientConfig;
import org.bluo.config.ConfigLoader;
import org.bluo.register.Register;
import org.bluo.router.Router;
import org.bluo.serialize.Serialize;
import org.bluo.serialize.jackson.JacksonSerialize;
import org.bluo.spi.ExtraLoader;

import java.net.InetSocketAddress;
import java.util.LinkedHashMap;
import java.util.concurrent.CompletableFuture;

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
        bootstrap.group(new NioEventLoopGroup());
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel channel) throws Exception {
                channel.pipeline().addLast(new MessageEncoder());
                channel.pipeline().addLast(new MessageDecoder());
                channel.pipeline().addLast(new ClientChannelInboundHandler());
            }
        });
        initConfiguration();
    }

    public static void initConfiguration() {
        // 加载扩展类
        try {
            ClientConfig clientConfig = ConfigLoader.loadClientProperties();
            extraLoader.loadExtension(Register.class);
            extraLoader.loadExtension(Serialize.class);
            extraLoader.loadExtension(Router.class);
            LinkedHashMap<String, Class> registerClass = ExtraLoader.EXTENSION_LOADER_CLASS_CACHE.get(Register.class.getName());
            LinkedHashMap<String, Class> serializeClass = ExtraLoader.EXTENSION_LOADER_CLASS_CACHE.get(Serialize.class.getName());
            LinkedHashMap<String, Class> routerClass = ExtraLoader.EXTENSION_LOADER_CLASS_CACHE.get(Router.class.getName());

            // 注册中心
            Class regCls = registerClass.get(clientConfig.getRegisterType());
            if (ObjectUtil.isEmpty(regCls)) {
                throw new RuntimeException("注册中心类型不存在");
            }
            ClientCache.register = (Register) regCls.newInstance();
            // 序列化
            Class serCls = serializeClass.get(clientConfig.getClientSerialize());
            if (ObjectUtil.isEmpty(serCls)) {
                throw new RuntimeException("序列化类型不存在");
            }
            ClientCache.serialize = (Serialize) serCls.newInstance();
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
            byte[] body = new JacksonSerialize().serialize(rpcInvocation);
            rpcProtocol.setContentLength(body.length);
            rpcProtocol.setContent(body);
            channel = getChannelFuture(router).channel();
            CachePool.resultCache.put(rpcInvocation.getUuid(), future);
            if (!channel.isActive()) {
                bootstrap.group().shutdownGracefully();
                return null;
            }
            channel.writeAndFlush(rpcProtocol).addListener((ChannelFutureListener) future1 -> {
                if (future1.isSuccess()) {
                    log.info(String.format("客户端发送消息: %s", rpcInvocation));
                } else {
                    future1.channel().close();
                    future.completeExceptionally(future1.cause());
                }
            });
        } catch (Exception e) {
            CachePool.resultCache.remove(rpcInvocation.getUuid());
            log.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        return future;
    }
}
