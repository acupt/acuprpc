package com.acupt.acuprpc.spring.actuator;

import com.acupt.acuprpc.server.RpcServer;
import com.acupt.acuprpc.server.filter.impl.MonitorFilter;
import com.acupt.acuprpc.server.filter.impl.RejectFilter;
import com.acupt.acuprpc.spring.actuator.endpoint.AbstractRpcEndpoint;
import com.acupt.acuprpc.spring.actuator.endpoint.ReflectEndpointMvcAdapter;
import com.acupt.acuprpc.spring.actuator.endpoint.RpcEndpoint;
import com.acupt.acuprpc.spring.actuator.endpoint.RpcStatEndpoint;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liujie
 */
@Configuration
@ConfigurationProperties(prefix = "acuprpc.endpoints")
@Data
public class ActuatorConfiguration {

    private boolean sensitive = true;

    private String ipWhiteList;

    @Bean
    public MonitorFilter monitorFilter(RpcServer rpcServer) {
        return rpcServer.addFilter(new MonitorFilter());
    }

    @Bean
    public RejectFilter rejectFilter(RpcServer rpcServer) {
        return rpcServer.addFilter(new RejectFilter());
    }

    @Bean
    public RpcStatEndpoint rpcStatEndpoint(MonitorFilter requestMonitorFilter) {
        return process(new RpcStatEndpoint(requestMonitorFilter));
    }

    @Bean
    public ReflectEndpointMvcAdapter rpcEndpoint(RejectFilter rejectFilter) {
        return new ReflectEndpointMvcAdapter(process(new RpcEndpoint(rejectFilter)), ipWhiteList);
    }

    private <T extends AbstractRpcEndpoint<?>> T process(T endpoint) {
        endpoint.setSensitive(sensitive);
        return endpoint;
    }

}
