package com.acupt.acuprpc.core;

import com.acupt.acuprpc.util.JsonUtil;
import lombok.Data;

/**
 * @author liujie
 */
@Data
public class RpcResponse {

    private int code = 0;
    private String message = "";
    private Object result;

    public void success(Object result) {
        this.code = 0;
        this.message = "";
        this.result = result;
    }

    public void error(int code, Throwable t) {
        this.error(code, t != null ? t.getClass() + ":" + t.getMessage() : "");
    }

    public void error(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getResultString() {
        return JsonUtil.toJson(result);
    }
}
