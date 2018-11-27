package com.acupt.acuprpc.server.filter.impl;

import com.acupt.acuprpc.core.RpcRequest;
import com.acupt.acuprpc.core.RpcResponse;
import com.acupt.acuprpc.server.filter.RpcFilter;
import com.acupt.acuprpc.server.filter.RpcFilterChain;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author liujie
 */
@Getter
public class MonitorFilter implements RpcFilter {

    private Map<String, RequestCount> requestCountMap = new ConcurrentHashMap<>();

    @Override
    public void doFilter(RpcRequest request, RpcResponse response, RpcFilterChain filterChain) {
        RequestCount count = requestCountMap.computeIfAbsent(request.getKey(), RequestCount::new);
        count.received.increment();
        count.invoking.increment();
        try {
            filterChain.doFilter(request, response);
            count.success.increment();
        } catch (Exception e) {
            count.failed.increment();
            throw e;
        } finally {
            count.invoking.decrement();
        }
    }

    @Getter
    public static class RequestCount {
        private String key;
        private LongAdder received = new LongAdder();//已接收
        private LongAdder invoking = new LongAdder();//执行中
        private LongAdder success = new LongAdder();//处理成功
        private LongAdder failed = new LongAdder();//处理失败

        public RequestCount(String key) {
            this.key = key;
        }
    }

}
