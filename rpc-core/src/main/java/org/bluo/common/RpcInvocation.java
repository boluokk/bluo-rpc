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
     * 返回值
     */
    private Object result;
}
