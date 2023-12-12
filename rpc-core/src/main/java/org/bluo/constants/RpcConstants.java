package org.bluo.constants;

/**
 * @author boluo
 * @date 2023/11/30
 */
public class RpcConstants {
    public static final short MAGIC_NUMBER = 9527;
    public static final String REDIS_SERVICE_PREFIX_KEY = "rpc:service";
    public static final int REDIS_SERVICE_PREFIX_DEFAULT_EXPIRATION = 60 * 30 * 9999;
}
