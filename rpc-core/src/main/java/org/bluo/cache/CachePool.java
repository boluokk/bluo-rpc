package org.bluo.cache;

import org.bluo.common.RpcProtocol;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author boluo
 * @date 2023/11/30
 */
public class CachePool {
    /*
     * 缓存结果
     */
    public static final ConcurrentHashMap<String, Object> resultCache = new ConcurrentHashMap<>();

}
