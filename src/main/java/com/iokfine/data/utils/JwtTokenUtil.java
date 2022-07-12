package com.iokfine.data.utils;


import com.alibaba.fastjson.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;


/**
 * @author わらい
 */
@Slf4j
public class JwtTokenUtil {


    public static String getAttr(String token, String key) {
        if(StringUtils.isBlank(token)){
            return null;
        }
        try {
            DecodedJWT decode = JWT.decode(token);
            Claim claim = decode.getClaims().get(key);

            if (claim == null) {
                return null;
            }
            return claim.as(String.class);
        } catch (JWTDecodeException e) {
            log.error("token:{},解析jwt异常:{}", token, e.getMessage());
        }
        return null;
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    public static Map<String, Claim> getClaimsFromToken(String token) {

        DecodedJWT decode;
        try{
            decode = JWT.decode(token);
        }catch (JWTDecodeException e){
//            log.warn("JWTDecodeException解析出错，错误token：{},url:{}", token , HttpHelper.getHttpRequest().getRequestURI());
            throw new RuntimeException("错误");
        }
        return decode.getClaims();
    }


    /**
     * get value from token
     *
     * @param claimKey
     * @return
     */
    public static String getTextClaimFromToken(String token,String claimKey) {
        if(StringUtils.isBlank(token)){
            return null;
        }
        Map<String, Claim> claims = getClaimsFromToken(token);
        return claims.get(claimKey).as(String.class);
    }


    /**
     * get value from token mapper to class
     *
     * @param claimKey
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T getClaimFromToken(String token,String claimKey, Class<T> tClass) {
        if(StringUtils.isBlank(token)){
            return null;
        }
        Map<String, Claim> claims = getClaimsFromToken(token);
        Claim claim = claims.get(claimKey);
        if(claim == null){
            return null;
        }

        String jsonStr = claim.as(String.class);
        return JSON.parseObject(jsonStr, tClass);
    }

}
