package com.acupt.acuprpc.protocol.grpc;

import com.acupt.acuprpc.core.RpcInstance;
import com.acupt.acuprpc.server.RpcServer;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.SneakyThrows;

/**
 * @author liujie
 */
public class GrpcServer extends RpcServer {

    private Server server;

    public GrpcServer(RpcInstance rpcInstance) {
        super(rpcInstance);
    }


    @SneakyThrows
    protected void startRpc() {
        server = ServerBuilder
                .forPort(getRpcInstance().getRpcConf().getPort())
                .addService(new GrpcService(this))
                .build().start();
    }

    protected void shutdownRpc() {
        if (server != null) {
            server.shutdown();
        }
    }
}
