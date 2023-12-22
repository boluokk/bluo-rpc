package org.bluo.api;

import org.bluo.annotation.RpcService;
import org.springframework.stereotype.Service;

/**
 * @author boluo
 * @date 2023/12/22
 */
@Service
@RpcService
public class OtherServiceImpl implements OtherService {
    @Override
    public String getMessage(String message) {
        return "other service-1";
    }
}
