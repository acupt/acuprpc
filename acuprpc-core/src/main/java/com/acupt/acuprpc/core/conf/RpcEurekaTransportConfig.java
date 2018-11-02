package com.acupt.acuprpc.core.conf;

/**
 * Created by mark on 2017/6/14.
 */
import com.netflix.discovery.shared.transport.EurekaTransportConfig;
import lombok.Data;

/**
 * @author Spencer Gibb
 */
@Data
public class RpcEurekaTransportConfig implements EurekaTransportConfig {

    private int sessionedClientReconnectIntervalSeconds = 20 * 60;

    private double retryableClientQuarantineRefreshPercentage = 0.66;

    private int bootstrapResolverRefreshIntervalSeconds = 5 * 60;

    private int applicationsResolverDataStalenessThresholdSeconds = 5 * 60;

    private int asyncResolverRefreshIntervalMs = 5 * 60 * 1000;

    private int asyncResolverWarmUpTimeoutMs = 5000;

    private int asyncExecutorThreadPoolSize = 5;

    private String readClusterVip;

    private String writeClusterVip;

    private boolean bootstrapResolverForQuery = true;

    private String bootstrapResolverStrategy;

    private boolean applicationsResolverUseIp = false;

    @Override
    public boolean useBootstrapResolverForQuery() {
        return this.bootstrapResolverForQuery;
    }

    @Override
    public boolean applicationsResolverUseIp() {
        return this.applicationsResolverUseIp;
    }
}