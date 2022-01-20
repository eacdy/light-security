package com.itmuch.lightsecurity.util;

import com.itmuch.lightsecurity.constants.ConstantsSecurity;
import com.itmuch.lightsecurity.jwt.LoginUser;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public class UserContextUtil {
    public static LoginUser getUser() {
        HttpServletRequest request = WebUtil.getRequest();
        Object user = request.getAttribute(ConstantsSecurity.LIGHT_SECURITY_REQ_ATTR_USER);
        return (LoginUser) user;
    }

    public static Integer getUserId() {
        LoginUser loginUser = getUser();
        return Optional.ofNullable(loginUser)
                .map(LoginUser::getId)
                .orElse(null);
    }
}
