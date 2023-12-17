package org.bluo.filter.client;

import org.bluo.common.RpcInvocation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author boluo
 * @date 2023/12/11
 */
public class ClientFilterChain {
    private static List<ClientFilter> clientBeforeFilterList = new ArrayList<>();
    private static List<ClientFilter> clientAfterFilterList = new ArrayList<>();

    public static void addBeforeFilter(ClientFilter clientFilter) {
        clientBeforeFilterList.add(clientFilter);
    }

    public static void addAfterFilter(ClientFilter clientFilter) {
        clientAfterFilterList.add(clientFilter);
    }

    public static void doBeforeFilter(RpcInvocation rpcInvocation) {
        for (ClientFilter clientFilter : clientBeforeFilterList) {
            clientFilter.doFilter(rpcInvocation);
        }
    }

    public static void doAfterFilter(RpcInvocation rpcInvocation) {
        for (ClientFilter clientFilter : clientAfterFilterList) {
            clientFilter.doFilter(rpcInvocation);
        }
    }
}
