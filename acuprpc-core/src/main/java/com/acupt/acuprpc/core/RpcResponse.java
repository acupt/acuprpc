package com.acupt.acuprpc.core;

import com.acupt.acuprpc.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

/**
 * @author liujie
 */
@Data
public class RpcResponse {

    private int code = 0;
    private String message = "";
    private Object result;

    public RpcResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public RpcResponse(Object result) {
        this.result = result;
    }

    public String getResultString() {
        return JsonUtil.toJson(result);
    }
}
