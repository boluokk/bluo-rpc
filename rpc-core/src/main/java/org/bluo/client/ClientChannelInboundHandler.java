package org.bluo.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.bluo.common.RpcInvocation;

import static org.bluo.cache.CachePool.resultCache;

/**
 * @author boluo
 * @date 2023/11/30
 */
@Slf4j
public class ClientChannelInboundHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcInvocation data = (RpcInvocation) msg;
        resultCache.put(data.getUuid(), data);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("客户端发生错误: ", cause);

        super.exceptionCaught(ctx, cause);
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
