package com.acupt.acuprpc.spring.actuator.endpoint;

import org.springframework.boot.actuate.endpoint.AbstractEndpoint;

/**
 * @author liujie
 */
public abstract class AbstractRpcEndPoint<T> extends AbstractEndpoint<T> {

    private static final String PREFIX = "rpc";

    public AbstractRpcEndPoint(String id) {
        super(PREFIX + id);
    }

    public AbstractRpcEndPoint(String id, boolean sensitive) {
        super(PREFIX + id, sensitive);
    }

    public AbstractRpcEndPoint(String id, boolean sensitive, boolean enabled) {
        super(PREFIX + id, sensitive, enabled);
    }
}
