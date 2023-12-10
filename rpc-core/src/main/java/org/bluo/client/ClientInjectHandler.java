package org.bluo.client;

import lombok.extern.slf4j.Slf4j;
import org.bluo.annotation.RpcReference;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * @author boluo
 * @date 2023/12/08
 */
@Configuration
@Slf4j
public class ClientInjectHandler implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> aClass = bean.getClass();
        Field[] fields = aClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(RpcReference.class)) {
                try {
                    field.setAccessible(true);
                    System.out.println(field.getType());

                    field.set(bean, Proxy.newProxyInstance(
                            Thread.currentThread().getContextClassLoader(),
                            new Class[]{field.getType()},
                            new ClientProxy()));

                    field.setAccessible(false);
                } catch (IllegalAccessException e) {
                    log.error("客户端实例化错误 {}", e.getMessage());
                }
            }
        }
        return bean;
    }
}