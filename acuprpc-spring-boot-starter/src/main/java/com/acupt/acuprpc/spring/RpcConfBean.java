package com.acupt.acuprpc.spring;

import com.acupt.acuprpc.client.RpcClient;
import com.acupt.acuprpc.core.conf.RpcConf;
import com.acupt.acuprpc.server.RpcServer;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

/**
 * @author liujie
 */
@ConfigurationProperties(prefix = "acuprpc")
public class RpcConfBean extends RpcConf implements EnvironmentAware {

    @Override
    @SuppressWarnings("unchecked")
    public void setEnvironment(Environment environment) {
        RelaxedPropertyResolver springPropertyResolver = new RelaxedPropertyResolver(environment, "spring.application.");
        String springAppName = springPropertyResolver.getProperty("name");
        if (StringUtils.hasText(springAppName)) {
            setAppName(springAppName);
        }
        RelaxedPropertyResolver acuprpcPropertyResolver = new RelaxedPropertyResolver(environment, "acuprpc.");
        if (!acuprpcPropertyResolver.containsProperty("rpcServerClass")) {
            try {
                Class<? extends RpcServer> rpcServerClass = (Class<? extends RpcServer>) Class.forName(DEFAULT_RPC_SERVER_CLASS);
                setRpcServerClass(rpcServerClass);
            } catch (Exception e) {
                throw new RuntimeException("can't set property ${acuprpc.rpcServerClass} with default "
                        + DEFAULT_RPC_SERVER_CLASS + " " + e.getMessage(), e);
            }
        }
        if (!acuprpcPropertyResolver.containsProperty("rpcClientClass")) {
            try {
                Class<? extends RpcClient> rpcServerClass = (Class<? extends RpcClient>) Class.forName(DEFAULT_RPC_CLIENT_CLASS);
                setRpcClientClass(rpcServerClass);
            } catch (Exception e) {
                throw new RuntimeException("can't set property ${acuprpc.rpcClientClass} with default "
                        + DEFAULT_RPC_CLIENT_CLASS + " " + e.getMessage(), e);
            }
        }
    }
}
