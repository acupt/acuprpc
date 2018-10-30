package com.acupt.acuprpc.spring;

import com.acupt.acuprpc.client.RpcClient;
import com.acupt.acuprpc.core.NodeInfo;
import com.acupt.acuprpc.core.RpcMethodInfo;
import com.acupt.acuprpc.core.RpcServiceInfo;
import com.acupt.acuprpc.protocol.grpc.GrpcClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author liujie
 */
public class RpcInvocationHandler implements InvocationHandler {

    private RpcClient rpcClient;

    private RpcServiceInfo rpcServiceInfo;

    public RpcInvocationHandler(RpcServiceInfo rpcServiceInfo, NodeInfo nodeInfo) {
        rpcClient = new GrpcClient(nodeInfo);
        this.rpcServiceInfo = rpcServiceInfo;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcMethodInfo methodInfo = new RpcMethodInfo(rpcServiceInfo, method.getName(), method.getGenericReturnType());
        return rpcClient.invoke(methodInfo, args);
    }
}
