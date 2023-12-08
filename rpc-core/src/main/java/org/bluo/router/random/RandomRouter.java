package org.bluo.router.random;

import org.bluo.common.ServiceWrapper;
import org.bluo.router.RouterAbs;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author boluo
 * @date 2023/12/02
 */
public class RandomRouter extends RouterAbs {
    /**
     * 随机选择一个服务
     */
    @Override
    public ServiceWrapper select(List<ServiceWrapper> services) {
        return services.get(ThreadLocalRandom.current().nextInt(0, services.size()));
    }
}
