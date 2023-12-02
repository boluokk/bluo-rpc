package org.bluo.register;

/**
 * @author boluo
 * @date 2023/12/02
 */
public interface Register {
    void register(String serviceName, String host, int port);
    void unRegister(String serviceName, String host, int port);
}
