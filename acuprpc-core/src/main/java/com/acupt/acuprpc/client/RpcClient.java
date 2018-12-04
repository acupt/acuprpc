package com.acupt.acuprpc.client;

import com.acupt.acuprpc.core.NodeInfo;
import com.acupt.acuprpc.core.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * @author liujie
 */
@Slf4j
public abstract class RpcClient {

    private NodeInfo nodeInfo;

    private int timeout = 5;

    public RpcClient(NodeInfo nodeInfo) {
        Objects.requireNonNull(nodeInfo);
        this.nodeInfo = nodeInfo;
    }

    /**
     * 远程调用
     */
    protected abstract String remoteInvoke(RpcRequest rpcRequest);

    /**
     * 重新连接远程服务
     */
    protected abstract NodeInfo reconnectRpc(NodeInfo nodeInfo);

    /**
     * 关闭连接
     */
    protected abstract void shutdownRpc();

    public void shutdown() {
        shutdownRpc();
        log.info("shutdown {} {}", getClass().getSimpleName(), nodeInfo);
    }

    public NodeInfo reconnect(NodeInfo nodeInfo) {
        Objects.requireNonNull(nodeInfo);
        return reconnectRpc(nodeInfo);
    }

    public String invoke(RpcRequest rpcRequest) {
        if (rpcRequest.getNamedParameter() == null) {
            rpcRequest.setNamedParameter(new HashMap<>(0));
        }
        if (rpcRequest.getOrderedParameter() == null) {
            rpcRequest.setOrderedParameter(new ArrayList<>(0));
        }
        return remoteInvoke(rpcRequest);
    }

    public NodeInfo getNodeInfo() {
        return nodeInfo;
    }

    protected NodeInfo setNodeInfo(NodeInfo nodeInfo) {
        Objects.requireNonNull(nodeInfo);
        NodeInfo old = this.nodeInfo;
        this.nodeInfo = nodeInfo;
        return old;
    }

    protected int getTimeout() {
        return timeout;
    }

    protected int getTimeoutMilliseconds() {
        return timeout * 1000;
    }
}
