package org.bluo.client.controller;

import org.bluo.annotation.RpcReference;
import org.bluo.api.MessageService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author boluo
 * @date 2023/12/11
 */

@RestController
@RequestMapping("/client")
public class ClientController {
    @RpcReference(serviceName = "shop")
    public MessageService messageService;
    public static final int count = 1000;

    public static final int THREAD_COUNT = 10;
    ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(THREAD_COUNT);

    @RequestMapping("/test")
    public String test() throws InterruptedException {
//        System.out.println(messageService.getMessage("123"));
        CountDownLatch countDownLatch = new CountDownLatch(THREAD_COUNT);
        long l = System.currentTimeMillis();
        for (int i = 0; i < 15; i++) {
            threadPoolExecutor.submit(() -> {
                for (int j = 0; j < count; j++) {
                    messageService.getMessage("test");
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        long tps = (THREAD_COUNT * count) / ((System.currentTimeMillis() - l) / 1000);
        System.out.println("测试TPS：" + tps);
        return "ok";
    }
}