package com.acupt.acuprpc.protocol.grpc;

import com.acupt.acuprpc.core.RpcRequest;
import com.acupt.acuprpc.core.RpcResponse;
import com.acupt.acuprpc.server.RpcServer;
import com.acupt.acuprpc.protocol.grpc.proto.GrpcServiceGrpc;
import com.acupt.acuprpc.protocol.grpc.proto.InvokeRequest;
import com.acupt.acuprpc.protocol.grpc.proto.InvokeResponse;
import io.grpc.stub.StreamObserver;

/**
 * @author liujie
 */
public class GrpcService extends GrpcServiceGrpc.GrpcServiceImplBase {

    private RpcServer rpcServer;

    public GrpcService(RpcServer rpcServer) {
        this.rpcServer = rpcServer;
    }

    @Override
    public void invokeMethod(InvokeRequest request, StreamObserver<InvokeResponse> responseObserver) {
        RpcRequest rpcRequest = new RpcRequest(
                request.getAppName(),
                request.getServiceName(),
                request.getMethodName(),
                request.getOrderedParameterList());
        RpcResponse rpcResponse = rpcServer.execute(rpcRequest);
        InvokeResponse response = InvokeResponse.newBuilder()
                .setCode(rpcResponse.getCode())
                .setMessage(rpcResponse.getMessage())
                .setResult(rpcResponse.getResultString())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
