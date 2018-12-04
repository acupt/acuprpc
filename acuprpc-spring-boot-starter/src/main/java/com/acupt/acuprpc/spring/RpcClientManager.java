package com.acupt.acuprpc.spring;

import com.acupt.acuprpc.client.RpcClient;
import com.acupt.acuprpc.core.NodeInfo;
import com.acupt.acuprpc.core.RpcInstance;
import com.acupt.acuprpc.core.RpcServiceInfo;
import com.acupt.acuprpc.exception.RpcException;
import com.acupt.acuprpc.exception.RpcNotFoundException;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.Application;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author liujie
 */
@Slf4j
public class RpcClientManager {

    private static final long RELOOKUP_PERIOD = 30000;
    private RpcInstance rpcInstance;

    private Map<RpcServiceInfo, RpcClient> rpcClientMap = new ConcurrentHashMap<>();

    private Random random = new Random();

    private ScheduledExecutorService relookupService = Executors.newSingleThreadScheduledExecutor();

    public RpcClientManager(RpcInstance rpcInstance) {
        this.rpcInstance = rpcInstance;
        relookupService.scheduleAtFixedRate(() -> {
            try {
                relookup();
                log.info("rpc relookup finish");
            } catch (Exception e) {
                log.error("rpc relookup error " + e.getMessage(), e);
            }
        }, RELOOKUP_PERIOD, RELOOKUP_PERIOD, TimeUnit.MILLISECONDS);
    }

    public RpcClient lookup(RpcServiceInfo rpcServiceInfo) {
        return rpcClientMap.computeIfAbsent(rpcServiceInfo, k -> {
            try {
                return rpcInstance.newRpcClient(selectNode(rpcServiceInfo));
            } catch (RpcNotFoundException e) {
                throw new RpcException(e);
            }
        });
    }

    @PreDestroy
    public void shutdown() {
        if (relookupService != null) {
            relookupService.shutdown();
        }
        rpcClientMap.forEach((k, v) -> {
            try {
                v.shutdown();
            } catch (Exception e) {
                log.error(String.join(" ", "shutdown error",
                        k.toString(), v.getNodeInfo().toString(), e.getMessage()), e);
            }
        });
    }

    private void relookup() {
        rpcClientMap.forEach((serviceInfo, client) -> {
            try {
                NodeInfo nodeInfo = selectNode(serviceInfo);
                if (!nodeInfo.equals(client.getNodeInfo())) {
                    NodeInfo oldNodeInfo = client.reconnect(nodeInfo);
                    log.info("reconnet {} {} -> {}", serviceInfo, oldNodeInfo, nodeInfo);
                }
            } catch (RpcNotFoundException e) {
                log.error("relookup " + e.getMessage(), e);
            }
        });
    }

    private NodeInfo selectNode(RpcServiceInfo serviceInfo) throws RpcNotFoundException {
        return selectNode(serviceInfo, null);
    }

    NodeInfo selectNode(RpcServiceInfo serviceInfo, NodeInfo exclude) throws RpcNotFoundException {
        Application application = rpcInstance.getEurekaClient().getApplication(serviceInfo.getAppName());
        if (application == null) {
            throw new RpcNotFoundException(String.format("service[%s] not found", serviceInfo.getAppName()));
        }
        List<NodeInfo> list = application.getInstances().stream()
                .map(this::convertInstanceInfo)
                .collect(Collectors.toList());
        if (exclude != null) {
            list.remove(exclude);
        }
        if (CollectionUtils.isEmpty(list)) {
            throw new RpcNotFoundException(String.format("service[%s] found no instance", serviceInfo.getAppName()));
        }
        return list.get(random.nextInt(list.size()));
    }

    private NodeInfo convertInstanceInfo(InstanceInfo instanceInfo) {
        return new NodeInfo(instanceInfo.getIPAddr(), instanceInfo.getPort());
    }
}
