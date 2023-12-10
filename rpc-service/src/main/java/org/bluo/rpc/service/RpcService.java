package org.bluo.rpc.service;

import org.bluo.annotation.EnableRpcClient;
import org.bluo.rpc.service.controller.TestController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author boluo
 * @date 2023/12/08
 */

@SpringBootApplication
@EnableRpcClient
public class RpcService {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(RpcService.class, args);
        System.out.println(run.getBean(TestController.class));
    }
}
