package com.acupt.acuprpc.spring;

import com.acupt.acuprpc.client.RpcClient;
import com.acupt.acuprpc.core.NodeInfo;
import com.acupt.acuprpc.core.RpcInstance;
import com.acupt.acuprpc.core.RpcServiceInfo;
import com.acupt.acuprpc.exception.RpcException;
import com.acupt.acuprpc.protocol.grpc.GrpcClient;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.Application;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liujie
 */
public class RpcServiceManager {

    private RpcInstance rpcInstance;

    //todo 重新均衡
    private Map<RpcServiceInfo, RpcClient> rpcClientMap = new ConcurrentHashMap<>();

    private Random random = new Random();

    public RpcServiceManager(RpcInstance rpcInstance) {
        this.rpcInstance = rpcInstance;
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
}
