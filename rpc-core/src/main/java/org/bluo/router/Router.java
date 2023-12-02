package org.bluo.router;

import org.bluo.register.Register;

import java.util.List;
import java.util.Set;

/**
 * @author boluo
 * @date 2023/12/02
 */
public interface Router {
    /**
     * 获取服务
     */
    String select(List<String> services);

    /**
     * 刷新路由
     */
    List<String> refresh(Register register, String serviceName);
}
