package org.bluo.annotation;

import org.bluo.server.ServerInjectHandle;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务端注解
 *
 * @author boluo
 * @date 2023/12/02
 */
@Import(ServerInjectHandle.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableRpcServer {
}
