package com.iokfine.data.modules.premint.dao.modal;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "t_premint_project")
@DynamicUpdate
public class PremintProject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "logo")
    private String logo;

    @Column(name = "name")
    private String name;

    @Column(name = "url")
    private String url;

    @Column(name = "reg_deadline")
    private String regDeadline;

    @Column(name = "mint_date")
    private String mintDate;

    @Column(name = "supply")
    private String supply;

    @Column(name = "winners")
    private String winners;

    @Column(name = "price")
    private String price;

    @Column(name = "requirement_eth")
    private String requirementETH;

    @Column(name = "type")
    private String type;

    @Column(name = "tab")
    private String tab;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "create_time")
    private Date createTime;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_time")
    private Date updateTime;

}
