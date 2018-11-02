package com.acupt.acuprpc.protocol.grpc;

import com.acupt.acuprpc.core.RpcRequest;
import com.acupt.acuprpc.client.RpcClient;
import com.acupt.acuprpc.core.NodeInfo;
import com.acupt.acuprpc.exception.RpcException;
import com.acupt.acuprpc.protocol.grpc.proto.GrpcServiceGrpc;
import com.acupt.acuprpc.protocol.grpc.proto.InvokeRequest;
import com.acupt.acuprpc.protocol.grpc.proto.InvokeResponse;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author liujie
 */
public class GrpcClient extends RpcClient {

    private AtomicReference<GrpcServiceGrpc.GrpcServiceBlockingStub> stubRef;

    public GrpcClient(NodeInfo nodeInfo) {
        super(nodeInfo);
        this.stubRef = new AtomicReference<>(getStub(nodeInfo));
    }

    @Override
    protected String remoteInvoke(RpcRequest rpcRequest) {
        InvokeRequest.Builder builder = InvokeRequest.newBuilder()
                .setAppName(rpcRequest.getAppName())
                .setServiceName(rpcRequest.getServiceName())
                .setMethodName(rpcRequest.getMethodName());
        if (rpcRequest.getOrderedParameter() != null && !rpcRequest.getOrderedParameter().isEmpty()) {
            builder.addAllOrderedParameter(rpcRequest.getOrderedParameter());
        }
        InvokeResponse response = stubRef.get().invokeMethod(builder.build());
        if (response.getCode() != 0) {
            throw new RpcException(response.getCode() + ":" + response.getMessage());
        }
        return response.getResult();
    }

    @Override
    @SneakyThrows
    public void shutdownRpc() {
        GrpcServiceGrpc.GrpcServiceBlockingStub stub = stubRef.get();
        if (stub == null) {
            return;
        }
        Channel channel = stub.getChannel();
        if (channel instanceof ManagedChannel) {
            ((ManagedChannel) channel).shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    private GrpcServiceGrpc.GrpcServiceBlockingStub getStub(NodeInfo nodeInfo) {
        Channel channel = ManagedChannelBuilder
                .forAddress(nodeInfo.getIp(), nodeInfo.getPort())
                .usePlaintext(true)
                .build();
        return GrpcServiceGrpc.newBlockingStub(channel);
    }
}
