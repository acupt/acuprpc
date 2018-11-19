package com.acupt.acuprpc.spring;

import com.acupt.acuprpc.client.RpcClient;
import com.acupt.acuprpc.core.NodeInfo;
import com.acupt.acuprpc.core.RpcMethodInfo;
import com.acupt.acuprpc.core.RpcServiceInfo;
import com.acupt.acuprpc.exception.RpcNotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.ConnectException;

/**
 * @author liujie
 */
@Slf4j
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
    public Object invoke(Object proxy, Method method, Object[] args) {
        if ("toString".equals(method.getName()) && (args == null || args.length == 0)) {
            return rpcServiceInfo.toString();//debug时老是被ide调用然后抛异常，很烦
        }
        RpcMethodInfo methodInfo = new RpcMethodInfo(rpcServiceInfo, method.getName(), method.getGenericReturnType());
        int n = 3;
        RpcClient client = null;
        for (int i = 0; i < n; i++) {
            try {
                client = getRpcClient();
                return client.invoke(methodInfo, args);
            } catch (Exception e) {
                assert client != null;
                boolean rediscover = needRediscover(e);
                log.error("invoke {}/{} {} {} error={} msg={} rediscover={}",
                        i + 1, n, methodInfo, client.getNodeInfo(), e.getClass().getName(), e.getMessage(), rediscover);
                if (rediscover) {
                    try {
                        NodeInfo nodeInfo = rpcServiceManager.selectNode(rpcServiceInfo);
                        client.reconnect(nodeInfo);
                        continue;
                    } catch (RpcNotFoundException e1) {
                        e.addSuppressed(e1);
                    }
                }
                throw e;
            }
        }
        throw new RuntimeException("invoke error");
    }

    private RpcClient getRpcClient() {
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

    private boolean needRediscover(Throwable e) {
        while (e != null) {
            if (e instanceof ConnectException) {
                return true;
            }
            e = e.getCause();
        }
        return false;
    }
}
