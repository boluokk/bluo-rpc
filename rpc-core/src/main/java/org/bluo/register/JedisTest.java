package org.bluo.register;

import redis.clients.jedis.Jedis;

/**
 * @author boluo
 * @date 2023/12/20
 */
public class JedisTest {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        jedis.auth("123456");
        String script = "return redis.call('get', 'rpc:service:apple')";
        System.out.println(jedis.eval(script));;
    }
}
