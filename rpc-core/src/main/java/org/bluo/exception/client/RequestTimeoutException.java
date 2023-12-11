package org.bluo.exception.client;

import org.bluo.exception.BaseException;

/**
 * @author boluo
 * @date 2023/12/11
 */
public class RequestTimeoutException extends BaseException {
    public RequestTimeoutException(String message) {
        super(message);
    }
}
