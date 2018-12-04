package com.acupt.acuprpc.protocol.thrift;

import com.acupt.acuprpc.core.RpcRequest;
import com.acupt.acuprpc.core.RpcResponse;
import com.acupt.acuprpc.protocol.thrift.proto.InvokeRequest;
import com.acupt.acuprpc.protocol.thrift.proto.InvokeResponse;
import com.acupt.acuprpc.server.RpcServer;

/**
 * @author liujie
 */
public class ThriftService implements com.acupt.acuprpc.protocol.thrift.proto.ThriftService.Iface {

    private RpcServer rpcServer;

    public ThriftService(RpcServer rpcServer) {
        this.rpcServer = rpcServer;
    }

    @Override
    public InvokeResponse invokeMethod(InvokeRequest invokeRequest) {
        RpcRequest rpcRequest = new RpcRequest(
                invokeRequest.getAppName(),
                invokeRequest.getServiceName(),
                invokeRequest.getMethodName(),
                invokeRequest.getOrderedParameter(),
                invokeRequest.getNamedParameter());
        RpcResponse rpcResponse = rpcServer.execute(rpcRequest);
        InvokeResponse response = new InvokeResponse();
        response.setCode(rpcResponse.getCode());
        response.setMessage(rpcResponse.getMessage());
        response.setResult(rpcResponse.jsonResult());
        return response;
    }
}
