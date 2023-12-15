package org.bluo;

import org.bluo.annotation.EnableRpcServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

/**
 * @author boluo
 * @date 2023/12/11
 */

@SpringBootApplication
@EnableRpcServer
public class ServerStart {
    public static void main(String[] args) throws IOException {
        ConfigurableApplicationContext run = SpringApplication.run(ServerStart.class, args);
        System.in.read();
    }
}
