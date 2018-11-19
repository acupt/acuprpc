package com.acupt.acuprpc.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author liujie
 */
@Getter
@AllArgsConstructor
public class RpcServiceInfo {
    private String appName;
    private String serviceName;

    @Override
    public String toString() {
        return appName + ":" + serviceName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RpcServiceInfo that = (RpcServiceInfo) o;
        return Objects.equals(appName, that.appName) &&
                Objects.equals(serviceName, that.serviceName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appName, serviceName);
    }
}
