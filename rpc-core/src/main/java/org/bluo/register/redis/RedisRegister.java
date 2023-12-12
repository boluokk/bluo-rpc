package org.bluo.register.redis;

import cn.hutool.core.util.ObjectUtil;
import org.bluo.common.ServiceListWrapper;
import org.bluo.common.ServiceWrapper;
import org.bluo.properties.CommonProperties;
import org.bluo.register.SimpleRegisterAbstract;
import org.bluo.register.redis.config.RedisUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.bluo.constants.RpcConstants.REDIS_SERVICE_PREFIX_DEFAULT_EXPIRATION;
import static org.bluo.constants.RpcConstants.REDIS_SERVICE_PREFIX_KEY;

/**
 * redis注册中心
 *
 * @author boluo
 * @date 2023/12/02
 */
@SuppressWarnings("ConstantConditions")
public class RedisRegister extends SimpleRegisterAbstract {

    private RedisUtil redisUtil;

    public RedisRegister(CommonProperties commonProperties) {
        super(commonProperties);
        redisUtil = new RedisUtil(commonProperties.getRedisUrl(),
                commonProperties.getRedisPort(), commonProperties.getRedisPassword());
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
            if (serviceListMapWrapper.getValue().size() == 0) {
                RedisUtil.delete(key);
            } else {
                RedisUtil.setWithExpiration(key, serviceListMapWrapper,
                        REDIS_SERVICE_PREFIX_DEFAULT_EXPIRATION);
            }
        }
    }

    @Override
    public List<ServiceWrapper> getServices(String serviceName) {
        String key = REDIS_SERVICE_PREFIX_KEY + ":" + serviceName;
        ServiceListWrapper listWrapper = RedisUtil.get(key, ServiceListWrapper.class);
        return listWrapper.getValue();
    }
}
