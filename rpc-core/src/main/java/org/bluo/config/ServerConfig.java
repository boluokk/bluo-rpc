package org.bluo.config;

import lombok.Data;

/**
 * @author boluo
 * @date 2023/12/15
 */
@Data
public class ServerConfig {
    private int serverPort;
    private String registerAddress;
    private String registerType;
    private String applicationName;
    private String serverSerialize;
    private String registerPassword;
}
