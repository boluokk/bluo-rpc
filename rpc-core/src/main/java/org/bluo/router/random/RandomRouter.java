package org.bluo.router.random;

import org.bluo.common.ServiceWrapper;
import org.bluo.register.Register;
import org.bluo.router.Router;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author boluo
 * @date 2023/12/02
 */
public class RandomRouter implements Router {

    @Override
    public String select(List<String> services) {
        return services.get(ThreadLocalRandom.current().nextInt(0, services.size()));
    }

    @Override
    public List<String> refresh(Register register, String serviceName) {
        return null;
    }


}
