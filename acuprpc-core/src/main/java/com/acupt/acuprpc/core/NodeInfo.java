package com.acupt.acuprpc.core;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author liujie
 */
@Data
@AllArgsConstructor
public class NodeInfo {
    private String ip;
    private int port;
}
