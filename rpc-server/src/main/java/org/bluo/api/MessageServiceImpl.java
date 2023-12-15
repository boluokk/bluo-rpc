package org.bluo.api;

import org.bluo.annotation.RpcService;
import org.springframework.stereotype.Service;

/**
 * @author boluo
 * @date 2023/12/11
 */

@RpcService
@Service
public class MessageServiceImpl implements MessageService {
    @Override
    public String getMessage(String message) {
        return "apple shopping";
    }
}
