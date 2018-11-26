package com.acupt.acuprpc.core;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author liujie
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class NodeInfo {
    private String ip;
    private int port;

    @Override
    public String toString() {
        return ip + ":" + port;
    }
}
