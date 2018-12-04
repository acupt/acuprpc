package com.acupt.acuprpc.protocol.http;

import com.acupt.acuprpc.core.RpcInstance;
import com.acupt.acuprpc.core.RpcRequest;
import com.acupt.acuprpc.core.RpcResponse;
import com.acupt.acuprpc.server.RpcServer;
import com.acupt.acuprpc.util.JsonUtil;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.spi.HttpServerProvider;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * @author liujie
 */
public class HttpRpcServer extends RpcServer {

    private static final int nThreads = 100;

    private static final int BACKLOG = 100;

    private static final String CHARSET = "UTF-8";

    private HttpServer httpServer;

    @SneakyThrows
    public HttpRpcServer(RpcInstance rpcInstance) {
        super(rpcInstance);
        HttpServerProvider provider = HttpServerProvider.provider();
        httpServer = provider.createHttpServer(new InetSocketAddress(rpcInstance.getRpcConf().getPort()), BACKLOG);
        httpServer.createContext("/", httpExchange -> {
            String json = IOUtils.toString(httpExchange.getRequestBody(), CHARSET);
            RpcResponse rpcResponse = new RpcResponse();
            if (!"post".equalsIgnoreCase(httpExchange.getRequestMethod())) {
                rpcResponse.error(METHOD_NOT_ALLOWED, httpExchange.getRequestMethod() + " not allowed");
            } else if (StringUtils.isEmpty(json)) {
                rpcResponse.error(BAD_REQUEST, "request body can't empty");
            } else {
                RpcRequest rpcRequest = JsonUtil.fromJson(json, RpcRequest.class);
                rpcResponse = execute(rpcRequest);
            }
            String responseJson = JsonUtil.toJson(rpcResponse);
            byte[] bytes = responseJson.getBytes(CHARSET);
            httpExchange.getResponseHeaders().add("Content-Type", "application/json; charset=" + CHARSET);
            httpExchange.sendResponseHeaders(SUCCESS, bytes.length);
            httpExchange.getResponseBody().write(bytes);
            httpExchange.close();
        });
        httpServer.setExecutor(Executors.newFixedThreadPool(nThreads));
    }

    @Override
    protected void startRpc() {
        httpServer.start();
    }

    @Override
    protected void shutdownRpc() {
        httpServer.stop(1);
    }

}
