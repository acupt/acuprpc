package com.acupt.acuprpc.client;

import com.acupt.acuprpc.core.RpcMethodInfo;
import com.acupt.acuprpc.core.RpcRequest;
import com.acupt.acuprpc.util.JsonUtil;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author liujie
 */
public abstract class RpcClient {

    /**
     * 关闭连接
     */
    public abstract void shutdown();

    /**
     * 远程调用
     */
    protected abstract String remoteInvoke(RpcRequest rpcRequest);

    public Object invoke(RpcMethodInfo methodInfo, Object[] parameters) {
        RpcRequest request = new RpcRequest(methodInfo.getRpcServiceInfo().getServiceName(), methodInfo.getMethodName());
        if (parameters != null && parameters.length > 0) {
            request.setOrderedParameter(Arrays.stream(parameters).map(JsonUtil::toJson).collect(Collectors.toList()));
        }
        String res = remoteInvoke(request);
        return JsonUtil.fromJson(res, TypeFactory.defaultInstance().constructType(methodInfo.getReturnType()));
    }
}
