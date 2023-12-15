package org.bluo.filter.server;

import org.bluo.common.RpcInvocation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author boluo
 * @date 2023/12/11
 */
public class ServerFilterChain {
    private static List<ServerFilter> filters = new ArrayList<>();

    public static void addFilter(ServerFilter serverFilter) {
        filters.add(serverFilter);
    }

    public static void doBeforeFilter(RpcInvocation rpcInvocation) {
        for (ServerFilter filter : filters) {
            filter.doFilter(rpcInvocation);
        }
    }

    public static void doAfterFilter(RpcInvocation rpcInvocation) {
        for (ServerFilter filter : filters) {
        }
    }
}
