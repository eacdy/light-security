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
public class PreAuthorizeExpressionRoot {
    private final UserOperator userOperator;

    public boolean hasLogin() {
        return userOperator.getUser() != null;
    }

    public boolean hasRole(String role) {
        return hasAnyRoles(role);
    }

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
            log.warn("角色不匹配，userRolesFromToken = {}, roles = {}", userRoles, roles);
        }
        return checkResult;
    }
}
