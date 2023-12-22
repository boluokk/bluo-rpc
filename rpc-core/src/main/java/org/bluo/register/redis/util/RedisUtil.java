package org.bluo.register.redis.util;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author boluo
 */
@Slf4j
public class RedisUtil {
    private JedisPool jedisPool;

    public RedisUtil(String address, String password) {
        String[] add = address.split(":");
        jedisPool = new JedisPool(new JedisPoolConfig(), add[0], Integer.valueOf(add[1]), null, password);
    }

    public void zSetAddByScore(String key, Object value, long score) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.zadd(key, score, JSONUtil.toJsonStr(value));
        } catch (Throwable e) {
            log.error("设置数据失败: key={}, 错误信息={} - {}", key, e.getMessage(), e.getClass());
        }
    }

    public void zSetRemoveByScore(String key, long score) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.zremrangeByScore(key, 0, score);
        } catch (Throwable e) {
            log.error("设置数据失败: key={}, 错误信息={}", key, e.getMessage());
        }

    }

    public <T> List<T> zSetRangeRetObj(String key, long start, long end, Class<T> clazz) {
        return zSetRange(key, start, end).stream()
                .map(s -> JSONUtil.toBean(s, clazz))
                .collect(Collectors.toList());
    }

    public Set<String> zSetRange(String key, long start, long end) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrangeByScore(key, start, end);
        } catch (Throwable e) {
            log.error("设置数据失败: key={}, 错误信息={}", key, e.getMessage());
        }
        return null;
    }
}