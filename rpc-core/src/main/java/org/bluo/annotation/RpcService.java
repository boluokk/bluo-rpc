package org.bluo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author boluo
 * @date 2023/12/11
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcService {
    /**
     * 版本号
     */
    String version() default "1.0";

    /**
     * 实现的接口，默认第一个
     */
    Class<?> interfaceClass() default void.class;
}
