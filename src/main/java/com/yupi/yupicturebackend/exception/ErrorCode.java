package com.yupi.yupicturebackend.exception;

import lombok.Getter;

/**
 * 定义错误码枚举类，用于统一错误码和错误信息
 */
@Getter
public enum ErrorCode {

    // 成功状态
    SUCCESS(0, "ok"),
    // 参数错误
    PARAMS_ERROR(40000, "请求参数错误"),
    // 未登录错误
    NOT_LOGIN_ERROR(40100, "未登录"),
    // 无权限错误
    NO_AUTH_ERROR(40101, "无权限"),
    // 请求数据不存在错误
    NOT_FOUND_ERROR(40400, "请求数据不存在"),
    // 禁止访问错误
    FORBIDDEN_ERROR(40300, "禁止访问"),
    // 系统内部异常错误
    SYSTEM_ERROR(50000, "系统内部异常"),
    // 操作失败错误
    OPERATION_ERROR(50001, "操作失败");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

    /**
     * 构造错误码枚举实例
     *
     * @param code    错误码
     * @param message 错误信息
     */
    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
