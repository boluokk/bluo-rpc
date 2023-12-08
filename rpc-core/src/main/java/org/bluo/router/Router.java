package org.bluo.router;

import org.bluo.common.ServiceWrapper;
import org.bluo.register.Register;

import java.util.List;

/**
 * @author boluo
 * @date 2023/12/02
 */
public interface Router {
    /**
     * 获取服务
     */
    ServiceWrapper select(List<ServiceWrapper> services);

    /**
     * 刷新路由
     */
    List<ServiceWrapper> refresh(Register register, String serviceName);
}
