package org.bluo.client;

import lombok.extern.slf4j.Slf4j;
import org.bluo.annotation.RpcReference;
import org.bluo.properties.CommonProperties;
import org.bluo.register.Register;
import org.bluo.router.Router;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

/**
 * @author boluo
 * @date 2023/12/08
 */
@Configuration
@ComponentScan("org.bluo.properties")
@Slf4j
public class ClientInjectHandler implements BeanPostProcessor {

    @Resource
    private CommonProperties commonProperties;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> aClass = bean.getClass();
        Field[] fields = aClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(RpcReference.class)) {
                try {
                    RpcReference annotation = field.getAnnotation(RpcReference.class);
                    Class<?> type = field.getType();
                    field.setAccessible(true);
                    field.set(bean, Proxy.newProxyInstance(
                            type.getClassLoader(),
                            new Class<?>[]{type},
                            new ClientProxy(register(), annotation.serviceName(),
                                    (Router) annotation.router().newInstance())));
                    field.setAccessible(false);
                } catch (IllegalAccessException | InstantiationException e) {
                    log.error("客户端实例化错误 {}", e.getMessage());
                }
            }
        }
        return bean;
    }

    @Bean
    public Register register() {
        String registerPath = commonProperties.getRegisterPath();
        try {
            Class<?> aClass = Class.forName(registerPath);
            Constructor<?> constructor = aClass.getConstructor(CommonProperties.class);
            return (Register) constructor.newInstance(commonProperties);
        } catch (InvocationTargetException | NoSuchMethodException | ClassNotFoundException |
                 IllegalAccessException | InstantiationException e) {
            log.warn("注册中心对象实例失败: {}", e.getMessage());
        }
        return null;
    }
}
