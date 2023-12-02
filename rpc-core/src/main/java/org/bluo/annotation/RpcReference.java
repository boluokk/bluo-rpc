package org.bluo.annotation;

/**
 * 客服端注解
 *
 * @author boluo
 * @date 2023/11/30
 */
public @interface RpcReference {
    String name() default "";
}
