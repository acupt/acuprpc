package com.acupt.acuprpc.core;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author liujie
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class RpcServiceInfo {
    private String appName;
    private String serviceName;

    @Override
    public String toString() {
        return appName + ":" + serviceName;
    }
}
