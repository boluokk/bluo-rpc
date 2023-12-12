package org.bluo.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.bluo.cache.CachePool;
import org.bluo.common.*;
import org.bluo.serialize.jackson.JacksonSerialize;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

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
