package com.accountbook.proxy;

import java.io.Serializable;

/**
 * 后端统一响应对象，用于返回给前端渲染
 * 仅通过success判断操作成败，结构简洁，聚焦核心结果反馈
 * @param <T> 响应业务数据类型（如账单详情、操作结果）
 */
public class BackendResponse<T> implements Serializable {
    // 序列化版本号，确保前后端传输稳定
    private static final long serialVersionUID = 1L;

    // 核心判断：true=操作成功，false=操作失败
    private boolean success;
    // 响应消息：成功/失败的具体描述（前端可直接展示，如“删除账单成功”“账单ID不能为空”）
    private String message;
    // 响应数据：成功时返回具体业务数据（如账单列表、预算详情）
    private T data;

    /**
     * 快速创建【成功】响应（带业务数据+默认提示）
     * @param data 业务数据（如BillSingleResponse、BudgetListResponse）
     * @return 成功响应对象
     */
    public static <T> BackendResponse<T> success(T data) {
        return new BackendResponse<>(true, "处理成功", data);
    }

    /**
     * 快速创建【成功】响应（带业务数据+自定义提示）
     * @param message 自定义成功提示（如“新增预算成功，本月预算已生效”）
     * @param data 业务数据
     * @return 成功响应对象
     */
    public static <T> BackendResponse<T> success(String message, T data) {
        return new BackendResponse<>(true, message, data);
    }

    /**
     * 快速创建【失败】响应（带自定义失败提示）
     * @param message 失败原因描述（如“账单ID不存在，删除失败”“金额不能为负数”）
     * @return 失败响应对象（data默认null）
     */
    public static <T> BackendResponse<T> fail(String message) {
        return new BackendResponse<>(false, message, null);
    }

    /**
     * 私有构造：通过静态方法创建响应，避免手动设置success导致状态混乱
     * 确保响应对象创建时，success、message、data的逻辑一致性
     */
    private BackendResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Getter：响应对象创建后不可修改，避免后续代码篡改结果（无Setter）
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}

