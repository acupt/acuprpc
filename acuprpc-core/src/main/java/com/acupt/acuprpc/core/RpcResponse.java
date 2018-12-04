package com.acupt.acuprpc.core;

import com.acupt.acuprpc.exception.HttpStatusException;
import com.acupt.acuprpc.util.JsonUtil;
import lombok.Data;

/**
 * @author liujie
 */
@Data
public class RpcResponse implements RpcCode {

    private int code = SUCCESS;
    private String message = EMPTY_MSG;
    private Object result;

    public void success(Object result) {
        this.code = SUCCESS;
        this.message = EMPTY_MSG;
        this.result = result;
    }

    public void error(Throwable t) {
        if (t instanceof HttpStatusException) {
            this.error(((HttpStatusException) t).getStatus(), t.getMessage());
        }
        this.error(INTERNAL_ERROR, t != null ? t.getClass() + ":" + t.getMessage() : "");
    }

    public void error(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public void reject() {
        code = NOT_AVAILABLE;
        message = "service not available";
    }

    public String jsonResult() {
        return JsonUtil.toJson(result);
    }
}
