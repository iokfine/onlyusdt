package com.iokfine.data.modules.user;

import com.iokfine.data.modules.security.utils.SecurityUtils;

public class UserHelper {
    public static Integer getCurrentUserId(){
        return SecurityUtils.getCurrentUser().getUser().getId();
    }
}
