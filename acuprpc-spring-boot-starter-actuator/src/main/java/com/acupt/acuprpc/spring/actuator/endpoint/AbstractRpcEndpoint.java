package com.acupt.acuprpc.spring.actuator.endpoint;

import org.springframework.boot.actuate.endpoint.AbstractEndpoint;

/**
 * @author liujie
 */
public abstract class AbstractRpcEndpoint<T> extends AbstractEndpoint<T> {

    private static final String PREFIX = "rpc";

    public AbstractRpcEndpoint(String id) {
        super(PREFIX + id);
    }

    public AbstractRpcEndpoint(String id, boolean sensitive) {
        super(PREFIX + id, sensitive);
    }

    public AbstractRpcEndpoint(String id, boolean sensitive, boolean enabled) {
        super(PREFIX + id, sensitive, enabled);
    }
}
