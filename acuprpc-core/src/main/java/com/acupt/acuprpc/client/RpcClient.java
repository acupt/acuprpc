package com.acupt.acuprpc.client;

import com.acupt.acuprpc.core.NodeInfo;
import com.acupt.acuprpc.core.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author liujie
 */
@Slf4j
public abstract class RpcClient {

    private NodeInfo nodeInfo;

    private int timeout = 5;

    public RpcClient(NodeInfo nodeInfo) {
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
    public abstract void shutdownRpc();

    @PreDestroy
    public void shutdown() {
        log.info("shutting down {}", this);
        shutdownRpc();
    }

    public NodeInfo reconnect(NodeInfo nodeInfo) {
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

    protected void setNodeInfo(NodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;
    }

    public int getTimeout() {
        return timeout;
    }
}
