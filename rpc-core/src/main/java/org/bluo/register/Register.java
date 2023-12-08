package org.bluo.register;

import org.bluo.common.ServiceWrapper;

import java.util.List;

/**
 * @author boluo
 * @date 2023/12/02
 */
public interface Register {
    /**
     * 注册服务
     */
    void register(String serviceName, ServiceWrapper serviceWrapper);

    /**
     * 注销服务
     */
    void unRegister(String serviceName, ServiceWrapper serviceWrapper);

    /**
     * 获取服务
     */
    List<ServiceWrapper> getServices(String serviceName);
}
