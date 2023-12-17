package org.bluo.filter.server;

import org.bluo.common.RpcInvocation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author boluo
 * @date 2023/12/11
 */
public class ServerFilterChain {
    private static List<ServerFilter> serverBeforeFilters = new ArrayList<>();
    private static List<ServerFilter> serverAfterFilters = new ArrayList<>();

    public static void addBeforeFilter(ServerFilter serverFilter) {
        serverBeforeFilters.add(serverFilter);
    }

    public static void addAfterFilter(ServerFilter serverFilter) {
        serverAfterFilters.add(serverFilter);
    }

    public static void doBeforeFilter(RpcInvocation rpcInvocation) {
        for (ServerFilter filter : serverBeforeFilters) {
            filter.doFilter(rpcInvocation);
        }
    }

    public static void doAfterFilter(RpcInvocation rpcInvocation) {
        for (ServerFilter filter : serverAfterFilters) {
            filter.doFilter(rpcInvocation);
        }
    }
}
