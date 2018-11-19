package com.acupt.acuprpc.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author liujie
 */
@Getter
@AllArgsConstructor
public class NodeInfo {
    private String ip;
    private int port;

    @Override
    public String toString() {
        return ip + ":" + port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeInfo nodeInfo = (NodeInfo) o;
        return port == nodeInfo.port &&
                Objects.equals(ip, nodeInfo.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port);
    }
}
