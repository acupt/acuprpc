package com.acupt.acuprpc.core;

import lombok.Data;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

/**
 * @author liujie
 */
@Data
public class RpcConf {

    private String appName;

    private int port;
}
