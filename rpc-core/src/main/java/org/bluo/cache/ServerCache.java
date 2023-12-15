package org.bluo.cache;

import org.bluo.config.ServerConfig;
import org.bluo.register.Register;
import org.bluo.serializer.Serializer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author boluo
 * @date 2023/12/15
 */
public class ServerCache {
    public static Serializer serializer;
    public static Register register;
    public static ServerConfig serverConfig;
    public static Map<String, Object> services = new ConcurrentHashMap<>();
}
