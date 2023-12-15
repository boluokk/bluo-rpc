package org.bluo.client;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.bluo.cache.ClientCache;
import org.bluo.common.RpcInvocation;
import org.bluo.common.ServiceWrapper;
import org.bluo.filter.client.ClientFilterChain;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 客户端代理对象
 *
 * @author boluo
 * @date 2023/12/08
 */
@Slf4j
public class ClientProxy implements InvocationHandler {

    private final String serviceName;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String uuid = UUID.fastUUID().toString();
        RpcInvocation rpcInvocation =
                new RpcInvocation(method.getDeclaringClass().getName(), method.getName(), args, null, uuid);
        // 过滤器
        ClientFilterChain.doFilter(rpcInvocation);
        // 获取服务 -> 每次都会去拿一下?
        List<ServiceWrapper> services = ClientCache.register.getServices(serviceName);
        // 负载均衡-路由
        if (ObjectUtil.isEmpty(services)) {
            throw new RuntimeException("未找到路由信息, 请确认服务已经注册");
        }
        ServiceWrapper select = ClientCache.router.select(services);
        log.info("服务：{} - {}", select.getDomain(), select.getPort());
        Object result = null;
        int count = 3;
        while (--count > 0) {
            try {
                CompletableFuture<Object> completableFuture = Client.seedMessage(select, rpcInvocation);
                assert completableFuture != null;
                result = completableFuture.get();
            } catch (Throwable e) {
                log.warn("发送失败：{} - {}", e.getMessage(), e.getClass());
            }
        }
        assert result != null;

        return ((RpcInvocation) result).getResult();
    }

    public ClientProxy(String serviceName) {
        this.serviceName = serviceName;
    }
}
