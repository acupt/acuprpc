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

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liujie
 */
@Slf4j
public class RpcClientManager {

    private RpcInstance rpcInstance;

    private Map<RpcServiceInfo, RpcClient> rpcClientMap = new ConcurrentHashMap<>();

    private Random random = new Random();

    public RpcClientManager(RpcInstance rpcInstance) {
        this.rpcInstance = rpcInstance;
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    log.error("rpc relookup sleep error " + e.getMessage(), e);
                }
                try {
                    relookup();
                    log.info("rpc relookup finish");
                } catch (Exception e) {
                    log.error("rpc relookup error " + e.getMessage(), e);
                }
            }
        }).start();
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

    private void relookup() {
        rpcClientMap.forEach((serviceInfo, client) -> {
            try {
                NodeInfo nodeInfo = selectNode(serviceInfo);
                if (!nodeInfo.equals(client.getNodeInfo())) {
                    NodeInfo oldNodeInfo = client.reconnect(nodeInfo);
                    log.info("reconnet {} {} -> {}", serviceInfo, oldNodeInfo, nodeInfo);
                }
            } catch (RpcNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    public NodeInfo selectNode(RpcServiceInfo serviceInfo) throws RpcNotFoundException {
        Application application = rpcInstance.getEurekaClient().getApplication(serviceInfo.getAppName());
        if (application == null) {
            throw new RpcNotFoundException(String.format("service[%s] not found", serviceInfo.getAppName()));
        }
        List<InstanceInfo> list = application.getInstances();
        if (CollectionUtils.isEmpty(list)) {
            throw new RpcNotFoundException(String.format("service[%s] found no instance", serviceInfo.getAppName()));
        }
        InstanceInfo instanceInfo = list.get(random.nextInt(list.size()));
        return new NodeInfo(instanceInfo.getIPAddr(), instanceInfo.getPort());
    }
}
