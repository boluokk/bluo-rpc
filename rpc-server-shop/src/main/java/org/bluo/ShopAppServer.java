package org.bluo;

import org.bluo.annotation.EnableRpcServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author boluo
 * @date 2023/12/16
 */

@SpringBootApplication
@EnableRpcServer
public class ShopAppServer {
    public static void main(String[] args) {
        SpringApplication.run(ShopAppServer.class, args);
    }
}
