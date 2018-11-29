package com.acupt.acuprpc.spring;

import com.acupt.acuprpc.core.RpcServiceInfo;

import java.lang.reflect.Proxy;

/**
 * @author liujie
 */
public class RpcServiceConsumer {

    private RpcClientManager rpcClientManager;

    public RpcServiceConsumer(RpcClientManager rpcClientManager) {
        this.rpcClientManager = rpcClientManager;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(String appName, Class<T> serviceInterface) {
        RpcServiceInfo serviceInfo = new RpcServiceInfo(appName, serviceInterface.getCanonicalName());
        return (T) Proxy.newProxyInstance(
                serviceInterface.getClassLoader(),
                new Class<?>[]{serviceInterface},
                new RpcInvocationHandler(serviceInfo, rpcClientManager));
    }
}
