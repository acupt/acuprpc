package com.acupt.acuprpc.spring.actuator;

import com.acupt.acuprpc.server.RpcServer;
import com.acupt.acuprpc.spring.RpcServiceConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liujie
 */
@Configuration
public class ActuatorConfiguration {

    private boolean defaultSensitive = false;

    @Bean
    public RpcStatusEndPoint rpcInfoEndPoint(RpcServer rpcServer, RpcServiceConsumer rpcServiceConsumer) {
        RpcStatusEndPoint point = new RpcStatusEndPoint(rpcServer, rpcServiceConsumer);
        point.setSensitive(defaultSensitive);
        return point;
    }

    @Bean
    public RpcDumpEndPoint rpcDumpEndPoint(RpcServer rpcServer, RpcServiceConsumer rpcServiceConsumer) {
        RpcDumpEndPoint point = new RpcDumpEndPoint(rpcServer, rpcServiceConsumer);
        point.setSensitive(defaultSensitive);
        return point;
    }

}
