package org.bluo.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 服务信息
 *
 * @author boluo
 * @date 2023/12/02
 */
@Data
public class ServiceWrapper implements Serializable {
    private String domain;
    private int port;

    public String getUrl() {
        return domain + ":" + port;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ServiceWrapper && getUrl().equals(((ServiceWrapper) obj).getUrl());
    }
}
