package org.bluo.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 客户端代理对象
 *
 * @author boluo
 * @date 2023/12/08
 */

public class ClientProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println(12312);
        Object invoke = method.invoke(proxy, args);
        return "12312";
    }
}
