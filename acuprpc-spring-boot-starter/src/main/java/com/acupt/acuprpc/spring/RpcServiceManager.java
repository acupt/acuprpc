package com.acupt.acuprpc.spring;

import com.acupt.acuprpc.client.RpcClient;
import com.acupt.acuprpc.core.NodeInfo;
import com.acupt.acuprpc.core.RpcInstance;
import com.acupt.acuprpc.core.RpcServiceInfo;
import com.acupt.acuprpc.exception.RpcException;
import com.acupt.acuprpc.protocol.grpc.GrpcClient;
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
public class RpcServiceManager {

    private RpcInstance rpcInstance;

    private Map<RpcServiceInfo, RpcClient> rpcClientMap = new ConcurrentHashMap<>();

    private Random random = new Random();

    public RpcServiceManager(RpcInstance rpcInstance) {
        this.rpcInstance = rpcInstance;
        new Thread(() -> {
            while (true) {
                try {
                    relookup();
                } catch (Exception e) {
                    log.error("rpc relookup error " + e.getMessage(), e);
                }
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    log.error("rpc relookup sleep error " + e.getMessage(), e);
                }
            }
        }).start();
    }

    public RpcClient lookup(RpcServiceInfo rpcServiceInfo) {
        return rpcClientMap.computeIfAbsent(rpcServiceInfo, k -> {
            Application application = rpcInstance.getEurekaClient().getApplication(k.getAppName());
            if (application == null) {
                throw new RpcException(String.format("service[%s] not found", k.getAppName()));
            }
            List<InstanceInfo> list = application.getInstances();
            if (CollectionUtils.isEmpty(list)) {
                throw new RpcException(String.format("service[%s] found no instance", k.getAppName()));
            }
            InstanceInfo instanceInfo = list.get(random.nextInt(list.size()));
            NodeInfo nodeInfo = new NodeInfo(instanceInfo.getIPAddr(), instanceInfo.getPort());
            return new GrpcClient(nodeInfo);
        });
    }

    private void relookup() {
        rpcClientMap.forEach((serviceInfo, client) -> {
            Application application = rpcInstance.getEurekaClient().getApplication(serviceInfo.getAppName());
            if (application == null) {
                log.error("service[{}] not found", serviceInfo.getAppName());
                return;
            }
            List<InstanceInfo> list = application.getInstances();
            if (CollectionUtils.isEmpty(list)) {
                log.error("service[{}] found no instance", serviceInfo.getAppName());
                return;
            }
            InstanceInfo instanceInfo = list.get(random.nextInt(list.size()));
            NodeInfo nodeInfo = new NodeInfo(instanceInfo.getIPAddr(), instanceInfo.getPort());
            if (!nodeInfo.equals(client.getNodeInfo())) {
                NodeInfo oldNodeInfo = client.reconnect(nodeInfo);
                log.info("reconnet {} {} -> {}", serviceInfo, oldNodeInfo, nodeInfo);
            }
        });
    }
}
