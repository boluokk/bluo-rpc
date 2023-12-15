package org.bluo;

import org.bluo.annotation.EnableRpcServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author boluo
 * @date 2023/12/11
 */

@SpringBootApplication
@EnableRpcServer
public class ServerStart {
    public static void main(String[] args) {
        SpringApplication.run(ServerStart.class, args);
    }
}
