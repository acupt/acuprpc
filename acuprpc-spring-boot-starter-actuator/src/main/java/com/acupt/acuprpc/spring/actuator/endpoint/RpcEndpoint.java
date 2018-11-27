package com.acupt.acuprpc.spring.actuator.endpoint;

import com.acupt.acuprpc.core.RpcCode;
import com.acupt.acuprpc.exception.HttpStatusException;
import com.acupt.acuprpc.server.filter.impl.RejectFilter;

/**
 * @author liujie
 */
public class RpcEndpoint extends AbstractRpcEndpoint<Object> implements RpcCode {

    private RejectFilter filter;

    public RpcEndpoint(RejectFilter filter) {
        super("");
        this.filter = filter;
    }

    @Override
    public Object invoke() {
        return null;
    }

    public void online() {
        filter.setReject(false);
    }

    public void offline() {
        filter.setReject(true);
    }

    public int status() {
        if (filter.isReject()) {
            throw new HttpStatusException(NOT_AVAILABLE);
        }
        return 0;
    }

}
