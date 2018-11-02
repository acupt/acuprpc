package com.acupt.acuprpc.core.conf;

import com.netflix.appinfo.DataCenterInfo;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.MyDataCenterInfo;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mark on 2017/6/28.
 */
@Data
public class RpcEurekaInstanceConfig implements EurekaInstanceConfig {

    private static final String UNKNOWN = "unknown";

    /**
     * Get the name of the application to be registered with eureka.
     */
    private String appname = UNKNOWN;

    /**
     * Get the name of the application group to be registered with eureka.
     */
    private String appGroupName;

    /**
     * Indicates whether the instance should be enabled for taking traffic as soon as it
     * is registered with eureka. Sometimes the application might need to do some
     * pre-processing before it is ready to take traffic.
     */
    private boolean instanceEnabledOnit;

    /**
     * Get the non-secure port on which the instance should receive traffic.
     */
    private int nonSecurePort = 5000;

    /**
     * Get the Secure port on which the instance should receive traffic.
     */
    private int securePort = 443;

    /**
     * Indicates whether the non-secure port should be enabled for traffic or not.
     */
    private boolean nonSecurePortEnabled = true;

    /**
     * Indicates whether the secure port should be enabled for traffic or not.
     */
    private boolean securePortEnabled;

    /**
     * Indicates how often (in seconds) the eureka client needs to send heartbeats to
     * eureka server to indicate that it is still alive. If the heartbeats are not
     * received for the period specified in leaseExpirationDurationInSeconds, eureka
     * server will remove the instance from its view, there by disallowing traffic to this
     * instance.
     * <p>
     * Note that the instance could still not take traffic if it implements
     * HealthCheckCallback and then decides to make itself unavailable.
     */
    private int leaseRenewalIntervalInSeconds = 30;

    /**
     * Indicates the time in seconds that the eureka server waits since it received the
     * last heartbeat before it can remove this instance from its view and there by
     * disallowing traffic to this instance.
     * <p>
     * Setting this value too long could mean that the traffic could be routed to the
     * instance even though the instance is not alive. Setting this value too small could
     * mean, the instance may be taken out of traffic because of temporary network
     * glitches.This value to be set to atleast higher than the value specified in
     * leaseRenewalIntervalInSeconds.
     */
    private int leaseExpirationDurationInSeconds = 90;

    /**
     * Gets the virtual host name defined for this instance.
     * <p>
     * This is typically the way other instance would find this instance by using the
     * virtual host name.Think of this as similar to the fully qualified domain name, that
     * the users of your services will need to find this instance.
     */
    private String virtualHostName = UNKNOWN;

    /**
     * Get the unique Id (within the scope of the appName) of this instance to be
     * registered with eureka.
     */
    private String instanceId;

    /**
     * Gets the secure virtual host name defined for this instance.
     * <p>
     * This is typically the way other instance would find this instance by using the
     * secure virtual host name.Think of this as similar to the fully qualified domain
     * name, that the users of your services will need to find this instance.
     */
    private String secureVirtualHostName = UNKNOWN;

    /**
     * Gets the AWS autoscaling group name associated with this instance. This information
     * is specifically used in an AWS environment to automatically put an instance out of
     * service after the instance is launched and it has been disabled for traffic..
     */
    private String aSGName;

    /**
     * Gets the metadata name/value pairs associated with this instance. This information
     * is sent to eureka server and can be used by other instances.
     */
    private Map<String, String> metadataMap = new HashMap<>();

    /**
     * Returns the data center this instance is deployed. This information is used to get
     * some AWS specific instance information if the instance is deployed in AWS.
     */
    private DataCenterInfo dataCenterInfo = new MyDataCenterInfo(
            DataCenterInfo.Name.MyOwn);

    /**
     * Get the IPAdress of the instance. This information is for academic purposes only as
     * the communication from other instances primarily happen using the information
     * supplied in {@link #getHostName(boolean)}.
     */
    private String ipAddress;

    /**
     * Gets the relative status page URL path for this instance. The status page URL is
     * then constructed out of the hostName and the type of communication - secure or
     * unsecure as specified in securePort and nonSecurePort.
     * <p>
     * It is normally used for informational purposes for other services to find about the
     * status of this instance. Users can provide a simple HTML indicating what is the
     * get status of the instance.
     */
    private String statusPageUrlPath = "/info";

