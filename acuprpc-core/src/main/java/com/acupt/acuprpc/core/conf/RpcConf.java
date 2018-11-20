package com.acupt.acuprpc.core.conf;

import com.acupt.acuprpc.client.RpcClient;
import com.acupt.acuprpc.server.RpcServer;
import lombok.Data;

/**
 * @author liujie
 */
@Data
public class RpcConf {

    protected static final String DEFAULT_RPC_SERVER_CLASS = "com.acupt.acuprpc.protocol.grpc.GrpcServer";
    protected static final String DEFAULT_RPC_CLIENT_CLASS = "com.acupt.acuprpc.protocol.grpc.GrpcClient";

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

    /**
     * 通信协议，对外提供服务的类
     */
    private Class<? extends RpcServer> rpcServerClass;

    /**
     * 通信协议，请求服务的类
     */
    private Class<? extends RpcClient> rpcClientClass;

}
