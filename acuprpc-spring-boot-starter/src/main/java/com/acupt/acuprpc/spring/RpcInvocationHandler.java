package com.acupt.acuprpc.spring;

import com.acupt.acuprpc.client.RpcClient;
import com.acupt.acuprpc.core.RpcMethodInfo;
import com.acupt.acuprpc.core.RpcServiceInfo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author liujie
 */
public class RpcInvocationHandler implements InvocationHandler {

    private RpcServiceInfo rpcServiceInfo;

    private RpcServiceManager rpcServiceManager;

    private RpcClient rpcClient;

    public RpcInvocationHandler(RpcServiceInfo rpcServiceInfo, RpcServiceManager rpcServiceManager) {
        this.rpcServiceInfo = rpcServiceInfo;
        this.rpcServiceManager = rpcServiceManager;
        tryInitRpcClient(false);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcMethodInfo methodInfo = new RpcMethodInfo(rpcServiceInfo, method.getName(), method.getGenericReturnType());
        return tryGetRpcClient().invoke(methodInfo, args);
    }

    private RpcClient tryGetRpcClient() {
        if (rpcClient == null) {
            tryInitRpcClient(true);
        }
        return rpcClient;
    }

    private synchronized void tryInitRpcClient(boolean throwError) {
        if (rpcClient != null) {
            return;
        }
        try {
            rpcClient = rpcServiceManager.lookup(rpcServiceInfo);
        } catch (Exception e) {
            if (throwError) {
                throw e;
            }
        }
    }
}
