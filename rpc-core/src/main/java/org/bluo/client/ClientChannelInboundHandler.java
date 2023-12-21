package org.bluo.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.bluo.cache.ClientCache;
import org.bluo.common.RpcInvocation;
import org.bluo.common.RpcProtocol;

import java.util.concurrent.CompletableFuture;

import static org.bluo.cache.CachePool.RESULT_CACHE;

/**
 * @author boluo
 * @date 2023/11/30
 */
@Slf4j
public class ClientChannelInboundHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            RpcInvocation data = (RpcInvocation) msg;
            ((CompletableFuture) RESULT_CACHE.remove(data.getUuid())).complete(msg);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        channel.close().addListener(future -> {
            if (future.isSuccess()) {
                log.info("管道关闭成功");
            } else {
                log.error("管道关闭失败", future.cause());
            }
        });
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                log.info("发送心跳包 [{}]", ctx.channel().remoteAddress());
                RpcInvocation rpcInvocation = new RpcInvocation();
                rpcInvocation.setHeartBeat(true);
                byte[] data = ClientCache.serializer.serialize(rpcInvocation);
                RpcProtocol rpcProtocol = new RpcProtocol();
                rpcProtocol.setContentLength(data.length);
                rpcProtocol.setContent(data);
                ctx.writeAndFlush(rpcProtocol).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
