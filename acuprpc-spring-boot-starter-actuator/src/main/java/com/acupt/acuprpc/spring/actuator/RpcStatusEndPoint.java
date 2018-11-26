package com.acupt.acuprpc.spring.actuator;

import com.acupt.acuprpc.server.RpcServer;
import com.acupt.acuprpc.spring.RpcServiceConsumer;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * @author liujie
 */
public class RpcStatusEndPoint extends AbstractRpcEndPoint<RpcStatusEndPoint.RpcStatus> {

    private RpcServer server;

    private RpcServiceConsumer consumer;

    public RpcStatusEndPoint(RpcServer server, RpcServiceConsumer consumer) {
        super("status");
        this.server = server;
        this.consumer = consumer;
    }

    @Override
    public RpcStatus invoke() {
        return RpcStatus.builder()
                .server(new ServerStatus(server))
                .client(new ClientStatus(consumer))
                .build();
    }

    @Data
    @Builder
    public static class RpcStatus {
        private ServerStatus server;
        private ClientStatus client;

        public boolean isActive() {
            return server.getActiveNum() > 0 || client.getActiveNum() > 0;
        }
    }

    @Data
    public static class ServerStatus {
        private long activeNum;
        private Map<String, Map<String, Long>> invokingMap;

        ServerStatus(RpcServer rpcServer) {
//            invokingMap = rpcServer.getInvokingMap();
            activeNum = invokingMap.values().stream()
                    .mapToLong(map -> map.values().stream().mapToLong(c -> c).sum()).sum();
        }
    }

    @Data
    public static class ClientStatus {
        private long activeNum;
        private Map<String, Map<String, Long>> requestingMap;

        ClientStatus(RpcServiceConsumer consumer) {
//            this.requestingMap = consumer.getRpcServiceManager().getRequestingMap();
            this.activeNum = requestingMap.values().stream()
                    .mapToLong(map -> map.values().stream().mapToLong(c -> c).sum()).sum();
        }
    }
}
