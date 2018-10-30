package com.acupt.acuprpc.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Type;

/**
 * @author liujie
 */
@Data
@AllArgsConstructor
public class RpcMethodInfo {
    private RpcServiceInfo rpcServiceInfo;
    private String methodName;
    private Type returnType;
}
