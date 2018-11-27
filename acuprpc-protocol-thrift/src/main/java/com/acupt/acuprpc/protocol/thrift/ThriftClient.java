package com.acupt.acuprpc.protocol.thrift;


import com.acupt.acuprpc.client.RpcClient;
import com.acupt.acuprpc.core.NodeInfo;
import com.acupt.acuprpc.core.RpcCode;
import com.acupt.acuprpc.core.RpcRequest;
import com.acupt.acuprpc.exception.HttpStatusException;
import com.acupt.acuprpc.exception.RpcException;
import com.acupt.acuprpc.protocol.thrift.proto.InvokeRequest;
import com.acupt.acuprpc.protocol.thrift.proto.InvokeResponse;
import com.acupt.acuprpc.protocol.thrift.proto.ThriftService;
import lombok.SneakyThrows;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author liujie
 */
public class ThriftClient extends RpcClient implements RpcCode {

    private AtomicReference<ThriftService.Client> clientRef;

    public ThriftClient(NodeInfo nodeInfo) {
        super(nodeInfo);
        clientRef = new AtomicReference<>(getClient(nodeInfo));
    }

    //todo client线程不安全，使用连接池管理
    @Override
    @SneakyThrows
    protected synchronized String remoteInvoke(RpcRequest rpcRequest) {
        InvokeRequest request = new InvokeRequest();
        request.setAppName(rpcRequest.getAppName());
        request.setServiceName(rpcRequest.getServiceName());
        request.setMethodName(rpcRequest.getMethodName());
        request.setOrderedParameter(rpcRequest.getOrderedParameter());
        InvokeResponse response = clientRef.get().invokeMethod(request);
        if (response.getCode() != SUCCESS) {
            throw new HttpStatusException(response.getCode(), response.getMessage());
        }
        return response.getResult();
    }

    @Override
    protected NodeInfo reconnectRpc(NodeInfo nodeInfo) {
        ThriftService.Client old = clientRef.getAndUpdate(t -> getClient(nodeInfo));
        close(old);
        NodeInfo oldInfo = getNodeInfo();
        setNodeInfo(nodeInfo);
        return oldInfo;
    }

    @Override
    public void shutdownRpc() {
        close(clientRef.get());
    }

    private ThriftService.Client getClient(NodeInfo nodeInfo) {
        TTransport transport = null;
        try {
            transport = new TSocket(nodeInfo.getIp(), nodeInfo.getPort());
            TProtocol protocol = new TBinaryProtocol(transport);
            ThriftService.Client client = new ThriftService.Client(protocol);
            transport.open();
            return client;
        } catch (Exception e) {
            if (transport != null) {
                transport.close();
            }
            throw new RpcException(e);
        }
    }

    private void close(ThriftService.Client client) {
        if (client != null) {
            if (client.getInputProtocol().getTransport() == client.getOutputProtocol().getTransport()) {
                client.getInputProtocol().getTransport().close();
            } else {
                client.getInputProtocol().getTransport().close();
                client.getOutputProtocol().getTransport().close();
            }
        }
    }
}
