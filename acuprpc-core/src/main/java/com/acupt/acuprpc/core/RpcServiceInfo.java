package com.acupt.acuprpc.core;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author liujie
 */
@Data
@AllArgsConstructor
public class RpcServiceInfo {
    private String appName;
    private String serviceName;
}
