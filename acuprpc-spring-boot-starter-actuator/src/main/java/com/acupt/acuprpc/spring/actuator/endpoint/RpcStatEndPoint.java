package com.acupt.acuprpc.spring.actuator.endpoint;

import com.acupt.acuprpc.server.filter.impl.MonitorFilter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liujie
 */
public class RpcStatEndPoint extends AbstractRpcEndPoint<Map<String, Object>> {

    private MonitorFilter filter;

    public RpcStatEndPoint(MonitorFilter filter) {
        super("stat");
        this.filter = filter;
    }

    @Override
    public Map<String, Object> invoke() {
        Map<String, Object> result = new HashMap<>();
        Collection<MonitorFilter.RequestCount> counts = filter.getRequestCountMap().values();
        result.put("counts", counts);
        result.put("serving", counts.stream().anyMatch(t -> t.getInvoking().sum() > 0L));
        return result;
    }
}