package org.bluo.api;

import org.bluo.annotation.RpcService;
import org.springframework.stereotype.Service;

/**
 * @author boluo
 * @date 2023/12/16
 */
@RpcService
@Service
public class MessageServiceImpl implements MessageService {
    @Override
    public String getMessage(String message) {
        return "shop service by other";
    }
}
