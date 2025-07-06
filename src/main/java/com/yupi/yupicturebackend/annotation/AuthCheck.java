package com.yupi.yupicturebackend.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于方法级别的注解，旨在检查方法调用者的权限
 * 该注解主要用于安全控制，验证调用者是否具有执行某个方法所需的特定角色
 * 通过在方法上使用此注解并指定所需角色，可以在运行时动态检查调用者的权限
 *
 * @see #mustRole() 必须具有某个角色
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {

    /**
     * 必须具有的角色名称
     * 指定调用方法所需的特定角色如果未指定，默认为空字符串
     * 在运行时，权限检查逻辑将验证调用者是否具有此角色
     *
     * @return 必须具有的角色名称
     */
    String mustRole() default "";
}

