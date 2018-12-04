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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;

/**
 * @author liujie
 */
@Slf4j
public class ThriftClient extends RpcClient implements RpcCode {

    private ObjectPool<ThriftService.Client> clientPool;

    public ThriftClient(NodeInfo nodeInfo) {
        super(nodeInfo);
        GenericObjectPool<ThriftService.Client> pool = new GenericObjectPool<>(new PoolableObjectFactory<ThriftService.Client>() {
            @Override
            public ThriftService.Client makeObject() throws Exception {
                return createClient();
            }

            @Override
            public void destroyObject(ThriftService.Client obj) throws Exception {
                close(obj);
            }

            @Override
            public boolean validateObject(ThriftService.Client client) {
                if (!client.getInputProtocol().getTransport().isOpen()) {
                    return false;
                }
                return client.getOutputProtocol().getTransport().isOpen();
            }

            @Override
            public void activateObject(ThriftService.Client obj) throws Exception {

            }

            @Override
            public void passivateObject(ThriftService.Client obj) throws Exception {

            }
        });
        pool.setMaxActive(8);
        pool.setMaxIdle(2);
        pool.setMinIdle(1);
        clientPool = pool;
    }

    @Override
    protected String remoteInvoke(RpcRequest rpcRequest) {
        InvokeRequest request = new InvokeRequest();
        request.setAppName(rpcRequest.getAppName());
        request.setServiceName(rpcRequest.getServiceName());
        request.setMethodName(rpcRequest.getMethodName());
        request.setOrderedParameter(rpcRequest.getOrderedParameter());
        request.setNamedParameter(rpcRequest.getNamedParameter());
        ThriftService.Client client = null;
        try {
            client = getClient();
            InvokeResponse response = client.invokeMethod(request);
            if (response.getCode() != SUCCESS) {
                throw new HttpStatusException(response.getCode(), response.getMessage());
            }
            return response.getResult();
        } catch (Exception e) {
            throw new RpcException(e);
        } finally {
            returnClient(client);
        }
    }

    @Override
    @SneakyThrows
    protected NodeInfo reconnectRpc(NodeInfo nodeInfo) {
        clientPool.clear();
        return setNodeInfo(nodeInfo);
    }

    @Override
    @SneakyThrows
    public void shutdownRpc() {
        clientPool.close();
    }

    private ThriftService.Client createClient() {
        NodeInfo nodeInfo = getNodeInfo();
        TSocket transport = null;
        log.info("create ThriftService.Client " + nodeInfo);
        try {
            transport = new TSocket(nodeInfo.getIp(), nodeInfo.getPort());
            transport.setConnectTimeout(getTimeout() * 1000);
            transport.setSocketTimeout(getTimeout() * 1000);
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
        log.info("close ThriftService.Client " + client);
        if (client != null) {
            if (client.getInputProtocol().getTransport() == client.getOutputProtocol().getTransport()) {
                client.getInputProtocol().getTransport().close();
            } else {
                client.getInputProtocol().getTransport().close();
                client.getOutputProtocol().getTransport().close();
            }
        }
    }

    @SneakyThrows
    private ThriftService.Client getClient() {
        return clientPool.borrowObject();
    }

    @SneakyThrows
    private void returnClient(ThriftService.Client client) {
        if (client != null) {
            clientPool.returnObject(client);
        }
    }
}
