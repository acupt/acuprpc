package com.acupt.acuprpc.protocol.http;

import com.acupt.acuprpc.client.RpcClient;
import com.acupt.acuprpc.core.NodeInfo;
import com.acupt.acuprpc.core.RpcCode;
import com.acupt.acuprpc.core.RpcRequest;
import com.acupt.acuprpc.core.RpcResponse;
import com.acupt.acuprpc.exception.HttpStatusException;
import com.acupt.acuprpc.exception.RpcException;
import com.acupt.acuprpc.util.JsonUtil;
import lombok.SneakyThrows;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.Charset;

/**
 * @author liujie
 */
public class HttpRpcClient extends RpcClient implements RpcCode {

    private static final String CHARSET = "UTF-8";

    private HttpClient httpClient;

    public HttpRpcClient(NodeInfo nodeInfo) {
        super(nodeInfo);
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
        this.httpClient = HttpClientBuilder.create()
                .setConnectionManager(manager)
                .build();
    }

    @Override
    @SneakyThrows
    protected String remoteInvoke(RpcRequest rpcRequest) {
        HttpHost host = new HttpHost(getNodeInfo().getIp(), getNodeInfo().getPort());
        HttpPost post = new HttpPost("/");
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(getTimeoutMilliseconds())
                .setSocketTimeout(getTimeoutMilliseconds())
                .setConnectionRequestTimeout(getTimeoutMilliseconds())
                .build();
        post.setConfig(config);
        post.addHeader("Content-type", "application/json; charset=" + CHARSET);
        post.setEntity(new StringEntity(JsonUtil.toJson(rpcRequest), Charset.forName(CHARSET)));
        HttpResponse response = httpClient.execute(host, post);
        StatusLine statusLine = response.getStatusLine();
        if (statusLine.getStatusCode() != SUCCESS) {
            throw new RpcException(statusLine.toString());
        }
        String json = EntityUtils.toString(response.getEntity());
        if (StringUtils.isEmpty(json)) {
            throw new RpcException("response empty");
        }
        RpcResponse rpcResponse = JsonUtil.fromJson(json, RpcResponse.class);
        if (rpcResponse.getCode() != SUCCESS) {
            throw new HttpStatusException(rpcResponse.getCode(), rpcResponse.getMessage());
        }
        return rpcResponse.jsonResult();
    }

    @Override
    protected NodeInfo reconnectRpc(NodeInfo nodeInfo) {
        return setNodeInfo(nodeInfo);
    }

    @Override
    public void shutdownRpc() {
    }
}
