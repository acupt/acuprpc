package com.acupt.acuprpc.server.filter;

import com.acupt.acuprpc.core.RpcRequest;
import com.acupt.acuprpc.core.RpcResponse;

/**
 * @author liujie
 */
public interface RpcFilter {

    void doFilter(RpcRequest request, RpcResponse response, RpcFilterChain filterChain);
}
