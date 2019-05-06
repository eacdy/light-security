package com.itmuch.lightsecurity.spec;

import com.itmuch.lightsecurity.enums.HttpMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author itmuch.com
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Spec {
    /**
     * 请求方法
     */
    private HttpMethod httpMethod;
    /**
     * 路径
     */
    private String path;
    /**
     * 表达式
     * - hasLogin() 判断是否登录
     * - permitAll() 直接允许访问
     * - hasRole('角色名称') 判断是否具备指定角色
     * - hasAnyRole('角色1','角色2','角色3') 是否具备角色1/2/3中的任意一个角色
     * -
     */
    private String expression;
}
