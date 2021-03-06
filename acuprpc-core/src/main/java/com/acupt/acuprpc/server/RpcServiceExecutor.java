package com.acupt.acuprpc.server;

import com.acupt.acuprpc.core.MethodInfo;
import com.acupt.acuprpc.core.RpcCode;
import com.acupt.acuprpc.core.RpcRequest;
import com.acupt.acuprpc.core.RpcResponse;
import com.acupt.acuprpc.exception.HttpStatusException;
import com.acupt.acuprpc.exception.RpcException;
import com.acupt.acuprpc.util.JsonUtil;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * @author liujie
 */
@Slf4j
public class RpcServiceExecutor implements RpcCode {

    private Object serviceInstance;

    private Map<String, MethodInfo> methodInfoMap;

    RpcServiceExecutor(Object serviceInstance, Map<String, MethodInfo> methodInfoMap) {
        this.serviceInstance = serviceInstance;
        this.methodInfoMap = methodInfoMap;
    }

    @SneakyThrows
    public void execute(RpcRequest rpcRequest, RpcResponse rpcResponse) {
        MethodInfo methodInfo = methodInfoMap.get(rpcRequest.getMethodName());
        if (methodInfo == null) {
            throw new HttpStatusException(METHOD_NOT_ALLOWED, "method not found:" + rpcRequest.getKey());
        }
        Object result = methodInfo.getMethod().invoke(serviceInstance,
                convertParameter(methodInfo.getMethod(), rpcRequest));
        rpcResponse.success(result);
    }

    private Object[] convertParameter(Method method, RpcRequest request) {
        if (request.getOrderedParameter() != null && !request.getOrderedParameter().isEmpty()) {
            return convertParameter(method, request.getOrderedParameter());
        }
        return convertParameter(method, request.getNamedParameter());
    }

    private Object[] convertParameter(Method method, List<String> orderedParameter) {
        Type[] types = method.getGenericParameterTypes();
        Object[] paramArray = new Object[types.length];
        if (types.length == 0) {
            return paramArray;
        }
        if (orderedParameter == null || orderedParameter.size() != types.length) {
            throw new RpcException(method.toString() + " not support parameter: " + orderedParameter);
        }
        for (int i = 0; i < types.length; i++) {
            paramArray[i] = JsonUtil.fromJson(orderedParameter.get(i),
                    TypeFactory.defaultInstance().constructType(types[i]));
        }
        return paramArray;
    }

    private Object[] convertParameter(Method method, Map<String, String> namedParameter) {
        Parameter[] parameters = method.getParameters();
        Object[] paramArray = new Object[parameters.length];
        if (parameters.length == 0) {
            return paramArray;
        }
        boolean allNull = true;
        for (int i = 0; i < paramArray.length; i++) {
            String name = parameters[i].getName();
            String json = namedParameter.get(name);
            if (json == null) {
                paramArray[i] = null;
            } else {
                allNull = false;
                paramArray[i] = JsonUtil.fromJson(json,
                        TypeFactory.defaultInstance().constructType(parameters[i].getType()));
            }
        }
        if (allNull) {
            boolean needParameters = true;
            for (int i = 0; i < parameters.length; i++) {
                if (!("arg" + i).equalsIgnoreCase(parameters[i].getName())) {
                    needParameters = false;
                    break;
                }
            }
            if (needParameters) {
                log.warn("can't resolve parameter for " + method.toString() + ", try add compiler args: -parameters");
            }
        }
        return paramArray;
    }
}
