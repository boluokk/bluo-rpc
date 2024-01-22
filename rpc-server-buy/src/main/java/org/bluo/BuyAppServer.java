package org.bluo;

import org.bluo.annotation.EnableRpcServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author boluo
 */
@EnableRpcServer
@SpringBootApplication
public class BuyAppServer {
    public static void main(String[] args) {
        SpringApplication.run(BuyAppServer.class, args);
    }
}
