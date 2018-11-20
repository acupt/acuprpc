package com.acupt.acuprpc.protocol.thrift;

import com.acupt.acuprpc.core.RpcInstance;
import com.acupt.acuprpc.exception.RpcException;
import com.acupt.acuprpc.server.RpcServer;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;

/**
 * @author liujie
 */
public class ThriftServer extends RpcServer {

    private TServer server;

    public ThriftServer(RpcInstance rpcInstance) {
        super(rpcInstance);
    }

    @Override
    protected void startRpc() {
        new Thread(() -> {
            TProcessor tprocessor = new com.acupt.acuprpc.protocol.thrift.proto.ThriftService.
                    Processor<com.acupt.acuprpc.protocol.thrift.proto.ThriftService.Iface>(new ThriftService(this));
            TServerSocket serverTransport = null;
            try {
                serverTransport = new TServerSocket(getRpcInstance().getRpcConf().getPort());
            } catch (TTransportException e) {
                throw new RpcException(e);
            }
            TServer.Args tArgs = new TServer.Args(serverTransport);
            tArgs.processor(tprocessor);
            tArgs.protocolFactory(new TBinaryProtocol.Factory());
            server = new TSimpleServer(tArgs);
            server.serve();//阻塞
        }).start();
    }

    @Override
    protected void shutdownRpc() {
        if (server != null) {
            server.setShouldStop(true);
        }
    }
}
