package com.acupt.acuprpc.spring;

import com.acupt.acuprpc.client.RpcClient;
import com.acupt.acuprpc.core.NodeInfo;
import com.acupt.acuprpc.core.RpcCode;
import com.acupt.acuprpc.core.RpcRequest;
import com.acupt.acuprpc.core.RpcServiceInfo;
import com.acupt.acuprpc.exception.HttpStatusException;
import com.acupt.acuprpc.exception.RpcNotFoundException;
import com.acupt.acuprpc.util.JsonUtil;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author liujie
 */
@Slf4j
public class RpcInvocationHandler implements InvocationHandler, RpcCode {

    private RpcServiceInfo rpcServiceInfo;

    private RpcClientManager rpcClientManager;

    private RpcClient rpcClient;

    public RpcInvocationHandler(RpcServiceInfo rpcServiceInfo, RpcClientManager rpcClientManager) {
        this.rpcServiceInfo = rpcServiceInfo;
        this.rpcClientManager = rpcClientManager;
        tryInitRpcClient(false);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        if ("toString".equals(method.getName()) && (args == null || args.length == 0)) {
            return rpcServiceInfo.toString();//debug时老是被ide调用然后抛异常，很烦
        }
        RpcRequest rpcRequest = new RpcRequest(rpcServiceInfo.getAppName(), rpcServiceInfo.getServiceName(), method.getName());
        if (args != null && args.length > 0) {
            rpcRequest.setOrderedParameter(Arrays.stream(args).map(JsonUtil::toJson).collect(Collectors.toList()));
        }
        int n = 3;
        int i = 0;
        RpcClient client = null;
        while (i++ < n) {
            try {
                client = getRpcClient();
                String res = client.invoke(rpcRequest);
                return JsonUtil.fromJson(res, TypeFactory.defaultInstance().constructType(method.getGenericReturnType()));
            } catch (Exception e) {
                if (client == null) {
                    throw e;
                }
                boolean rediscover = needRediscover(e) && i < n;
                log.error("invoke {}/{} {} {} error={} msg={} rediscover={}",
                        i, n, rpcRequest.getKey(), client.getNodeInfo(), e.getClass().getName(), e.getMessage(), rediscover);
                if (rediscover) {
                    try {
                        NodeInfo nodeInfo = rpcClientManager.selectNode(rpcServiceInfo);
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
            rpcClient = rpcClientManager.lookup(rpcServiceInfo);
        } catch (Exception e) {
            if (throwError) {
                throw e;
            }
        }
    }

    private boolean needRediscover(Throwable e) {
        while (e != null) {
            if (e instanceof HttpStatusException) {
                if (((HttpStatusException) e).getStatus() == NOT_AVAILABLE) {
                    return true;
                }
            } else if (e instanceof ConnectException) {
                return true;
            }
            e = e.getCause();
        }
        return false;
    }
}
