package com.yupi.yupicturebackend.common;

import com.yupi.yupicturebackend.exception.ErrorCode;
import lombok.Data;

import java.io.Serializable;

/**
 * 全局响应封装类
 *
 * 该类用于全局响应处理，封装了响应的状态码、数据和消息
 * 实现Serializable接口以支持对象的序列化和反序列化
 *
 * @param <T> 泛型参数，表示响应数据的类型
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code;  // 响应状态码

    private T data;    // 响应数据

    private String message;   // 响应消息

    /**
     * 构造方法
     *
     * 初始化BaseResponse对象，设置响应的状态码、数据和消息
     *
     * @param code    响应状态码
     * @param data    响应数据
     * @param message 响应消息
     */
    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    /**
     * 构造方法
     *
     * 当响应消息为空字符串时使用的构造方法
     *
     * @param code 响应状态码
     * @param data 响应数据
     */
    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    /**
     * 构造方法
     *
     * 当响应为错误码时使用的构造方法
     *
     * @param errorCode 错误码对象，包含错误状态码和错误消息
     */
    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}

