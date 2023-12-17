package org.bluo.server;

import cn.hutool.core.util.ObjectUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
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
            rpcProtocol = new RpcProtocol();
            // 前置处理
            ServerFilterChain.doBeforeFilter(rpcInvocation);
            Object clsBean = services.get(rpcInvocation.getClassName());
            if (ObjectUtil.isEmpty(clsBean)) {
                rpcInvocation.setEx(new NotFoundServiceException("未找到对应服务"));
            } else {
                Method method = clsBean.getClass().getMethod(rpcInvocation.getMethodName(), rpcInvocation.getParamTypes());
                // 调用方法
                Object ret = method.invoke(clsBean, rpcInvocation.getParams());
                rpcInvocation.setResult(ret);
            }
        } catch (Throwable e) {
            rpcInvocation.setEx(e);
        } finally {
            // 后置处理
            ServerFilterChain.doAfterFilter(rpcInvocation);
            byte[] data = serializer.serialize(rpcInvocation);
            rpcProtocol.setContent(data);
            rpcProtocol.setContentLength(data.length);
            ctx.writeAndFlush(rpcProtocol);
        }
    }
}
