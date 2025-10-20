package com.accountbook.proxy;

import java.io.Serializable;

/**
 * 后端统一响应对象，用于返回给前端渲染
 * 采用标准JSON结构：状态码 + 消息 + 泛型数据
 */
public class BackendResponse<T> implements Serializable {
    // 序列化版本号，确保前后端传输稳定
    private static final long serialVersionUID = 1L;

    /**
     * 响应状态码枚举
     * 扩展了具体状态码值，方便前端快速判断（如HTTP状态码规范）
     */
    public enum Code {
        OK(200, "处理成功"),
        ERROR(500, "处理失败"),
        INVALID_REQUEST(400, "无效请求参数"),
        NOT_FOUND(404, "资源不存在"),
        UNAUTHORIZED(401, "未授权访问");

        private final int code;       // 数字状态码（前端可通过此判断，比字符串更高效）
        private final String defaultMsg; // 默认描述

        Code(int code, String defaultMsg) {
            this.code = code;
            this.defaultMsg = defaultMsg;
        }

        public int getCode() {
            return code;
        }

        public String getDefaultMsg() {
            return defaultMsg;
        }
    }

    // 状态码（数字形式，前端判断更方便）
    private int code;
    // 响应消息（成功/失败描述，前端可直接展示）
    private String message;
    // 响应数据（泛型类型，明确数据类型，避免前端类型转换错误）
    private T data;

    /**
     * 快速创建成功响应（带数据）
     */
    public static <T> BackendResponse<T> success(T data) {
        return new BackendResponse<>(Code.OK, Code.OK.getDefaultMsg(), data);
    }

    /**
     * 快速创建成功响应（自定义消息+数据）
     */
    public static <T> BackendResponse<T> success(String message, T data) {
        return new BackendResponse<>(Code.OK, message, data);
    }

    /**
     * 快速创建失败响应（基于错误类型）
     */
    public static <T> BackendResponse<T> fail(Code errorCode, String message) {
        return new BackendResponse<>(errorCode, message, null);
    }

    /**
     * 快速创建失败响应（默认消息）
     */
    public static <T> BackendResponse<T> fail(Code errorCode) {
        return new BackendResponse<>(errorCode, errorCode.getDefaultMsg(), null);
    }

    // 私有构造，通过静态方法创建响应，确保状态一致
    private BackendResponse(Code code, String message, T data) {
        this.code = code.getCode();
        this.message = message;
        this.data = data;
    }

    // Getter（无需Setter，响应对象创建后不可修改，避免数据混乱）
    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}