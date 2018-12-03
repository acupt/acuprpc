package com.acupt.acuprpc.core;

import com.acupt.acuprpc.client.RpcClient;
import com.acupt.acuprpc.core.conf.RpcConf;
import com.acupt.acuprpc.core.conf.RpcEurekaClientConfig;
import com.acupt.acuprpc.core.conf.RpcEurekaInstanceConfig;
import com.acupt.acuprpc.server.RpcServer;
import com.acupt.acuprpc.util.IpUtil;
import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.providers.EurekaConfigBasedInstanceInfoProvider;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * 在服务中心注册的实例
 *
 * @author liujie
 */
@Getter
@Slf4j
public class RpcInstance {

    private EurekaClient eurekaClient;

    private ApplicationInfoManager applicationInfoManager;

    private RpcConf rpcConf;

    public RpcInstance(RpcConf rpcConf) {
        RpcEurekaInstanceConfig instanceConfig = new RpcEurekaInstanceConfig();

        instanceConfig.setAppGroupName(rpcConf.getAppGroup());
        instanceConfig.setAppname(rpcConf.getAppName());
        instanceConfig.setNonSecurePort(rpcConf.getPort());
        instanceConfig.setIpAddress(IpUtil.INTRANET_IP);
        instanceConfig.setHostname(IpUtil.HOSTNAME);

        RpcEurekaClientConfig clientConfig = new RpcEurekaClientConfig();
        clientConfig.getServiceUrl().put("default", rpcConf.getDiscoveryServiceUrl());
        clientConfig.setRegisterWithEureka(rpcConf.isRegisterWithDiscovery());

        InstanceInfo instanceInfo = new EurekaConfigBasedInstanceInfoProvider(instanceConfig).get();
        this.applicationInfoManager = new ApplicationInfoManager(instanceConfig, instanceInfo);
        this.eurekaClient = new DiscoveryClient(applicationInfoManager, clientConfig);
        this.rpcConf = rpcConf;
        log.info("protocol server -> " + rpcConf.getRpcServerClass());
        log.info("protocol client -> " + rpcConf.getRpcClientClass());
    }

    public void start() {
        applicationInfoManager.setInstanceStatus(InstanceInfo.InstanceStatus.STARTING);
    }

    public void started() {
        applicationInfoManager.setInstanceStatus(InstanceInfo.InstanceStatus.UP);
    }

    public void shutdown() {
        applicationInfoManager.setInstanceStatus(InstanceInfo.InstanceStatus.DOWN);
        eurekaClient.shutdown();
    }

    /**
     * 创建一个rpc server，根据配置的调用方式（实现类）生成对象
     */
    @SneakyThrows
    public RpcServer newRpcServer() {
        return rpcConf.getRpcServerClass().getConstructor(RpcInstance.class).newInstance(this);
    }

    /**
     * 创建一个rpc client，根据配置的调用方式（实现类）生成对象
     */
    @SneakyThrows
    public RpcClient newRpcClient(NodeInfo nodeInfo) {
        return rpcConf.getRpcClientClass().getConstructor(NodeInfo.class).newInstance(nodeInfo);
    }
}
