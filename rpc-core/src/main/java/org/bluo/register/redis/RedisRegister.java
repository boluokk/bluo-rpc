package org.bluo.register.redis;

import cn.hutool.core.util.ObjectUtil;
import org.bluo.register.SimpleRegisterAbstract;
import org.bluo.register.redis.config.RedisService;
import org.bluo.register.redis.config.RedisUtil;

import java.util.HashSet;
import java.util.Set;

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

    @Override
    public void register(String serviceName, String host, int port) {
        String key = REDIS_SERVICE_PREFIX_KEY + ":" + serviceName;
        String value = host + ":" + port;
        RedisService redisService = RedisUtil.get(key, RedisService.class);
        if (ObjectUtil.isNotEmpty(redisService.getServices())) {
            if (redisService.getServices().contains(value)) {
                return;
            }
            redisService.getServices().add(value);
            RedisUtil.setWithExpiration(key, redisService, REDIS_SERVICE_PREFIX_DEFAULT_EXPIRATION);
        } else {
            Set<String> serviceList = new HashSet<>();
            serviceList.add(value);
            RedisUtil.setWithExpiration(key, new RedisService(serviceList), REDIS_SERVICE_PREFIX_DEFAULT_EXPIRATION);
        }
    }

    @Override
    public void unRegister(String serviceName, String host, int port) {
        String key = REDIS_SERVICE_PREFIX_KEY + ":" + serviceName;
        String value = host + ":" + port;
        RedisService redisService = RedisUtil.get(key, RedisService.class);
        if (ObjectUtil.isNotEmpty(redisService.getServices()) &&
                redisService.getServices().contains(value)) {
            redisService.getServices().remove(value);
            RedisUtil.setWithExpiration(key, redisService, REDIS_SERVICE_PREFIX_DEFAULT_EXPIRATION);
        }
    }
}
