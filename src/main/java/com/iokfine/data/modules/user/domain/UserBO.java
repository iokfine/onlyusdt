package com.iokfine.data.modules.user.domain;

import lombok.Data;

/**
 * @author hjx
 * @date 2022/9/30
 */
@Data
public class UserBO {
    private Integer id;

    private String userName;

    private String refferid;

    private String dueTime;

    private Integer accountMax;

    private Boolean available =true;

}
