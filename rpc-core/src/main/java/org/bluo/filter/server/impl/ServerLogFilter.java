package org.bluo.filter.server.impl;

import lombok.extern.slf4j.Slf4j;
import org.bluo.common.RpcInvocation;
import org.bluo.filter.server.ServerFilter;

/**
 * @author boluo
 * @date 2023/12/11
 */
@Slf4j
public class ServerLogFilter implements ServerFilter {
    @Override
    public void doFilter(RpcInvocation rpcInvocation) {
        log.info("服务端日志记录: {}", rpcInvocation);
    }
}
