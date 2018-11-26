package com.acupt.acuprpc.spring.actuator;

import com.acupt.acuprpc.server.RpcServer;
import com.acupt.acuprpc.spring.RpcServiceConsumer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liujie
 */
public class RpcDumpEndPoint extends AbstractRpcEndPoint<Map<String, Object>> {

    private RpcServer server;

    private RpcServiceConsumer consumer;

    public RpcDumpEndPoint(RpcServer server, RpcServiceConsumer consumer) {
        super("dump");
        this.server = server;
        this.consumer = consumer;
    }

    @Override
    public Map<String, Object> invoke() {
        Map<String, Object> map = new HashMap<>();
        map.put("server", server);
        map.put("consumer", consumer);
        return map;
    }

}
