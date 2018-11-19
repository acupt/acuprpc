package com.acupt.acuprpc.client;

import com.acupt.acuprpc.core.NodeInfo;
import com.acupt.acuprpc.core.RpcMethodInfo;
import com.acupt.acuprpc.core.RpcRequest;
import com.acupt.acuprpc.util.JsonUtil;
import com.fasterxml.jackson.databind.type.TypeFactory;

import javax.annotation.PreDestroy;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author liujie
 */
public abstract class RpcClient {

    private NodeInfo nodeInfo;

    public RpcClient(NodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;
    }

    /**
     * 关闭连接
     */
    public abstract void shutdownRpc();

    /**
     * 远程调用
     */
    protected abstract String remoteInvoke(RpcRequest rpcRequest);

    /**
     * 重新连接远程服务
     */
    protected abstract NodeInfo reconnectRpc(NodeInfo nodeInfo);

    @PreDestroy
    public void shutdown() {
        shutdownRpc();
    }

    public NodeInfo reconnect(NodeInfo nodeInfo) {
        return reconnectRpc(nodeInfo);
    }

    public Object invoke(RpcMethodInfo methodInfo, Object[] parameters) {
        RpcRequest request = new RpcRequest(
                methodInfo.getRpcServiceInfo().getAppName(),
                methodInfo.getRpcServiceInfo().getServiceName(),
                methodInfo.getMethodName());
        if (parameters != null && parameters.length > 0) {
            request.setOrderedParameter(Arrays.stream(parameters).map(JsonUtil::toJson).collect(Collectors.toList()));
        }
        //todo 维护多个连接，访问到下线节点时自动重试其他节点
        String res = remoteInvoke(request);
        return JsonUtil.fromJson(res, TypeFactory.defaultInstance().constructType(methodInfo.getReturnType()));
    }

    public NodeInfo getNodeInfo() {
        return nodeInfo;
    }

    protected void setNodeInfo(NodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;
    }
}
