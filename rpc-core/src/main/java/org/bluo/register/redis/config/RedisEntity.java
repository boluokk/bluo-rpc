package org.bluo.register.redis.config;

import lombok.Data;

/**
 * @author boluo
 * @date 2023/12/02
 */
@Data
public class RedisEntity {
    private String host;
    private String password;
    private int port;
}
