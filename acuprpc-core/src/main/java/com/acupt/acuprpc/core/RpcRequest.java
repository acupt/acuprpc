package com.acupt.acuprpc.core;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Parameter;
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

    /**
     * 参数名{@link Parameter#getName()} -> 参数值json
     * 编译项目代码时需要加上参数 -parameters（java8开始支持）
     * 否则无法利用反射获取参数名（只能得到arg0,arg1,arg3...）
     */
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
