package com.acupt.acuprpc.gateway.model;

import lombok.Data;

import java.util.Map;

/**
 * @author liujie
 */
@Data
public class RpcRequestDTO {
    private String app;
    private String service;
    private String method;
    private Map<String, Object> parameters;
}
