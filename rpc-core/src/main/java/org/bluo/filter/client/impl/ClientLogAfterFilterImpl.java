package org.bluo.filter.client.impl;

import lombok.extern.slf4j.Slf4j;
import org.bluo.common.RpcInvocation;
import org.bluo.filter.client.ClientFilter;

/**
 * @author boluo
 * @date 2023/12/17
 */
@Slf4j
public class ClientLogAfterFilterImpl implements ClientFilter {
    @Override
    public void doFilter(RpcInvocation rpcInvocation) {
        log.info("客户端接收: {}", rpcInvocation);
    }
}
