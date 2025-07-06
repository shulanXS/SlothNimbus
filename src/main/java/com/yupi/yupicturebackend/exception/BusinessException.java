package com.yupi.yupicturebackend.exception;

import lombok.Getter;

/**
 * 自定义业务异常类
 * 用于处理业务逻辑中的异常情况，提供了错误码和错误信息
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误码，用于标识具体的错误类型
     */
    private final int code;

    /**
     * 构造函数，根据错误码和错误信息创建业务异常实例
     * @param code 错误码
     * @param message 错误信息
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 构造函数，根据错误码对象创建业务异常实例
     * @param errorCode 错误码对象，包含错误码和错误信息
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    /**
     * 构造函数，根据错误码对象和自定义错误信息创建业务异常实例
     * @param errorCode 错误码对象，包含错误码和默认错误信息
     * @param message 自定义错误信息
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

}

