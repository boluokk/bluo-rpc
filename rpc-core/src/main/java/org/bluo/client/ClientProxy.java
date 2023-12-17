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
        RpcInvocation rpcInvocation = new RpcInvocation(method.getDeclaringClass().getName(),
                method.getName(), args, method.getParameterTypes(), null, uuid, null);
        // 前置处理
        ClientFilterChain.doBeforeFilter(rpcInvocation);
        // 获取服务
        List<ServiceWrapper> services = ClientCache.register.getServices(serviceName);
        // 负载均衡
        if (ObjectUtil.isEmpty(services)) {
            log.error("未找到路由信息, 请确认服务已经注册");
            throw new Exception("未找到路由信息, 请确认服务已经注册");
        }
        ServiceWrapper select = ClientCache.router.select(services);
        log.info("服务：{} - {}", select.getDomain(), select.getPort());
        Object result = null;
        int count = ClientCache.clientConfig.getRetryTimes();
        while (--count > 0) {
            try {
                CompletableFuture<Object> completableFuture = Client.seedMessage(select, rpcInvocation);
                result = completableFuture.get();
                // 后置处理
                ClientFilterChain.doAfterFilter((RpcInvocation) result);
                if (ObjectUtil.isNotEmpty(result)) {
                    break;
                }
            } catch (Throwable e) {
                log.warn("发送失败：{} - {}", e.getMessage(), e.getClass());
            }
        }

        return ((RpcInvocation) result).getResult();
    }

    public ClientProxy(String serviceName) {
        this.serviceName = serviceName;
    }
}
