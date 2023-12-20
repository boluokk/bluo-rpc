package org.bluo.register.redis;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.bluo.common.ServiceListWrapper;
import org.bluo.common.ServiceWrapper;
import org.bluo.register.SimpleRegisterAbstract;
import org.bluo.register.redis.config.RedisUtil;

import java.util.List;

import static org.bluo.constants.RpcConstants.REDIS_CLIENT_PREFIX_DEFAULT_EXPIRATION;
import static org.bluo.constants.RpcConstants.REDIS_SERVICE_PREFIX_KEY;

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

    private String regLua;
    private String unRegLua;

    private volatile List<ServiceWrapper> serviceList;

    public RedisRegister(String url, String password) {
        redisUtil = new RedisUtil(url, password);
        initLua();
    }

    public RedisRegister() {
        initLua();
    }

    private void initLua() {
        regLua = ResourceUtil.readUtf8Str(System.getProperty("user.dir") + "/rpc-core/src/main/java/org/bluo/register/redis/lua/reg.lua");
        unRegLua = ResourceUtil.readUtf8Str(System.getProperty("user.dir") + "/rpc-core/src/main/java/org/bluo/register/redis/lua/unReg.lua");
    }

    @Override
    public void register(String serviceName, ServiceWrapper serviceWrapper) {
        String key = REDIS_SERVICE_PREFIX_KEY + ":" + serviceName;
        redisUtil.evalLua(regLua, key, serviceWrapper);
    }

    @Override
    public void unRegister(String serviceName, ServiceWrapper serviceWrapper) {
        String key = REDIS_SERVICE_PREFIX_KEY + ":" + serviceName;
        redisUtil.evalLua(unRegLua, key, serviceWrapper);
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
