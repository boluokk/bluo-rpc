package org.bluo.config;

import lombok.Data;

/**
 * @author boluo
 * @date 2023/12/15
 */
@Data
public class ClientConfig {
    private String routerType;
    private String applicationName;
    private String registerAddress;
    private String clientSerialize;
    private String registerType;
    private int retryTimes;
    private int retryInterval;
    private String registerPassword;
}
