package com.acupt.acuprpc.spring;

import com.acupt.acuprpc.core.RpcInstance;
import com.acupt.acuprpc.core.conf.RpcConf;
import com.acupt.acuprpc.server.RpcServer;
import lombok.SneakyThrows;
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
    public RpcInstance rpcInstance(RpcConf rpcConf) {
        return new RpcInstance(rpcConf);
    }

    @Bean
    @SneakyThrows
    public RpcServer rpcServer(RpcInstance rpcInstance) {
        return rpcInstance.newRpcServer();
    }

    @Bean
    public RpcClientManager rpcClientManager(RpcInstance rpcInstance) {
        return new RpcClientManager(rpcInstance);
    }

    @Bean
    public RpcServiceConsumer rpcServiceConsumer(RpcClientManager rpcClientManager) {
        return new RpcServiceConsumer(rpcClientManager);
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
