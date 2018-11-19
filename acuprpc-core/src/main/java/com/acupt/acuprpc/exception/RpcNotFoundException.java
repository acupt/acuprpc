package com.acupt.acuprpc.exception;

/**
 * @author liujie
 */
public class RpcNotFoundException extends Exception {

    public RpcNotFoundException() {
    }

    public RpcNotFoundException(String message) {
        super(message);
    }

    public RpcNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcNotFoundException(Throwable cause) {
        super(cause);
    }
}
