package org.bluo.register.redis;

import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.bluo.common.ServiceListWrapper;
import org.bluo.common.ServiceWrapper;
import org.bluo.register.SimpleRegisterAbstract;
import org.bluo.register.redis.config.RedisUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    private RedisUtil redisUtil;

    private volatile List<ServiceWrapper> serviceList;

    public RedisRegister(String url, String password) {
        redisUtil = new RedisUtil(url, password);
    }

    public RedisRegister() {
    }

    @Override
    public void register(String serviceName, ServiceWrapper serviceWrapper) {
        String key = REDIS_SERVICE_PREFIX_KEY + ":" + serviceName;
        ServiceListWrapper serviceMapWrapper = RedisUtil.get(key, ServiceListWrapper.class);
        if (ObjectUtil.isNotEmpty(serviceMapWrapper.getValue())) {
            List<ServiceWrapper> value = serviceMapWrapper.getValue();
            for (int i = 0; i < value.size(); i++) {
                ServiceWrapper cur = value.get(i);
                if (cur.equals(serviceWrapper)) {
                    return;
                }
            }
            value.add(serviceWrapper);
            RedisUtil.setWithExpiration(key, serviceMapWrapper, REDIS_SERVICE_PREFIX_DEFAULT_EXPIRATION);
        } else {
            ServiceListWrapper serviceListWrapper = new ServiceListWrapper();
            ArrayList<ServiceWrapper> list = new ArrayList<>();
            list.add(serviceWrapper);
            serviceListWrapper.setValue(list);
            RedisUtil.setWithExpiration(key, serviceListWrapper, REDIS_SERVICE_PREFIX_DEFAULT_EXPIRATION);
        }
    }

    @Override
    public void unRegister(String serviceName, ServiceWrapper serviceWrapper) {
        String key = REDIS_SERVICE_PREFIX_KEY + ":" + serviceName;
        ServiceListWrapper serviceListMapWrapper = RedisUtil.get(key, ServiceListWrapper.class);
        if (ObjectUtil.isNotEmpty(serviceListMapWrapper.getValue())) {
            List<ServiceWrapper> value = serviceListMapWrapper.getValue();
            Iterator<ServiceWrapper> iterator = value.iterator();
            while (iterator.hasNext()) {
                ServiceWrapper next = iterator.next();
                if (next.equals(serviceWrapper)) {
                    iterator.remove();
                }
            }
            RedisUtil.setWithExpiration(key, serviceListMapWrapper,
                    REDIS_SERVICE_PREFIX_DEFAULT_EXPIRATION);
        }
    }

    @Override
    public List<ServiceWrapper> getServices(String serviceName) {
        String key = REDIS_SERVICE_PREFIX_KEY + ":" + serviceName;
        if (ObjectUtil.isEmpty(serviceList)) {
            synchronized (this) {
                if (ObjectUtil.isEmpty(serviceList)) {
                    serviceList = RedisUtil.get(key, ServiceListWrapper.class).getValue();
                    new Thread(() -> {
                        while (true) {
                            try {
                                Thread.sleep(REDIS_CLIENT_PREFIX_DEFAULT_EXPIRATION * 1000);
                                serviceList = RedisUtil.get(key, ServiceListWrapper.class).getValue();
                            } catch (InterruptedException e) {
                                log.debug("更新服务端失败: {}", e.getMessage());
                            }
                        }
                    }).start();
                }
            }
        }
        return serviceList;
    }
}
