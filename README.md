# Light Security

Light Security是一款基于 `jwt` 的、简洁而不简单的权限控制框架，可与 `Spring Boot` 配合使用，支持 `Spring MVC` 及 `WebFlux` 。



## 地址

* [GitHub](https://github.com/eacdy/light-security)
* [Gitee](https://gitee.com/itmuch/light-security)



## 特点

### 优点

- 上手快速
- **开箱即用**
- **轻量级，代码精简，不到500行代码**；
- **功能实用，市面上安全框架常见能力与套路均已具备**：
  - 支持 `RESTful` 权限控制
  - 支持灵活的权限配置(**代码配置方式优先级更高**)
    - 支持基于配置文件的权限配置
    - 支持基于代码的权限控制
  - 支持基于注解的权限控制
- 设计简单，没有复杂概念；
  - Spring Web编程模型
    - 基于权限配置的方式：核心是1个拦截器
    - 基于注解的权限控制：核心是1个切面
  - WebFlux编程模型
    - 基于权限配置的方式：核心是1个过滤器
    - 基于注解的权限控制：核心是1个切面

### 缺点

* 功能
  * 比 Spring Security 弱一点
  * 和Shiro比功能差不多，但没有实现复杂的Authentication Strategy（想实现也很简单，详见扩展点）
* **只考虑权限相关问题**
  * 不考虑身份认证(登录)，意味着登录逻辑得自己玩；
  * 不考虑防攻击，意味着网络攻击得自己防；



## 依赖

* Spring MVC：用到Spring MVC的拦截器，如只使用基于注解的权限控制，则无需该部分依赖；
* Spring WebFlux：用到WebFlux的Filter，如只使用基于注解的权限控制，则无需该部分依赖；
* Spring AOP：如果不用基于注解的权限控制，则无需该部分依赖；
* jwt：你懂的



## 快速上手

### Spring Web编程模型

> **TIPS**
>
> 快速上手可详见项目 `light-security-example` 目录，内附详细测试步骤。

#### 基于配置文件的权限配置

* 加依赖：

  ```xml
  <dependency>
      <groupId>com.itmuch.security</groupId>
      <artifactId>light-security-spring-boot-starter</artifactId>
      <version>1.1.0-RELEASE</version>
  </dependency>
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-aop</artifactId>
  </dependency>
  ```

* 写配置

  ```yaml
  server:
    port: 8009
  light-security:
    # 权限规则配置：表示用{http-method}方法请求的{path}路径必须具备什么{expression}
    spec-list:
      - http-method: ANY
        path: /login
        expression: "anon()"
      - http-method: ANY
        path: /user
        expression: "hasAnyRoles('user','admin')"
      - http-method: ANY
        path: /user-no-access
        expression: "hasAllRoles('user','admin','xx')"
      - http-method: GET
        path: /error
        expression: "anon()"
      - http-method: ANY
        path: /**
        expression: "hasLogin()"
    jwt:
      # jwt sign算法
      algorithm: hs512
      # jwt secret
      secret: {secret}
      # jwt 有效时间
      expiration-in-second: 1209600
  ```

* 写代码：

  ```java
  @RequestMapping
  @RestController
  @RequiredArgsConstructor(onConstructor = @__(@Autowired))
  public class TestController {
      private final UserOperator userOperator;
      private final JwtOperator operator;
  
      /**
       * 演示如何获取当前登录用户信息
       * - 该路径需要具备user或admin权限才可访问，详见application.yml
       *
       * @return 用户信息
       */
      @GetMapping("/user")
      public User user() {
          return userOperator.getUser();
      }
  
      @GetMapping("/user-no-access")
      public User userNoAccess() {
          return userOperator.getUser();
      }
  
      /**
       * 演示基于注解的权限控制
       *
       * @return 如果有权限返回 亲，你同时有user、admin角色..
       */
      @GetMapping("/annotation-test")
      @PreAuthorize("hasAllRoles('user','admin')")
      public String annotationTest() {
          return "亲，你同时有user、admin角色..";
      }
  
      @GetMapping("/annotation-test-no-access")
      @PreAuthorize("hasAllRoles('user','admin','xx')")
      public String annotationTestNoAccess() {
          return "亲，你同时有user、admin、xx角色..";
      }
  
      /**
       * 模拟登录，颁发token
       *
       * @return token字符串
       */
    @GetMapping("/login")
      public String loginReturnToken() {
          User user = User.builder()
                  .id(1)
                  .username("张三")
                  .roles(Arrays.asList("user", "admin"))
                  .build();
          return operator.generateToken(user);
      }
  }
  ```
  
  

#### 基于代码的权限配置

```java
@Configuration
public class LightSecurityConfigurtion {
    @Bean
    public SpecRegistry specRegistry() {
        return new SpecRegistry()
                .add(HttpMethod.GET, "/user", "hasAnyRoles('user')")
                .add(HttpMethod.ANY, "/**", "hasLogin()");
    }
}
```

此时，`application.yml` 中的如下配置可删除，**因为代码配置方式优先级更高，配置文件方式将会失效**。

```yaml
light-security:
  # 权限规则配置：表示用{http-method}方法请求的{path}路径必须具备什么{expression}
  spec-list:
    - http-method: ANY
      path: /login
      expression: "anon()"
    - http-method: ANY
      path: /user
      expression: "hasAnyRoles('user','admin')"
    - http-method: ANY
      path: /user-no-access
      expression: "hasAllRoles('user','admin','xx')"
    - http-method: GET
      path: /error
      expression: "anon()"
    - http-method: ANY
      path: /**
      expression: "hasLogin()"
```



#### 扩展点

| 类                                                           | 作用                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| com.itmuch.lightsecurity.jwt.UserOperator                    | 提供用户相关操作，例如解析token获得用户信息等。              |
| com.itmuch.lightsecurity.el.PreAuthorizeExpressionRoot       | 提供表达式支持，例如`hasAnyRoles('user')` 等，如需新能力，只需编写新方法即可 |
| com.itmuch.lightsecurity.annotation.support.PreAuthorizeAspect | 为注解 `@PreAuthorize("hasAllRoles('user','admin')")`提供支持 |



### WebFlux编程模型

> **TIPS**
>
> 快速上手可详见项目 `light-security-webflux-example` 目录，内附详细测试步骤。

#### 基于配置文件的权限配置

* 加依赖

  ```xml
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
  </dependency>
  <dependency>
    <groupId>com.itmuch.security</groupId>
    <artifactId>light-security-webflux-spring-boot-starter</artifactId>
    <version>1.1.0-RELEASE</version>
  </dependency>
  ```

* 写配置

  ```yaml
  server:
    port: 8009
  light-security:
    # 权限规则配置：表示用{http-method}方法请求的{path}路径必须具备什么{expression}
    spec-list:
      - http-method: ANY
        path: /login
        expression: "anon()"
      - http-method: ANY
        path: /user
        expression: "hasAnyRoles('user','admin')"
      - http-method: ANY
        path: /user-no-access
        expression: "hasAllRoles('user','admin','xx')"
      - http-method: GET
        path: /error
        expression: "anon()"
      - http-method: ANY
        path: /**
        expression: "hasLogin()"
    jwt:
      # jwt sign算法
      algorithm: hs512
      # jwt secret
      secret: {secret}
      # jwt 有效时间
      expiration-in-second: 1209600
  ```

* 写代码

  ```java
  @RequestMapping
  @RestController
  @RequiredArgsConstructor(onConstructor = @__(@Autowired))
  public class TestController {
      private final ReactiveUserOperator userOperator;
      private final JwtOperator operator;
  
      /**
       * 演示如何获取当前登录用户信息
       * - 该路径需要具备user或admin权限才可访问，详见application.yml
       *
       * @return 用户信息
       */
      @GetMapping("/user")
      public Mono<User> user() {
          return userOperator.getUser();
      }
  
      @GetMapping("/user-no-access")
      public Mono<User> userNoAccess() {
          return userOperator.getUser();
      }
  
      /**
       * 演示基于注解的权限控制
       *
       * @return 如果有权限返回 亲，你同时有user、admin角色..
       */
      @GetMapping("/annotation-test")
      @PreAuthorize("hasAllRoles('user','admin')")
      public Mono<String> annotationTest() {
          return Mono.just("亲，你同时有user、admin角色..");
      }
  
      @GetMapping("/annotation-test-no-access")
      @PreAuthorize("hasAllRoles('user','admin','xx')")
      public Mono<String> annotationTestNoAccess() {
          return Mono.just("亲，你同时有user、admin、xx角色..");
      }
  
      /**
       * 模拟登录，颁发token
       *
       * @return token字符串
       */
      @GetMapping("/login")
      public String loginReturnToken() {
          User user = User.builder()
                  .id(1)
                  .username("张三")
                  .roles(Arrays.asList("user", "admin"))
                  .build();
          return operator.generateToken(user);
      }
  }
  ```



#### 基于代码的权限配置

```java
@Configuration
public class LightSecurityConfigurtion {
    @Bean
    public SpecRegistry specRegistry() {
        return new SpecRegistry()
                .add(HttpMethod.GET, "/user", "hasAnyRoles('user')")
                .add(HttpMethod.ANY, "/**", "hasLogin()");
    }
}
```

此时，`application.yml` 中的如下配置可删除，**因为代码配置方式优先级更高，配置文件方式将会失效**。

```yaml
light-security:
  # 权限规则配置：表示用{http-method}方法请求的{path}路径必须具备什么{expression}
  spec-list:
    - http-method: ANY
      path: /login
      expression: "anon()"
    - http-method: ANY
      path: /user
      expression: "hasAnyRoles('user','admin')"
    - http-method: ANY
      path: /user-no-access
      expression: "hasAllRoles('user','admin','xx')"
    - http-method: GET
      path: /error
      expression: "anon()"
    - http-method: ANY
      path: /**
      expression: "hasLogin()"
```

#### 扩展点

| 类                                                           | 作用                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| com.itmuch.lightsecurity.jwt.ReactiveUserOperator            | 提供用户相关操作，例如解析token获得用户信息等。              |
| com.itmuch.lightsecurity.el.ReactivePreAuthorizeExpressionRoot | 提供表达式支持，例如`hasAnyRoles('user')` 等，如需新能力，只需编写新方法即可 |
| com.itmuch.lightsecurity.annotation.support.ReactivePreAuthorizeAspect | 为注解 `@PreAuthorize("hasAllRoles('user','admin')")`提供支持 |



## 常见问题

### 为什么要造这个轮子？

老是有人问我诸如"微服务安全怎么管理？"、"Spring Security xxxx问题你遇到过吗？"、"能写个Spring Cloud Security的系列教程吗？"、"Shiroxxxx问题你遇到过吗？"

烦不胜烦，初期积极回复；后来消极回复；再后来懒得回复。

分析一下，发现主要原因还是Spring Security、Shiro学习曲线较高，特别是Spring Security。所以就想写个轻量的框架，能够快速解决主要矛盾——足够简单、能实现权限控制。



### 为什么不考虑身份认证(登录)？

目前市面上大多权限框架都考虑了"身份认证(登录)" + "权限管理" 。然而登录操作在现在这个时代，是一个"五花八门"的操作。例如：

* 手机号 + 验证码登录
* 扫二维码登录
* 账号密码登录
* 证书登录

往往还需还同时支持多种登录方式。这挺难去抽象出通用模式，并为典型的登录方式提供支持。

索性不考虑了——把登录问题留给使用者自己。用户可根据业务需求实现登录逻辑，并颁发Token，后面的事情就交给 `Light Security` ，让它给你搞定。这样相对更加灵活，更重要的是——你也不再需要去学习用框架应该怎么登录。



## TODO

* 支持对称加密/非对称加密配置化；
* 支持 `JWE` 
* 补充单元测试
* 将框架与starter分离，否则既是框架，又是Starter感觉有点怪。
