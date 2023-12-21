package org.bluo.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 实际方法调用
 *
 * @author boluo
 * @date 2023/11/30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcInvocation {
    /**
     * 类名
     */
    private String className;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 参数
     */
    private Object[] params;
    /**
     * 参数类型
     */
    private Class<?>[] paramTypes;
    /**
     * 返回值
     */
    private Object result;
    /**
     * 唯一id
     */
    private String uuid;
    /**
     * 服务器异常信息
     */
    private Throwable e;
    /**
     * 是否是心跳包
     */
    private boolean heartBeat;

    public RpcInvocation(String className, String methodName, Object[] params, Class<?>[] paramTypes, String uuid) {
        this.className = className;
        this.methodName = methodName;
        this.params = params;
        this.paramTypes = paramTypes;
        this.uuid = uuid;
    }
}
