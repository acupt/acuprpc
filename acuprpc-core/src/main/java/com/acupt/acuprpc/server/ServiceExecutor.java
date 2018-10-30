package com.acupt.acuprpc.server;

import com.acupt.acuprpc.core.MethodInfo;
import com.acupt.acuprpc.core.RpcRequest;
import com.acupt.acuprpc.exception.RpcException;
import com.acupt.acuprpc.util.JsonUtil;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * @author liujie
 */
public class ServiceExecutor {

    private Object serviceInstance;

    private Map<String, MethodInfo> methodInfoMap;

    public ServiceExecutor(Object serviceInstance, Map<String, MethodInfo> methodInfoMap) {
        this.serviceInstance = serviceInstance;
        this.methodInfoMap = methodInfoMap;
    }

    @SneakyThrows
    public Object execute(RpcRequest rpcRequest) {
        MethodInfo methodInfo = methodInfoMap.get(rpcRequest.getMethodName());
        if (methodInfo == null) {
            throw new RpcException("method not found:" + rpcRequest.getKey());
        }
        return methodInfo.getMethod().invoke(serviceInstance,
                convertParameter(methodInfo.getMethod(), rpcRequest.getOrderedParameter()));
    }

    private Object[] convertParameter(Method method, List<String> orderedParameter) {
        Type[] types = method.getGenericParameterTypes();
        Object[] paramArray = new Object[types.length];
        if (types.length == 0) {
            return paramArray;
        }
        for (int i = 0; i < types.length; i++) {
            paramArray[i] = JsonUtil.fromJson(orderedParameter.get(i),
                    TypeFactory.defaultInstance().constructType(types[i]));
        }
        return paramArray;
    }
}
