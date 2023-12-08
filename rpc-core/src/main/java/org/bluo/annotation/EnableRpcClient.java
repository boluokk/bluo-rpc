package org.bluo.annotation;

import org.bluo.client.ClientInjectHandle;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author boluo
 * @date 2023/12/08
 */
@Import(ClientInjectHandle.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableRpcClient {
}
