package org.bluo.client.controller;

import org.bluo.annotation.RpcReference;
import org.bluo.api.MessageService;
import org.bluo.api.OtherService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author boluo
 * @date 2023/12/11
 */

@RestController
@RequestMapping("/client")
public class ClientController {
    @RpcReference(serviceName = "buy")
    private MessageService messageService;

    @RpcReference(serviceName = "shop")
    private OtherService otherService;

    @RequestMapping("/test")
    public String test() {
        return messageService.getMessage("111") + " @ " + otherService.getMessage("222");
    }
}
