package com.acupt.acuprpc.spring.actuator.endpoint;

import com.acupt.acuprpc.core.RpcCode;
import com.acupt.acuprpc.exception.HttpStatusException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.endpoint.Endpoint;
import org.springframework.boot.actuate.endpoint.mvc.ActuatorMediaTypes;
import org.springframework.boot.actuate.endpoint.mvc.EndpointMvcAdapter;
import org.springframework.boot.actuate.endpoint.mvc.HypermediaDisabled;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author liujie
 */
@Slf4j
public class ReflectEndpointMvcAdapter extends EndpointMvcAdapter implements RpcCode {

    private Map<String, Method> methodMap = new HashMap<>();

    private Set<String> ipWhiteList = new HashSet<>();

    public ReflectEndpointMvcAdapter(Endpoint<?> delegate, String ipWhiteList) {
        super(delegate);
        Method[] methods = delegate.getClass().getMethods();
        for (Method method : methods) {
            if (method.getParameterCount() > 0) {
                continue;
            }
            methodMap.put(method.getName(), method);
        }
        if (!StringUtils.isEmpty(ipWhiteList)) {
            for (String ip : ipWhiteList.trim().split(",")) {
                ip = ip.trim();
                if (!StringUtils.isEmpty(ip)) {
                    this.ipWhiteList.add(ip);
                }
            }
        }
    }

    @RequestMapping(value = "/{name:.*}", method = RequestMethod.GET, produces = {
            ActuatorMediaTypes.APPLICATION_ACTUATOR_V1_JSON_VALUE,
            MediaType.APPLICATION_JSON_VALUE
    })
    @ResponseBody
    @HypermediaDisabled
    public Object invoke(HttpServletRequest request, HttpServletResponse response, @PathVariable String name) {
        if (!checkIp(request)) {
            try {
                response.sendError(FORBIDDEN, "forbidden ip, try set acuprpc.endpoints.ipWhiteList=<your_ip>");
            } catch (IOException e) {
                response.setStatus(FORBIDDEN);
            }
            return null;
        }
        Method method = methodMap.get(name);
        if (method == null) {
            response.setStatus(SERVICE_NOT_FOUND);
            return null;
        }
        try {
            return method.invoke(getDelegate());
        } catch (Exception e) {
            if (e.getCause() != null && e.getCause() instanceof HttpStatusException) {
                response.setStatus(((HttpStatusException) e.getCause()).getStatus());
                return null;
            }
            throw new RuntimeException(e);
        }
    }

    private boolean checkIp(HttpServletRequest request) {
        if (CollectionUtils.isEmpty(ipWhiteList)) {
            return true;
        }
        String ip = request.getRemoteHost();
        return ipWhiteList.contains(ip);
    }

    private String getIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
