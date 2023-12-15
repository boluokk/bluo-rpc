package org.bluo.server;

import cn.hutool.core.util.ObjectUtil;
import org.bluo.annotation.RpcService;
import org.bluo.cache.ServerCache;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;

/**
 * @author boluo
 * @date 2023/12/15
 */
@Configuration
public class ServerInjectHandle implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {
            Class<?> api = bean.getClass().getInterfaces()[0];
            if (ObjectUtil.isEmpty(api)) {
                throw new RuntimeException("未实现任何接口");
            }
            ServerCache.services.put(api.getName(), bean);
        }
        return bean;
    }
}
