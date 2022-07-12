package com.iokfine.data.modules.core.domain.base;

import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class RespMsg<T> implements Serializable {

    /*返回码，成功：000000*/
    private String code;

    /*返回数据*/
    private T data;

    /*备注说明信息*/
    private String msg = "";

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static <T> RespBuilder<T> builder() {
        return new RespBuilder<T>();
    }

    /**
     * 参数 ：
     * <p>V</p>
     * <p>K, V  ...  K, V</p>
     *
     * <p>示例: "CharSequence", obj, "CharSequence", obj</p>
     *
     * @param obj
     * @return
     */
    public static RespMsg<?> successResult(Object... obj) {

        int bound = obj.length;
        if (bound == 1) {
            return new RespBuilder<>().setSuccessResult(obj[0]).build();
        }

        if (bound % 2 != 0) {
            throw new IllegalArgumentException("RespBuilder.setSuccessMapResult() Wrong number of parameters");
        }

        Map<Object, Object> map = new HashMap<>();
        for (int i = 0; i < bound; i = i + 2) {
            Assert.isInstanceOf(CharSequence.class, obj[i], "RespBuilder.setSuccessMapResult() parameter type error");
            map.put(obj[i], obj[i + 1] == null ? "" : obj[i + 1]);
        }

        return new RespBuilder<>().setSuccessResult(map).build();
    }


    public static RespMsg<?> successMapResult(Object... obj) {

        Map<Object, Object> map = new HashMap<>();
        int bound = obj.length;
        if (bound == 1) {
            map.put("res", obj[0]);
            return new RespBuilder<>().setSuccessResult(map).build();
        }

        if (bound % 2 != 0) {
            throw new IllegalArgumentException("RespBuilder.setSuccessMapResult() Wrong number of parameters");
        }


        for (int i = 0; i < bound; i = i + 2) {
            Assert.isInstanceOf(CharSequence.class, obj[i], "RespBuilder.successMapResult() parameter type error");
            map.put(obj[i], obj[i + 1] == null ? "" : obj[i + 1]);
        }

        return new RespBuilder<>().setSuccessResult(map).build();
    }

}
