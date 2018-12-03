package com.acupt.acuprpc.spring;

import com.acupt.acuprpc.core.MethodInfo;
import com.acupt.acuprpc.core.Rpc;
import com.acupt.acuprpc.core.RpcServiceInfo;
import com.acupt.acuprpc.server.RpcServer;
import lombok.val;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liujie
 */
public class RpcServiceScanner implements BeanPostProcessor {

    private RpcServer rpcServer;

    public RpcServiceScanner(RpcServer rpcServer) {
        this.rpcServer = rpcServer;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // AOP代理类需要拿到原始的类，不然读不到类上的注解
        Class<?> beanClass = AopUtils.isAopProxy(bean) ? AopUtils.getTargetClass(bean) : bean.getClass();
        val nrpc = beanClass.getAnnotation(Rpc.class);
        if (nrpc == null) {
            return bean;
        }

        Method[] methods = beanClass.getDeclaredMethods();
        if (methods.length == 0) {
            return bean;
        }
        Map<String, MethodInfo> methodInfoMap = new HashMap<>();
        for (Method method : methods) {
            methodInfoMap.put(method.getName(), new MethodInfo(method));
        }

        Class<?>[] interfaces = beanClass.getInterfaces();
        if (interfaces.length == 0) {
            return bean;
        }
        // client是通过接口调用server的，因此并不知道具体实现类的路径，只有接口名，因此把所有接口都注册一遍
        for (Class<?> serviceInterface : interfaces) {
            rpcServer.registerService(
                    new RpcServiceInfo(rpcServer.getRpcInstance().getRpcConf().getAppName(),
                            serviceInterface.getCanonicalName()),
                    bean,
                    serviceInterface,
                    methodInfoMap);
        }
        return bean;
    }
}
