package org.bluo.annotation;

import org.bluo.router.random.RandomRouter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 客服端注解
 *
 * @author boluo
 * @date 2023/11/30
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RpcReference {
    /**
     * 远程服务名
     */
    String serviceName();

    /*
     * 负载均衡算法
     */
    Class<?> router() default RandomRouter.class;

    /**
     * 超时时间
     */
    long timeout() default 5000;

    /**
     * 版本号
     */
    String version() default "1.0";
}
