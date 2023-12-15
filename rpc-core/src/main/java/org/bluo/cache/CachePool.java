package org.bluo.cache;

import org.bluo.spi.ExtraLoader;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author boluo
 * @date 2023/11/30
 */
public class CachePool {
    /*
     * 缓存结果
     */
    public static final ConcurrentHashMap<String, Object> RESULT_CACHE = new ConcurrentHashMap<>();

    public static ExtraLoader extraLoader = new ExtraLoader();

}
