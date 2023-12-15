package org.bluo.cache;

import org.bluo.config.ClientConfig;
import org.bluo.register.Register;
import org.bluo.router.Router;
import org.bluo.serialize.Serialize;

/**
 * @author boluo
 * @date 2023/12/15
 */
public class ClientCache {
    public static Router router;
    public static Serialize serialize;
    public static Register register;
    public static ClientConfig clientConfig;
}
