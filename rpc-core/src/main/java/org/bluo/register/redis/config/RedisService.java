package org.bluo.register.redis.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * @author boluo
 * @date 2023/12/02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedisService {
    Set<String> services;
}
