package org.bluo.client;

import org.bluo.client.controller.ClientController;
import org.bluo.common.ServiceWrapper;
import org.bluo.register.redis.config.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author boluo
 * @date 2023/12/15
 */
@SpringBootTest
public class ClientTest {
    @Resource
    ClientController clientController;

    @Test
    public void test() {
//        clientController.test();
        RedisUtil redisUtil = new RedisUtil("127.0.0.1:6379", "123456");
        List<ServiceWrapper> serviceWrappers = redisUtil.zSetRangeByScore("rpc-service-buy", 0, System.currentTimeMillis() + 10000, ServiceWrapper.class);
        System.out.println(serviceWrappers);
    }
}
