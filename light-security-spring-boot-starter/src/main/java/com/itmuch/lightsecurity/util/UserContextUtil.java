package com.itmuch.lightsecurity.util;

import com.itmuch.lightsecurity.constants.ConstantsSecurity;
import com.itmuch.lightsecurity.jwt.LoginUser;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
/**
 * @author itmuch.com
 */
public class UserContextUtil {
    /**
     * 获取当前登录用户信息
     * @return 当前登录用户信息
     */
    public static LoginUser getUser() {
        HttpServletRequest request = WebUtil.getRequest();
        Object user = request.getAttribute(ConstantsSecurity.LIGHT_SECURITY_REQ_ATTR_USER);
        return (LoginUser) user;
    }

    /**
     * 获取当前登录用户的id
     * @return 当前登录用户id
     */
    public static Integer getUserId() {
        LoginUser loginUser = getUser();
        return Optional.ofNullable(loginUser)
                .map(LoginUser::getId)
                .orElse(null);
    }
}
