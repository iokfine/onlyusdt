package com.iokfine.data.modules.core.domain.base;

import com.alibaba.fastjson.JSON;
import com.iokfine.data.modules.core.domain.constant.CodeConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 统一返回类
 *
 * @param <T>
 */
public class RespBuilder<T> {

    private static Logger logger = LoggerFactory.getLogger(RespBuilder.class);

    private RespMsg response = new RespMsg();

    public RespBuilder setCode(String codeNum) {
        response.setCode(codeNum);
        return this;
    }

    public RespBuilder setData(T data) {
        response.setData(data);
        return this;
    }

    public RespBuilder setMsg(String message) {
        response.setMsg(message);
        return this;
    }

    public RespBuilder setSuccessResult(T data) {
        logger.debug(JSON.toJSONString(data));
        return setSuccessResult(CodeConstant.SUCCESS_CODE, data);
    }

    public RespBuilder setSuccessResult(String code, T data) {
        return setResult(code, null, data);
    }

    public RespBuilder setFailResult(String data) {
        return setFailResult(CodeConstant.FAIL_CODE, data);
    }

    public RespBuilder setFailResult(String code, String msg) {
        return setResult(code, msg, null);
    }

    public RespBuilder setResult(String code, String msg, T data) {
        logger.debug("+++++++++++++++++++++++ \n response result：code:{} -- msg:{} -- data:{} \n+++++++++++++++++++++++", code, msg, JSON.toJSONString(data));
        return setCode(code).setMsg(msg).setData(data);
    }

    public RespMsg build() {
        return response;
    }
}
