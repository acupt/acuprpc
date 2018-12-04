package com.acupt.acuprpc.protocol.grpc;

import com.acupt.acuprpc.client.RpcClient;
import com.acupt.acuprpc.core.NodeInfo;
import com.acupt.acuprpc.core.RpcCode;
import com.acupt.acuprpc.core.RpcRequest;
import com.acupt.acuprpc.exception.HttpStatusException;
import com.acupt.acuprpc.exception.RpcException;
import com.acupt.acuprpc.protocol.grpc.proto.GrpcServiceGrpc;
import com.acupt.acuprpc.protocol.grpc.proto.InvokeRequest;
import com.acupt.acuprpc.protocol.grpc.proto.InvokeResponse;
import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.AbstractStub;
import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author liujie
 */
public class GrpcClient extends RpcClient implements RpcCode {

    private static final int shutdownTimeout = 5;

    private AtomicReference<GrpcServiceGrpc.GrpcServiceFutureStub> stubRef;

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
        if (rpcRequest.getNamedParameter() != null && !rpcRequest.getNamedParameter().isEmpty()) {
            builder.putAllNamedParameter(rpcRequest.getNamedParameter());
        }
        ListenableFuture<InvokeResponse> future = stubRef.get().invokeMethod(builder.build());
        InvokeResponse response = null;
        try {
            response = future.get(getTimeout(), TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RpcException(e);
        }
        if (response.getCode() != SUCCESS) {
            throw new HttpStatusException(response.getCode(), response.getMessage());
        }
        return response.getResult();
    }

    @Override
    @SneakyThrows
    protected NodeInfo reconnectRpc(NodeInfo nodeInfo) {
        AbstractStub<?> old = stubRef.getAndUpdate(s -> getStub(nodeInfo));
        if (old != null && old.getChannel() instanceof ManagedChannel) {
            ((ManagedChannel) old.getChannel()).shutdown()
                    .awaitTermination(shutdownTimeout, TimeUnit.SECONDS);
        }
        return setNodeInfo(nodeInfo);
    }

    @Override
    @SneakyThrows
    public void shutdownRpc() {
        AbstractStub<?> stub = stubRef.get();
        if (stub == null) {
            return;
        }
        Channel channel = stub.getChannel();
        if (channel instanceof ManagedChannel) {
            ((ManagedChannel) channel).shutdown().awaitTermination(shutdownTimeout, TimeUnit.SECONDS);
        }
    }

    private GrpcServiceGrpc.GrpcServiceFutureStub getStub(NodeInfo nodeInfo) {
        Channel channel = ManagedChannelBuilder
                .forAddress(nodeInfo.getIp(), nodeInfo.getPort())
                .usePlaintext(true)
                .build();
        return GrpcServiceGrpc.newFutureStub(channel);
    }
}
