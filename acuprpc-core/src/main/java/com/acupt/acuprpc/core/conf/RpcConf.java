package com.acupt.acuprpc.core.conf;

import lombok.Data;

/**
 * @author liujie
 */
@Data
public class RpcConf {

    private String appGroup;
    private String appName;
    private int port;
    private String discoveryServiceUrl;
    private boolean registerWithDiscovery = true;//是否注册应用到eureka
}
