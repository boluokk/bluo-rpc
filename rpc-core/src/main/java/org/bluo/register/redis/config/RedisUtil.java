package org.bluo.register.redis.config;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

/**
 * @author boluo
 */
@Slf4j
public class RedisUtil {
    private static Jedis jedis;

    public RedisUtil(String url, String password) {
        String[] split = url.split(":");
        jedis = new Jedis(split[0], Integer.parseInt(split[1]));
        if (password != null && !password.isEmpty()) {
            try {
                jedis.auth(password);
            } catch (Throwable e) {
                log.error("Redis认证失败: {}", e.getMessage());
            }
        }
    }

    public RedisUtil() {
    }

    public static void set(String key, Object value) {
        try {
            jedis.set(key, JSONUtil.toJsonStr(value));
        } catch (Throwable e) {
            log.error("添加数据失败: {}", e.getMessage());
        }
    }

    public static <T> T get(String key, Class<T> clazz) {
        try {
            return JSONUtil.toBean(jedis.get(key), clazz);
        } catch (Throwable e) {
            log.error("获取数据失败: key={}, 错误信息={}", key, e.getMessage());
            return null;
        }
    }

    public static void delete(String key) {
        try {
            jedis.del(key);
        } catch (Throwable e) {
            log.error("删除数据失败: key={}, 错误信息={}", key, e.getMessage());
        }
    }

    public static void setWithExpiration(String key, Object value, int seconds) {
        try {
            jedis.setex(key, seconds, JSONUtil.toJsonStr(value));
        } catch (Throwable e) {
            log.error("设置数据失败: key={}, 错误信息={}", key, e.getMessage());
        }
    }

    public static Long getTimeToLive(String key) {
        try {
            return jedis.ttl(key);
        } catch (Throwable e) {
            log.error("获取剩余时间失败: key={}, 错误信息={}", key, e.getMessage());
            return null;
        }
    }

    public static void close() {
        try {
            jedis.close();
        } catch (Throwable e) {
            log.error("关闭失败: {}", e.getMessage());
        }
    }

    public static void evalLua(String luaScript, String key, Object value) {
        try {
            jedis.eval(luaScript, 1, key, JSONUtil.toJsonStr(value));
        } catch (Throwable e) {
            log.error("执行lua失败: {}", e.getMessage());
        }
    }
}
