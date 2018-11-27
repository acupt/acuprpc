package com.acupt.acuprpc.server.filter.impl;

import com.acupt.acuprpc.core.RpcRequest;
import com.acupt.acuprpc.core.RpcResponse;
import com.acupt.acuprpc.server.filter.RpcFilter;
import com.acupt.acuprpc.server.filter.RpcFilterChain;
import lombok.Data;

import java.util.function.BiConsumer;

/**
 * @author liujie
 */
@Data
public class RejectFilter implements RpcFilter {

    private boolean reject = false;

    private BiConsumer<RpcRequest, RpcResponse> rejectFunction = (rpcRequest, response) -> response.reject();

    @Override
    public void doFilter(RpcRequest request, RpcResponse response, RpcFilterChain filterChain) {
        if (reject) {
            rejectFunction.accept(request, response);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
