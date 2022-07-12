package com.iokfine.data.modules.notify.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

@Slf4j
@Service
public class EmailClient {

    @Async
    public void gmailSender(String to, String subject, String context) {
        if(StringUtils.isBlank(to)){
            to = "307321458@qq.com";
        }
        // Get a Properties object
        Properties props = new Properties();
        //选择ssl方式
        props.put("mail.debug", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.auth", "true");
        //gmail邮箱
        final String sendEmail = "iokfine.quantize@gmail.com";
        //密码
        final String password = "fE*n8*li7XL3Lvta";
        Session session = Session.getDefaultInstance(props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(sendEmail, password);
                    }
                });
        // -- Create a new message --
        Message msg = new MimeMessage(session);

        // -- Set the FROM and TO fields --
        try {
            msg.setFrom(new InternetAddress(sendEmail));
            msg.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));
            msg.setSubject(subject);
            msg.setText(context);
            msg.setSentDate(new Date());
            Transport.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("发送邮件到:"+to+" 标题 :"+subject+" 内容 :"+context);
    }

}
