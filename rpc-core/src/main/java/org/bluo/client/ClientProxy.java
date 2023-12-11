package org.bluo.client;

import cn.hutool.core.lang.UUID;
import lombok.extern.slf4j.Slf4j;
import org.bluo.common.RpcInvocation;
import org.bluo.filter.client.ClientFilterChain;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

/**
 * 客户端代理对象
 *
 * @author boluo
 * @date 2023/12/08
 */
@Slf4j
public class ClientProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String uuid = UUID.fastUUID().toString();
        RpcInvocation rpcInvocation =
                new RpcInvocation(method.getDeclaringClass().getName(), method.getName(), args, null, uuid);
        // 过滤器
        ClientFilterChain.doFilter(rpcInvocation);
        Object result = null;
        int count = 3;
        while (--count > 0) {
            try {
                CompletableFuture<Object> completableFuture = Client.seedMessage(rpcInvocation);
                result = completableFuture.get();
            } catch (Throwable e) {
                log.warn("发送失败：{} - {}", e.getMessage(), e.getClass());
            }
        }
        return ((RpcInvocation) result).getResult();
    }
}
