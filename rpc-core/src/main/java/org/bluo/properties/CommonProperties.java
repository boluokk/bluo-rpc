package org.bluo.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author boluo
 * @date 2023/12/12
 */
@Configuration
@Data
public class CommonProperties {
    @Value("${rpc.redis.url:127.0.0.1}")
    private String redisUrl;
    @Value("${rpc.redis.port:6379}")
    private int redisPort;
    @Value("${rpc.redis.password:}")
    private String redisPassword;
    @Value("${rpc.register.path:org.bluo.register.redis.RedisRegister}")
    private String registerPath;
}
