package org.bluo.constants;

/**
 * @author boluo
 * @date 2023/11/30
 */
public class RpcConstants {
    public static final short MAGIC_NUMBER = 9527;
    public static final String REDIS_SERVICE_PREFIX_KEY = "rpc-service-";
    public static final int REDIS_GET_INTERVAL = 10;
    public static final int REDIS_GET_INTERVAL_MILLISECONDS = (REDIS_GET_INTERVAL - 5) * 1000;
    public static final int DEFAULT_HEARTBEAT_INTERVAL = 3;
    public static final long REDIS_REGISTER_EXPIRATION = 1000 * 3;
    public static final long REDIS_REGISTER_EXPIRATION_30_SECONDS = 1000 * 30;
}
