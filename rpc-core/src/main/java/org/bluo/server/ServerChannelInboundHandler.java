package org.bluo.server;

import cn.hutool.core.util.ObjectUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.bluo.common.RpcInvocation;
import org.bluo.common.RpcProtocol;
import org.bluo.exception.server.NotFoundServiceException;
import org.bluo.filter.server.ServerFilterChain;

import java.lang.reflect.Method;

import static org.bluo.cache.ClientCache.serializer;
import static org.bluo.cache.ServerCache.services;

/**
 * @author boluo
 * @date 2023/11/30
 */
@Slf4j
public class ServerChannelInboundHandler extends SimpleChannelInboundHandler<RpcInvocation> {

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

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcInvocation rpcInvocation) throws Exception {
        RpcProtocol rpcProtocol = null;
        try {
            //是否心跳包
            if (rpcInvocation.isHeartBeat()) {
                log.info("接受到心跳包");
                return;
            }
            rpcProtocol = new RpcProtocol();
            // 前置处理
            ServerFilterChain.doBeforeFilter(rpcInvocation);
            Object clsBean = services.get(rpcInvocation.getClassName());
            if (ObjectUtil.isEmpty(clsBean)) {
                rpcInvocation.setE(new NotFoundServiceException("未找到对应服务"));
            } else {
                Method method = clsBean.getClass().getMethod(rpcInvocation.getMethodName(), rpcInvocation.getParamTypes());
                // 调用方法
                if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                    Object ret = method.invoke(clsBean, rpcInvocation.getParams());
                    rpcInvocation.setResult(ret);
                } else {
                    log.info("管道不可写如数据");
                }
                // 后置处理
                ServerFilterChain.doAfterFilter(rpcInvocation);
            }
        } catch (Throwable e) {
            rpcInvocation.setResult(null);
            rpcInvocation.setE(e);
        } finally {
            byte[] data = serializer.serialize(rpcInvocation);
            rpcProtocol.setContent(data);
            rpcProtocol.setContentLength(data.length);
            ctx.writeAndFlush(rpcProtocol);
        }
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.info("长时间未收到心跳包，断开连接...");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