    /**
     * Gets the absolute status page URL path for this instance. The users can provide the
     * statusPageUrlPath if the status page resides in the same instance talking to
     * eureka, else in the cases where the instance is a proxy for some other server,
     * users can provide the full URL. If the full URL is provided it takes precedence.
     * <p>
     * It is normally used for informational purposes for other services to find about the
     * status of this instance. Users can provide a simple HTML indicating what is the
     * get status of the instance.
     */
    private String statusPageUrl;

    /**
     * Gets the relative home page URL Path for this instance. The home page URL is then
     * constructed out of the hostName and the type of communication - secure or unsecure.
     * <p>
     * It is normally used for informational purposes for other services to use it as a
     * landing page.
     */
    private String homePageUrlPath = "/";

    /**
     * Gets the absolute home page URL for this instance. The users can provide the
     * homePageUrlPath if the home page resides in the same instance talking to eureka,
     * else in the cases where the instance is a proxy for some other server, users can
     * provide the full URL. If the full URL is provided it takes precedence.
     * <p>
     * It is normally used for informational purposes for other services to use it as a
     * landing page. The full URL should follow the format http://${eureka.hostname}:7001/
     * where the value ${eureka.hostname} is replaced at runtime.
     */
    private String homePageUrl;

    /**
     * Gets the relative health check URL path for this instance. The health check page
     * URL is then constructed out of the hostname and the type of communication - secure
     * or unsecure as specified in securePort and nonSecurePort.
     * <p>
     * It is normally used for making educated decisions based on the health of the
     * instance - for example, it can be used to determine whether to proceed deployments
     * to an entire farm or stop the deployments without causing further damage.
     */
    private String healthCheckUrlPath = "/health";

    /**
     * Gets the absolute health check page URL for this instance. The users can provide
     * the healthCheckUrlPath if the health check page resides in the same instance
     * talking to eureka, else in the cases where the instance is a proxy for some other
     * server, users can provide the full URL. If the full URL is provided it takes
     * precedence.
     * <p>
     * <p>
     * It is normally used for making educated decisions based on the health of the
     * instance - for example, it can be used to determine whether to proceed deployments
     * to an entire farm or stop the deployments without causing further damage. The full
     * URL should follow the format http://${eureka.hostname}:7001/ where the value
     * ${eureka.hostname} is replaced at runtime.
     */
    private String healthCheckUrl;

    /**
     * Gets the absolute secure health check page URL for this instance. The users can
     * provide the secureHealthCheckUrl if the health check page resides in the same
     * instance talking to eureka, else in the cases where the instance is a proxy for
     * some other server, users can provide the full URL. If the full URL is provided it
     * takes precedence.
     * <p>
     * <p>
     * It is normally used for making educated decisions based on the health of the
     * instance - for example, it can be used to determine whether to proceed deployments
     * to an entire farm or stop the deployments without causing further damage. The full
     * URL should follow the format http://${eureka.hostname}:7001/ where the value
     * ${eureka.hostname} is replaced at runtime.
     */
    private String secureHealthCheckUrl;

    /**
     * Get the namespace used to find properties. Ignored in Spring Cloud.
     */
    private String namespace = "eureka";

    /**
     * The hostname if it can be determined at configuration time (otherwise it will be
     * guessed from OS primitives).
     */
    private String hostname;

    /**
     * Flag to say that, when guessing a hostname, the IP address of the server should be
     * used in prference to the hostname reported by the OS.
     */
    private boolean preferIpAddress = false;

    /**
     * Initial status to register with rmeote Eureka server.
     */
    private InstanceInfo.InstanceStatus initialStatus = InstanceInfo.InstanceStatus.UP;

    private String[] defaultAddressResolutionOrder = new String[0];


    public String getHostname() {
        return getHostName(false);
    }


    @Override
    public String getInstanceId() {
        return this.getHostname() + ":" + this.getNonSecurePort();
    }

    @Override
    public boolean getSecurePortEnabled() {
        return false;
    }

    @Override
    public String getHostName(boolean refresh) {
        return this.preferIpAddress ? this.ipAddress : this.hostname;
    }
}
