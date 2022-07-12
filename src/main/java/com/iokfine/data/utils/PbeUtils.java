package com.iokfine.data.utils;

import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

@Slf4j
public class PbeUtils {

    static String password = "iokfine.com";


    public static String PBEencrypt(String target,String salt) {
        try {

            // 加 密 口令与密钥
            PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBEWITHMD5andDES");
            Key key = factory.generateSecret(pbeKeySpec);

            //加密
            PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(Base64.decodeBase64(salt), 100);
            Cipher cipher = Cipher.getInstance("PBEWITHMD5andDES");
            cipher.init(Cipher.ENCRYPT_MODE, key, pbeParameterSpec);
            byte[] result = cipher.doFinal(target.getBytes());
            return Base64.encodeBase64String(result);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("加密失败");
        }
        return "";
    }

    /**
     * @param encryptStr
     * @return
     */
    public static String PBEdecrypt(String encryptStr,String secret) {
        try {
            //初始化盐
            byte[] salt = Base64.decodeBase64(secret);
            // 加 密 口令与密钥
            PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBEWITHMD5andDES");
            Key key = factory.generateSecret(pbeKeySpec);

            PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(salt, 100);
            Cipher cipher = Cipher.getInstance("PBEWITHMD5andDES");
            cipher.init(Cipher.DECRYPT_MODE, key, pbeParameterSpec);
            byte[] resultBytes = cipher.doFinal(Base64.decodeBase64(encryptStr));
            String result = new String(resultBytes);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}