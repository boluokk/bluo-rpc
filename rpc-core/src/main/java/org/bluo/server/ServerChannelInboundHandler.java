package org.bluo.server;

import cn.hutool.core.util.ObjectUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.bluo.common.RpcInvocation;
import org.bluo.common.RpcProtocol;
import org.bluo.exception.server.NotFoundServiceException;

import java.lang.reflect.Method;

import static org.bluo.cache.ClientCache.serializer;
import static org.bluo.cache.ServerCache.services;

/**
 * @author boluo
 * @date 2023/11/30
 */
@Slf4j
public class ServerChannelInboundHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcProtocol rpcProtocol = null;
        RpcInvocation rpcInvocation = null;
        try {
            // 调用接口
            log.info("获取到数据: {}", msg);
            rpcProtocol = new RpcProtocol();
            rpcInvocation = (RpcInvocation) msg;
            Object clsBean = services.get(rpcInvocation.getClassName());
            if (ObjectUtil.isEmpty(clsBean)) {
                rpcInvocation.setEx(new NotFoundServiceException("未找到对应服务"));
            } else {
                Method method = clsBean.getClass().getMethod(rpcInvocation.getMethodName(), rpcInvocation.getParamTypes());
                Object ret = method.invoke(clsBean, rpcInvocation.getParams());
                rpcInvocation.setResult(ret);
            }
        } catch (Throwable e) {
            rpcInvocation.setEx(e);
        } finally {
            byte[] data = serializer.serialize(rpcInvocation);
            rpcProtocol.setContent(data);
            rpcProtocol.setContentLength(data.length);
            ctx.writeAndFlush(rpcProtocol);
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
