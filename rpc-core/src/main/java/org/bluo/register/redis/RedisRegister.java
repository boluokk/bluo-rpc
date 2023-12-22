package org.bluo.register.redis;

import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.bluo.common.ServiceWrapper;
import org.bluo.register.SimpleRegisterAbstract;
import org.bluo.register.redis.util.RedisUtil;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.bluo.constants.RpcConstants.*;

/**
 * redis注册中心
 *
 * @author boluo
 * @date 2023/12/02
 */
@SuppressWarnings("ConstantConditions")
@Slf4j
public class RedisRegister extends SimpleRegisterAbstract {

    private List<ServiceWrapper> serviceList;
    private RedisUtil redisUtil;

    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);


    public RedisRegister(String address, String password) {
        redisUtil = new RedisUtil(address, password);
    }

    public RedisRegister() {
    }

    @Override
    public void register(String serviceName, ServiceWrapper serviceWrapper) {
        redisUtil.zSetAddByScore(REDIS_SERVICE_PREFIX_KEY.concat(serviceName), serviceWrapper, System.currentTimeMillis());
    }

    @Override
    public void unRegister(String serviceName, ServiceWrapper serviceWrapper) {
        redisUtil.zSetRemoveByScore(REDIS_SERVICE_PREFIX_KEY.concat(serviceName),
                System.currentTimeMillis() - REDIS_REGISTER_EXPIRATION_30_SECONDS);
    }

    @Override
    public List<ServiceWrapper> getServices(String serviceName) {
        String key = REDIS_SERVICE_PREFIX_KEY.concat(serviceName);
        if (ObjectUtil.isNull(serviceList)) {
            synchronized (this) {
                if (ObjectUtil.isNull(serviceList)) {

                    serviceList = redisUtil.zSetRangeRetObj(key, 0,
                            System.currentTimeMillis() + REDIS_GET_INTERVAL_MILLISECONDS,
                            ServiceWrapper.class);

                    executorService.scheduleAtFixedRate(() -> {
                        long intervalGetTime = System.currentTimeMillis();
                        List<ServiceWrapper> serviceWrappers = redisUtil.zSetRangeRetObj(key,
                                System.currentTimeMillis() - REDIS_GET_INTERVAL_MILLISECONDS,
                                intervalGetTime + REDIS_GET_INTERVAL_MILLISECONDS, ServiceWrapper.class);
                        serviceList = serviceWrappers;
                    }, 0, REDIS_GET_INTERVAL, TimeUnit.SECONDS);

                }
            }
        }
        return serviceList;
    }
}
