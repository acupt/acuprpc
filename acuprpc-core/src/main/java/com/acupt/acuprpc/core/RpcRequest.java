package com.acupt.acuprpc.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author liujie
 */
@Data
@AllArgsConstructor
public class RpcRequest {

    private String serviceName;
    private String methodName;
    private List<String> orderedParameter;

    public RpcRequest(String serviceName, String methodName) {
        this.serviceName = serviceName;
        this.methodName = methodName;
    }

    public String getKey() {
        return serviceName + "#" + methodName;
    }
}
