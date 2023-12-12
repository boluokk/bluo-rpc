package org.bluo.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.bluo.common.RpcInvocation;
import org.bluo.common.RpcProtocol;
import org.bluo.serialize.jackson.JacksonSerialize;

/**
 * @author boluo
 * @date 2023/11/30
 */
@Slf4j
public class ServerChannelInboundHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            log.info("获取到数据: {}", msg);
            RpcInvocation rpcInvocation = (RpcInvocation) msg;
            rpcInvocation.setResult("ok");
            byte[] serialize = new JacksonSerialize().serialize(rpcInvocation);
            RpcProtocol rpcProtocol = new RpcProtocol();
            rpcProtocol.setContentLength(serialize.length);
            rpcProtocol.setContent(serialize);
            ctx.writeAndFlush(rpcProtocol);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Channel channel = ctx.channel();
        channel.close().addListener(future -> {
            if (future.isSuccess()) {
                log.info("管道关闭成功");
            } else {
                log.error("管道关闭失败", future.cause());
            }
        });
    }
}
