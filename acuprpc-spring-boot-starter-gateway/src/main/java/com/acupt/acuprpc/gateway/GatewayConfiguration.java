package com.acupt.acuprpc.gateway;

import com.acupt.acuprpc.gateway.controller.ApiController;
import com.acupt.acuprpc.spring.RpcClientManager;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liujie
 */
@Configuration
//@ConfigurationProperties(prefix = "acuprpc.gateway")
@Data
public class GatewayConfiguration {

    @Bean
    public ApiController apiController(RpcClientManager rpcClientManager) {
        return new ApiController(rpcClientManager);
    }
}
