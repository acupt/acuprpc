package com.acupt.acuprpc.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * @author liujie
 */
@Getter
@AllArgsConstructor
public class RpcMethodInfo {
    private RpcServiceInfo rpcServiceInfo;
    private String methodName;
    private Type returnType;

    @Override
    public String toString() {
        return rpcServiceInfo + "#" + methodName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RpcMethodInfo that = (RpcMethodInfo) o;
        return Objects.equals(rpcServiceInfo, that.rpcServiceInfo) &&
                Objects.equals(methodName, that.methodName) &&
                Objects.equals(returnType, that.returnType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rpcServiceInfo, methodName, returnType);
    }
}
