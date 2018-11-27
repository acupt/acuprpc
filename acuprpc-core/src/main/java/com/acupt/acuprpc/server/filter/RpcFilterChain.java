package com.acupt.acuprpc.server.filter;

import com.acupt.acuprpc.core.RpcRequest;
import com.acupt.acuprpc.core.RpcResponse;
import com.acupt.acuprpc.core.RpcServiceInfo;
import com.acupt.acuprpc.server.RpcServiceExecutor;

import java.util.List;

/**
 * @author liujie
 */
public class RpcFilterChain {

    private RpcFilter[] filters = new RpcFilter[0];

    private int pos;

    private RpcServiceInfo serviceInfo;

    private RpcServiceExecutor serviceExecutor;

    public RpcFilterChain(List<RpcFilter> filterList, RpcServiceInfo serviceInfo, RpcServiceExecutor serviceExecutor) {
        if (filterList != null && !filterList.isEmpty()) {
            this.filters = new RpcFilter[filterList.size()];
            this.filters = filterList.toArray(this.filters);
        }
        this.serviceInfo = serviceInfo;
        this.serviceExecutor = serviceExecutor;
    }

    public void doFilter(RpcRequest request, RpcResponse response) {
        if (pos < filters.length) {
            RpcFilter filter = filters[pos++];
            filter.doFilter(request, response, this);
            return;
        }
        if (serviceExecutor == null) {
            response.error(404, "service not exist: " + serviceInfo);
            return;
        }
        serviceExecutor.execute(request, response);
    }

}
