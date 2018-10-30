package com.acupt.acuprpc.core;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Method;

/**
 * @author liujie
 */
@Data
@AllArgsConstructor
public class MethodInfo {
    private Method method;
}
