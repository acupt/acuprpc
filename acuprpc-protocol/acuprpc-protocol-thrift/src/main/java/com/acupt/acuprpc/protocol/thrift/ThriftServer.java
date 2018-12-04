package com.acupt.acuprpc.protocol.thrift;

import com.acupt.acuprpc.core.RpcInstance;
import com.acupt.acuprpc.exception.RpcException;
import com.acupt.acuprpc.server.RpcServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TProcessor;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;

import java.util.concurrent.Executors;

/**
 * @author liujie
 */
@Slf4j
public class ThriftServer extends RpcServer {

    private static final int nThreads = 100;

    private TServer server;

    public ThriftServer(RpcInstance rpcInstance) {
        super(rpcInstance);
    }

    @Override
    protected void startRpc() {
        new Thread(() -> {
            TProcessor tprocessor = new com.acupt.acuprpc.protocol.thrift.proto.ThriftService.
                    Processor<com.acupt.acuprpc.protocol.thrift.proto.ThriftService.Iface>(new ThriftService(this));
            TServerTransport serverTransport = null;
            try {
                serverTransport = new TServerSocket(getRpcInstance().getRpcConf().getPort());
            } catch (TTransportException e) {
                throw new RpcException(e);
            }
            TThreadPoolServer.Args tArgs = new TThreadPoolServer.Args(serverTransport);
            tArgs.processor(tprocessor);
            tArgs.executorService(Executors.newFixedThreadPool(nThreads));
            server = new TThreadPoolServer(tArgs);
            server.serve();//阻塞
        }, "thrift-server-serve").start();
    }

    @Override
    protected void shutdownRpc() {
        if (server != null) {
            server.setShouldStop(true);
        }
    }
}
