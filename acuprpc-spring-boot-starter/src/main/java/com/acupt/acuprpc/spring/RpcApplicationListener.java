package com.acupt.acuprpc.spring;

import com.acupt.acuprpc.server.RpcServer;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

/**
 * @author liujie
 */
public class RpcApplicationListener implements ApplicationListener<ApplicationReadyEvent> {

    private RpcServer rpcServer;

    public RpcApplicationListener(RpcServer rpcServer) {
        this.rpcServer = rpcServer;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        rpcServer.started();
    }
}
