package com.acupt.acuprpc.spring;

import com.acupt.acuprpc.core.NodeInfo;
import com.acupt.acuprpc.core.RpcServiceInfo;

import java.lang.reflect.Proxy;

/**
 * @author liujie
 */
public class RpcServiceConsumer {

    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> serviceInterface, NodeInfo nodeInfo) {
        RpcServiceInfo serviceInfo = new RpcServiceInfo(serviceInterface.getCanonicalName());
        return (T) Proxy.newProxyInstance(
                serviceInterface.getClassLoader(),
                new Class<?>[]{serviceInterface},
                new RpcInvocationHandler(serviceInfo, nodeInfo));
    }
}
