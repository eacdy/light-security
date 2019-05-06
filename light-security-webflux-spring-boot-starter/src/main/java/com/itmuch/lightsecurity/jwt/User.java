package com.itmuch.lightsecurity.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author itmuch.com
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    /**
     * id
     */
    private Integer id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 角色
     */
    private List<String> roles;
}