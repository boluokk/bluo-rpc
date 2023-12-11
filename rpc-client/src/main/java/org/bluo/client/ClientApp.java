package org.bluo.client;

import org.bluo.annotation.EnableRpcClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author boluo
 * @date 2023/12/11
 */

@SpringBootApplication
@EnableRpcClient
public class ClientApp {
    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(ClientApp.class, args);
    }
}
