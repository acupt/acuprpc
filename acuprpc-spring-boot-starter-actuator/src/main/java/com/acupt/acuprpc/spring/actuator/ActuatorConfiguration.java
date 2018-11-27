package com.acupt.acuprpc.spring.actuator;

import com.acupt.acuprpc.server.RpcServer;
import com.acupt.acuprpc.server.filter.impl.RequestMonitorFilter;
import com.acupt.acuprpc.spring.actuator.endpoint.RpcStatEndPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liujie
 */
@Configuration
public class ActuatorConfiguration {

    private boolean defaultSensitive = false;

    @Bean
    public RequestMonitorFilter requestMonitorFilter(RpcServer rpcServer) {
        return rpcServer.addFilter(new RequestMonitorFilter());
    }

    @Bean
    public RpcStatEndPoint rpcInfoEndPoint(RequestMonitorFilter requestMonitorFilter) {
        RpcStatEndPoint point = new RpcStatEndPoint(requestMonitorFilter);
        point.setSensitive(defaultSensitive);
        return point;
    }

}
