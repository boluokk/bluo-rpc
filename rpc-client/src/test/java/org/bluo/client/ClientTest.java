package org.bluo.client;

import org.bluo.client.controller.ClientController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author boluo
 * @date 2023/12/15
 */
@SpringBootTest
public class ClientTest {
    @Resource
    ClientController clientController;

    @Test
    public void test() {
    }
}
