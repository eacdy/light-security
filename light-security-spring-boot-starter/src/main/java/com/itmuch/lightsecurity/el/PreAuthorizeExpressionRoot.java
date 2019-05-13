package com.itmuch.lightsecurity.el;

import com.itmuch.lightsecurity.jwt.User;
import com.itmuch.lightsecurity.jwt.UserOperator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;


/**
 * @author itmuch.com
 */
@Slf4j
@AllArgsConstructor
@SuppressWarnings({"WeakerAccess", "unused"})
public class PreAuthorizeExpressionRoot {
    private final UserOperator userOperator;

    /**
     * 匿名即可访问
     *
     * @return true
     */
    public boolean anon() {
        return true;
    }

    /**
     * 登录才能访问
     *
     * @return 如已登录，则返回true
     */
    public boolean hasLogin() {
        return userOperator.getUser() != null;
    }

    /**
     * 拥有指定角色才能访问
     *
     * @param role 角色
     * @return 如果拥有指定角色，则返回true
     */
    public boolean hasRole(String role) {
        return hasAnyRoles(role);
    }

    /**
     * 拥有所有指定角色才能访问
     *
     * @param roles 角色
     * @return 如果拥有roles所有角色，则返回true
     */
    public boolean hasAllRoles(String... roles) {
        User user = userOperator.getUser();
        if (user == null) {
            return false;
        }

        List<String> userRoles = user.getRoles();
        if (CollectionUtils.isEmpty(userRoles)) {
            return false;
        }
        List<String> roleList = Arrays.asList(roles);
        return userRoles.containsAll(roleList);
    }

    /**
     * 拥有指定角色之一即可访问
     *
     * @param roles 角色
     * @return 如果拥有roles元素之一，则返回true
     */
    public boolean hasAnyRoles(String... roles) {
        User user = userOperator.getUser();
        if (user == null) {
            return false;
        }

        List<String> userRoles = user.getRoles();
        List<String> roleList = Arrays.asList(roles);
        if (CollectionUtils.isEmpty(userRoles)) {
            return false;
        }

        boolean checkResult = userRoles.stream()
                .anyMatch(roleList::contains);
        if (!checkResult) {
            log.warn("角色不匹配，userRolesFromToken = {}, roles = {}", userRoles, roleList);
        }
        return checkResult;
    }
}
