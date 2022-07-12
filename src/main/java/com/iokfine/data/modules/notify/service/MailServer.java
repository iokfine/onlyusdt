package com.iokfine.data.modules.notify.service;

import com.iokfine.data.modules.notify.dao.model.MailMsgLog;
import com.iokfine.data.modules.notify.dao.repository.MailMsgLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

/**
 * @author hjx
 * @date 2021/8/18
 */
@Service
@Slf4j
public class MailServer {

    @Resource
    private JavaMailSender mailSender;

    @Resource
    private MailMsgLogRepository mailMsgLogRepository;

    @Value("${spring.mail.username}")
    private String from;
    @Value("${appName}")
    private String appName;

    private String[] cs = {};

    public String send(String to,String subject,String context) {
        if(StringUtils.isBlank(to)){
            to = "307321458@qq.com";
        }
        MimeMessage message = mailSender.createMimeMessage();

        try {
            String nick = "";
            nick = MimeUtility.decodeText(appName);
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(nick+"<"+from+">");
            helper.setTo(to);
            if (cs.length > 0) {
                helper.setCc(cs);
            }
            helper.setSubject(subject);
            helper.setText(context, true);
            long start = System.currentTimeMillis();
             mailSender.send(message);
            log.info("发送邮件到:"+to+" 标题 :"+subject+" 内容 :"+context+" 耗时："+(System.currentTimeMillis() - start)+" ms");

            MailMsgLog mailMsgLog = new MailMsgLog();
            mailMsgLog.setMailTo(to);
            mailMsgLog.setSubject(subject);
            mailMsgLog.setContent(context);
            mailMsgLogRepository.save(mailMsgLog);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "SUCCESS";
    }
}
