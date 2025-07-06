package com.yupi.yupicturebackend.aop;

import com.yupi.yupicturebackend.annotation.AuthCheck;
import com.yupi.yupicturebackend.exception.BusinessException;
import com.yupi.yupicturebackend.exception.ErrorCode;
import com.yupi.yupicturebackend.model.entity.User;
import com.yupi.yupicturebackend.model.enums.UserRoleEnum;
import com.yupi.yupicturebackend.service.UserService;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 权限校验切面类 —— 拦截带有 @AuthCheck 注解的方法
 */
@Aspect // 标识为切面类
@Component
public class AuthInterceptor {

    // 注入用户服务，用于获取当前登录用户
    @Resource
    private UserService userService;

    /**
     * 核心权限校验逻辑（切面方法）
     *
     * @param joinPoint 切入点对象，表示被拦截的方法
     * @param authCheck 注解对象，用于获取注解中配置的参数
     * @return 被拦截方法的返回值（可能被拦截或放行）
     * @throws Throwable 若被拦截的方法抛出异常，需继续向上传递
     */
    @Around("@annotation(authCheck)") // 表示拦截带有 @AuthCheck 注解的方法
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {

        // 从注解中获取必须具备的角色
        String mustRole = authCheck.mustRole();

        // 获取当前请求的上下文信息（用于拿到 HttpServletRequest）
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        // 从请求中获取当前已登录用户
        User loginUser = userService.getLoginUser(request);

        // 将注解中定义的角色（字符串）转换为枚举类型
        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);

        // 如果注解中没有配置必须角色（为 null），则说明该接口不需要权限校验，直接放行
        if (mustRoleEnum == null) {
            return joinPoint.proceed(); // 调用原方法
        }

        // 获取当前用户的角色，转换为枚举
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());

        // 当前用户角色无效，抛出无权限异常
        if (userRoleEnum == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 如果接口要求必须是管理员，而当前用户不是管理员，抛出无权限异常
        if (UserRoleEnum.ADMIN.equals(mustRoleEnum) && !UserRoleEnum.ADMIN.equals(userRoleEnum)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 权限校验通过，放行执行原方法
        return joinPoint.proceed();
    }
}
