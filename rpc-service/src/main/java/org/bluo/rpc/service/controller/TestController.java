package org.bluo.rpc.service.controller;

import org.bluo.annotation.RpcReference;
import org.bluo.api.MessageService;
import org.springframework.stereotype.Component;

/**
 * @author boluo
 * @date 2023/12/08
 */

@Component
public class TestController {
    @RpcReference
    private MessageService messageService;
}
