package com.acupt.acuprpc.core;

import com.acupt.acuprpc.core.conf.RpcConf;
import com.acupt.acuprpc.core.conf.RpcEurekaClientConfig;
import com.acupt.acuprpc.core.conf.RpcEurekaInstanceConfig;
import com.acupt.acuprpc.util.IpUtil;
import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.providers.EurekaConfigBasedInstanceInfoProvider;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;
import lombok.Getter;

/**
 * 在服务中心注册的实例
 *
 * @author liujie
 */
@Getter
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
}
