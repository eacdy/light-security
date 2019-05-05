package com.itmuch.lightsecurity.el;

import com.itmuch.lightsecurity.jwt.ReactiveUserOperator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;


/**
 * @author itmuch.com
 */
@Slf4j
@AllArgsConstructor
@SuppressWarnings("WeakerAccess")
public class ReactivePreAuthorizeExpressionRoot {
    private final ReactiveUserOperator userOperator;

    public Mono<Boolean> anon() {
        return Mono.just(true);
    }

    public Mono<Boolean> hasLogin() {
        return userOperator.getUser()
                .map(user -> true)
                .switchIfEmpty(Mono.just(false));
    }

    public Mono<Boolean> hasRole(String role) {
        return hasAnyRoles(role);
    }

    public Mono<Boolean> hasAllRoles(String... roles) {
        return userOperator.getUser()
                .map(user -> {
                    List<String> userRoles = user.getRoles();
                    if (CollectionUtils.isEmpty(userRoles)) {
                        return false;
                    }
                    List<String> roleList = Arrays.asList(roles);
                    boolean result = userRoles.containsAll(roleList);
                    if (!result) {
                        log.warn("hasAllRoles check failed. userRolesFromToken = {}, roles = {}", userRoles, roles);
                    }
                    return result;
                })
                .switchIfEmpty(Mono.just(false));
    }

    public Mono<Boolean> hasAnyRoles(String... roles) {
        return userOperator.getUser()
                .map(user -> {
                    List<String> userRoles = user.getRoles();


                    List<String> roleList = Arrays.asList(roles);
                    if (CollectionUtils.isEmpty(userRoles)) {
                        return false;
                    }
                    boolean result = userRoles.stream()
                            .anyMatch(roleList::contains);
                    if (!result) {
                        log.warn("hasAnyRoles check failed. userRolesFromToken = {}, roles = {}", userRoles, roles);
                    }
                    return result;
                })
                .switchIfEmpty(Mono.just(false));
    }
}
