package org.bluo.spi;

import lombok.extern.slf4j.Slf4j;
import org.bluo.common.RpcInvocation;
import org.bluo.filter.server.ServerFilter;

/**
 * @author boluo
 * @date 2023/12/17
 */
@Slf4j
public class ServerTestAfterFilterImpl implements ServerFilter {
    @Override
    public void doFilter(RpcInvocation rpcInvocation) {
        log.debug("测试自定义后置过滤器");
    }
}
