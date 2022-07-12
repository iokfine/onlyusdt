package com.iokfine.data.modules.notify.rest;

import cn.hutool.core.util.RandomUtil;
import com.iokfine.data.modules.cache.FastRedisService;
import com.iokfine.data.modules.cache.RedisKeyConstant;
import com.iokfine.data.modules.core.domain.annotation.rest.AnonymousPostMapping;
import com.iokfine.data.modules.notify.domain.CodeParma;
import com.iokfine.data.modules.notify.service.MailServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
public class MailNotifyController {

    @Resource
    private MailServer mailServer;
    @Resource
    private FastRedisService fastRedisService;

    @AnonymousPostMapping("/mail/verifyCode")
    public String extractWord(@RequestBody  CodeParma codeParma) {
        String str = "欢迎使用 星星小店！<br>" +
                "<br>" +
                "您的验证码是：%s<br>" +
                "<br>" +
                "请勿泄露<br>";
        String code = RandomUtil.randomString(6);
        log.info("验证码 {}",code);
        fastRedisService.set(String.format(RedisKeyConstant.REG_VERIFY_CODE,codeParma.getEmail()),code,5,TimeUnit.MINUTES);
        mailServer.send(codeParma.getEmail(), "星星小店注册", String.format(str,code));
        return "SUCCESS";
    }

}