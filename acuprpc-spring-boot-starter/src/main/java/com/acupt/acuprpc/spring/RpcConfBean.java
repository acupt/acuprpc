package com.acupt.acuprpc.spring;

import com.acupt.acuprpc.core.RpcConf;
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
    public void setEnvironment(Environment environment) {
        RelaxedPropertyResolver springPropertyResolver = new RelaxedPropertyResolver(environment, "spring.application.");
        String springAppName = springPropertyResolver.getProperty("name");
        if (StringUtils.hasText(springAppName)) {
            setAppName(springAppName);
        }
    }
}
