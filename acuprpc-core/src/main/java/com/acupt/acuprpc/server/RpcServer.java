package com.acupt.acuprpc.server;

import com.acupt.acuprpc.core.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liujie
 */
@Data
@Slf4j
public abstract class RpcServer {

    private RpcConf rpcConf;

    private Map<RpcServiceInfo, ServiceExecutor> serviceExecutorMap = new ConcurrentHashMap<>();

    public RpcServer(RpcConf rpcConf) {
        this.rpcConf = rpcConf;
    }

    protected abstract void startRpc();

    protected abstract void shutdownRpc();

    @PostConstruct
    public void start() {
        log.info(getClass().getSimpleName() + " starting");
        startRpc();
    }

    @PreDestroy
    public void shutdown() {
        shutdownRpc();
        log.info(getClass().getSimpleName() + " shutdown");
    }

    public void started() {
        log.info(getClass().getSimpleName() + " started");
    }

    public RpcResponse execute(RpcRequest rpcRequest) {
        ServiceExecutor executor = serviceExecutorMap.get(new RpcServiceInfo(rpcRequest.getServiceName()));
        if (executor == null) {
            return new RpcResponse(404, "service not found");
        }
        return new RpcResponse(executor.execute(rpcRequest));
    }

    public void registerService(RpcServiceInfo rpcServiceInfo,
                                Object serviceInstance,
                                Class<?> serviceInterface,
                                Map<String, MethodInfo> instanceMethodInfoMap) {
        Map<String, MethodInfo> methodInfoMap = new ConcurrentHashMap<>();
        for (Method method : serviceInterface.getDeclaredMethods()) {
            methodInfoMap.put(method.getName(), instanceMethodInfoMap.get(method.getName()));
        }
        ServiceExecutor executor = new ServiceExecutor(serviceInstance, methodInfoMap);
        serviceExecutorMap.put(rpcServiceInfo, executor);
    }


}
