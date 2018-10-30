package com.acupt.acuprpc.spring;

import com.acupt.acuprpc.core.RpcConf;
import com.acupt.acuprpc.protocol.grpc.GrpcServer;
import com.acupt.acuprpc.server.RpcServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liujie
 */
@Configuration
public class RpcConfiguration {

    @Bean
    public RpcConf rpcConf() {
        return new RpcConfBean();
    }

    @Bean
    public RpcServer rpcServer(RpcConf rpcConf) {
        return new GrpcServer(rpcConf);
    }

    @Bean
    public RpcServiceScanner rpcServiceScanner(RpcServer rpcServer) {
        return new RpcServiceScanner(rpcServer);
    }

    @Bean
    public RpcApplicationListener rpcApplicationListener(RpcServer rpcServer) {
        return new RpcApplicationListener(rpcServer);
    }

}
