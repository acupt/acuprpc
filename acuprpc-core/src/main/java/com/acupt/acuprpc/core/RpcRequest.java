package com.acupt.acuprpc.core;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author liujie
 */
@Data
@AllArgsConstructor
public class RpcRequest {

    private String appName;
    private String serviceName;
    private String methodName;
    private List<String> orderedParameter;//优先，null/empty时尝试map
    private Map<String, String> namedParameter;

    public RpcRequest(String appName, String serviceName, String methodName) {
        this.appName = appName;
        this.serviceName = serviceName;
        this.methodName = methodName;
    }

    public String getKey() {
        return appName + ":" + serviceName + "#" + methodName;
    }
}
