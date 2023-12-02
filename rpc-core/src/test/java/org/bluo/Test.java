package org.bluo;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.bluo.common.ServiceWrapper;
import org.bluo.register.redis.RedisRegister;
import org.bluo.register.redis.config.RedisUtil;

import java.awt.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import static org.bluo.constants.RpcConstants.REDIS_SERVICE_PREFIX_KEY;

/**
 * @author boluo
 * @date 2023/12/01
 */
public class Test {
    public static void main(String[] args) throws JsonProcessingException {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            String ipAddress = localHost.getHostAddress();
            String hostName = localHost.getHostName();

            System.out.println("IP Address: " + ipAddress);
            System.out.println("Host Name: " + hostName);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
