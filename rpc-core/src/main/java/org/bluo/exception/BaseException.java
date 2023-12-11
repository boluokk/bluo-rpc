package org.bluo.exception;

/**
 * @author boluo
 * @date 2023/12/11
 */
public abstract class BaseException extends Exception {
    public BaseException(String message) {
        super(message);
    }
    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
