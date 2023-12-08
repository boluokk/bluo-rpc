package org.bluo.router;

import org.bluo.common.ServiceWrapper;
import org.bluo.register.Register;

import java.util.List;

/**
 * @author boluo
 * @date 2023/12/02
 */
public abstract class RouterAbs implements Router {
    /**
     * 刷新服务列表
     */
    @Override
    public List<ServiceWrapper> refresh(Register register, String serviceName) {
        return register.getServices(serviceName);
    }
}
