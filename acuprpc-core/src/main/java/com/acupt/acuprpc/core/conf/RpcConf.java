package com.acupt.acuprpc.core.conf;

import lombok.Data;

/**
 * @author liujie
 */
@Data
public class RpcConf {

    /**
     * 应用群组，一般为环境
     */
    private String appGroup;

    /**
     * 应用名，默认为spring应用名: spring.application.name
     */
    private String appName;

    /**
     * 对外开放的rpc调用端口号
     */
    private int port;

    /**
     * 服务注册/发现中心地址
     */
    private String discoveryServiceUrl;

    /**
     * 是否注册应用到eureka
     */
    private boolean registerWithDiscovery = true;

}
