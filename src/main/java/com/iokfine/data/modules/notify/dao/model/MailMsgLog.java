package com.iokfine.data.modules.notify.dao.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "t_mail_msg_log")
public class MailMsgLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    /**
     * 接收人
     */
    @Column(name = "mail_to")
    private String mailTo;

    /**
     * 主题
     */
    @Column(name = "subject")
    private String subject;
    /**
     * 内容
     */
    @Column(name = "content")
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "create_time")
    private Date createTime;

}
