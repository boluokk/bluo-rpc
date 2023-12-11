package org.bluo.filter.client;

import org.bluo.common.RpcInvocation;
import org.bluo.filter.Filter;

/**
 * @author boluo
 * @date 2023/12/10
 */
public interface ClientFilter extends Filter {
    void doFilter(RpcInvocation rpcInvocation);
}
