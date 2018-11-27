package com.acupt.acuprpc.exception;

/**
 * @author liujie
 */
public class HttpStatusException extends RpcException {

    private int status;

    public HttpStatusException(int status) {
        super(status + "");
        this.status = status;
    }

    public HttpStatusException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
