package org.bluo.filter.client;

import org.bluo.common.RpcInvocation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author boluo
 * @date 2023/12/11
 */
public class ClientFilterChain {
    private static List<ClientFilter> clientFilterList = new ArrayList<>();

    public static void addFilter(ClientFilter clientFilter) {
        clientFilterList.add(clientFilter);
    }

    public static void doFilter(RpcInvocation rpcInvocation) {
        for (ClientFilter clientFilter : clientFilterList) {
            clientFilter.doFilter(rpcInvocation);
        }
    }
}
