package org.bluo.cache;

import org.bluo.common.ServiceWrapper;
import org.bluo.config.ClientConfig;
import org.bluo.register.Register;
import org.bluo.router.Router;
import org.bluo.serializer.Serializer;

import java.util.List;

/**
 * @author boluo
 * @date 2023/12/15
 */
public class ClientCache {
    public static Router router;
    public static Serializer serializer;
    public static Register register;
    public static ClientConfig clientConfig;
    public List<ServiceWrapper> services;
}
