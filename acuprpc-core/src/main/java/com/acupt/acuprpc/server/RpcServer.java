package com.acupt.acuprpc.server;

import com.acupt.acuprpc.core.*;
import com.acupt.acuprpc.server.filter.RpcFilter;
import com.acupt.acuprpc.server.filter.RpcFilterChain;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liujie
 */
@Slf4j
public abstract class RpcServer implements RpcCode {

    private Map<RpcServiceInfo, RpcServiceExecutor> serviceExecutorMap = new ConcurrentHashMap<>();

    private RpcInstance rpcInstance;

    private List<RpcFilter> filters = new ArrayList<>(0);

    public RpcServer(RpcInstance rpcInstance) {
        this.rpcInstance = rpcInstance;
    }

    protected abstract void startRpc();

    protected abstract void shutdownRpc();

    @PostConstruct
    public void start() {
        log.info("starting {}", getClass().getSimpleName());
        rpcInstance.start();
        startRpc();
    }

    @PreDestroy
    public void shutdown() {
        rpcInstance.shutdown();
        shutdownRpc();
        log.info("shutdown {}", getClass().getSimpleName());
    }

    public void started() {
        rpcInstance.started();
        log.info("started {}", getClass().getSimpleName());
    }

    public RpcResponse execute(RpcRequest rpcRequest) {
        RpcServiceInfo rpcServiceInfo = new RpcServiceInfo(rpcRequest.getAppName(), rpcRequest.getServiceName());
        RpcFilterChain chain = new RpcFilterChain(filters, rpcServiceInfo, serviceExecutorMap.get(rpcServiceInfo));
        RpcResponse rpcResponse = new RpcResponse();
        try {
            chain.doFilter(rpcRequest, rpcResponse);
        } catch (Exception e) {
            rpcResponse.error(e);
        }
        return rpcResponse;
    }

    public void registerService(RpcServiceInfo rpcServiceInfo,
                                Object serviceInstance,
                                Class<?> serviceInterface,
                                Map<String, MethodInfo> instanceMethodInfoMap) {
        Map<String, MethodInfo> methodInfoMap = new ConcurrentHashMap<>();
        for (Method method : serviceInterface.getDeclaredMethods()) {
            methodInfoMap.put(method.getName(), instanceMethodInfoMap.get(method.getName()));
        }
        RpcServiceExecutor executor = new RpcServiceExecutor(serviceInstance, methodInfoMap);
        serviceExecutorMap.put(rpcServiceInfo, executor);
    }

    public <T extends RpcFilter> T addFilter(T filter) {
        filters.add(filter);
        return filter;
    }

    public RpcInstance getRpcInstance() {
        return rpcInstance;
    }
}
