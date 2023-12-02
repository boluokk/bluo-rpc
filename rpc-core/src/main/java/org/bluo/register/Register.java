package org.bluo.register;

import org.bluo.common.ServiceWrapper;

import java.util.List;

/**
 * @author boluo
 * @date 2023/12/02
 */
public interface Register {
    void register(String serviceName, ServiceWrapper serviceWrapper);

    void unRegister(String serviceName, ServiceWrapper serviceWrapper);

    List<ServiceWrapper> getServices(String serviceName);
}
