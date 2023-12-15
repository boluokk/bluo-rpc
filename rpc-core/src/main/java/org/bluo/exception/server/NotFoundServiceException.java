package org.bluo.exception.server;

import org.bluo.exception.BaseException;

/**
 * @author boluo
 * @date 2023/12/15
 */
public class NotFoundServiceException extends BaseException {
    public NotFoundServiceException(String message) {
        super(message);
    }
}
