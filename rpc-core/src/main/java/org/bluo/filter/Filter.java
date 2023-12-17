package org.bluo.filter;

import org.bluo.common.RpcInvocation;

/**
 * @author boluo
 * @date 2023/12/10
 */
public interface Filter {
    void doFilter(RpcInvocation rpcInvocation);
}
