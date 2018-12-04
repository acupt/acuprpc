package com.acupt.acuprpc.core;

/**
 * @author liujie
 */
public interface RpcCode {
    String EMPTY_MSG = "";
    int SUCCESS = 200;
    int BAD_REQUEST = 400;
    int FORBIDDEN = 403;
    int SERVICE_NOT_FOUND = 404;
    int METHOD_NOT_ALLOWED = 405;
    int INTERNAL_ERROR = 500;
    int NOT_AVAILABLE = 503;//服务(暂)不可用
}
