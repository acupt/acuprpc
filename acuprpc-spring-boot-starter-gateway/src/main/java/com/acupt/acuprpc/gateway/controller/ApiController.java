package com.acupt.acuprpc.gateway.controller;

import com.acupt.acuprpc.client.RpcClient;
import com.acupt.acuprpc.core.RpcRequest;
import com.acupt.acuprpc.core.RpcServiceInfo;
import com.acupt.acuprpc.gateway.model.RpcRequestDTO;
import com.acupt.acuprpc.spring.RpcClientManager;
import com.acupt.acuprpc.util.JsonUtil;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liujie
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    /**
     * 动态调用rpc服务的关键，在acuprpc-spring-boot-starter中已经生成，可以随时引用
     */
    private RpcClientManager rpcClientManager;

    public ApiController(RpcClientManager rpcClientManager) {
        this.rpcClientManager = rpcClientManager;
    }

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public Object invoke(@RequestBody RpcRequestDTO requestDTO) {
        RpcServiceInfo serviceInfo = new RpcServiceInfo(requestDTO.getApp(), requestDTO.getService());
        RpcClient client = rpcClientManager.lookup(serviceInfo);//获取一个可以提供所需服务的连接
        RpcRequest request = new RpcRequest(requestDTO.getApp(), requestDTO.getService(), requestDTO.getMethod());
        if (requestDTO.getParameters() != null) {
            Map<String, String> map = new HashMap<>();
            requestDTO.getParameters().forEach((k, v) -> map.put(k, JsonUtil.toJson(v)));
            request.setNamedParameter(map);
        }
        return client.invoke(request);//调用服务获得返回的json字符串
    }
}
