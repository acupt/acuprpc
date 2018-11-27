package com.acupt.acuprpc.spring.actuator;

import com.acupt.acuprpc.server.RpcServer;
import com.acupt.acuprpc.server.filter.impl.MonitorFilter;
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
    public MonitorFilter monitorFilter(RpcServer rpcServer) {
        return rpcServer.addFilter(new MonitorFilter());
    }

    @Bean
    public RpcStatEndPoint rpcInfoEndPoint(MonitorFilter requestMonitorFilter) {
        RpcStatEndPoint point = new RpcStatEndPoint(requestMonitorFilter);
        point.setSensitive(defaultSensitive);
        return point;
    }

}
