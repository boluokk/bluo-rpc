package org.bluo.config;

import java.io.IOException;

/**
 * @author boluo
 * @date 2023/12/15
 */
public class ConfigLoader {

    private static final String prefix = "rpc.";
    private static final String applicationName = prefix + "applicationName";
    private static final String registerType = prefix + "registerType";
    private static final String registerAddr = prefix + "registerAddr";
    private static final String serialize = prefix + "serialize";
    private static final String port = prefix + "port";
    private static final String routerType = prefix + "routerType";
    private static final String retryTime = prefix + "retryTime";
    private static final String retryInterval = prefix + "retryInterval";
    private static final String registerPassword = prefix + "registerPassword";
    private static final String packageName = prefix + "packageName";

    public static ClientConfig loadClientProperties() throws IOException {
        ClientConfig clientConfig = new ClientConfig();
        PropertiesLoader.loadConfiguration();
        clientConfig.setApplicationName(PropertiesLoader.getPropertiesStrDefault(applicationName, "rpc-client"));
        clientConfig.setClientSerialize(PropertiesLoader.getPropertiesStrDefault(serialize, "jackson"));
        clientConfig.setRegisterAddress(PropertiesLoader.getPropertiesStrDefault(registerAddr, "127.0.0.1:6379"));
        clientConfig.setRegisterType(PropertiesLoader.getPropertiesStrDefault(registerType, "redis"));
        clientConfig.setRouterType(PropertiesLoader.getPropertiesStrDefault(routerType, "random"));
        clientConfig.setRetryTimes(Integer.valueOf(PropertiesLoader.getPropertiesStrDefault(retryTime, "5")));
        clientConfig.setRetryInterval(Integer.valueOf(PropertiesLoader.getPropertiesStrDefault(retryInterval, "1000")));
        clientConfig.setRegisterPassword(PropertiesLoader.getPropertiesStrDefault(registerPassword, "123456"));
        return clientConfig;
    }

    public static ServerConfig loadServerProperties() throws IOException {
        ServerConfig serverConfig = new ServerConfig();
        PropertiesLoader.loadConfiguration();
        serverConfig.setServerPort(Integer.valueOf(PropertiesLoader.getPropertiesStrDefault(port, "8080")));
        serverConfig.setApplicationName(PropertiesLoader.getPropertiesStrDefault(applicationName, "rpc-server"));
        serverConfig.setServerSerialize(PropertiesLoader.getPropertiesStrDefault(serialize, "jackson"));
        serverConfig.setRegisterAddress(PropertiesLoader.getPropertiesStrDefault(registerAddr, "127.0.0.1:6379"));
        serverConfig.setRegisterType(PropertiesLoader.getPropertiesStrDefault(registerType, "redis"));
        serverConfig.setRegisterPassword(PropertiesLoader.getPropertiesStrDefault(registerPassword, "123456"));
        return serverConfig;
    }
}
