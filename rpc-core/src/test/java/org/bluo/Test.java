package org.bluo;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.bluo.register.redis.config.RedisService;
import org.bluo.register.redis.config.RedisUtil;

import java.util.Arrays;

import static org.bluo.constants.RpcConstants.REDIS_SERVICE_PREFIX_KEY;

/**
 * @author boluo
 * @date 2023/12/01
 */
public class Test {
    public static void main(String[] args) throws JsonProcessingException {

//        ArrayList<String> list = new ArrayList<>();
//        list.add("127.0.0.1:5000");
//        list.add("127.0.0.1:4000");
//        list.add("127.0.0.1:3000");
//        RedisUtil.set(REDIS_SERVICE_PREFIX_KEY, new RedisService(list));
        System.out.println(RedisUtil.get(REDIS_SERVICE_PREFIX_KEY + "ww", RedisService.class));
    }
}
