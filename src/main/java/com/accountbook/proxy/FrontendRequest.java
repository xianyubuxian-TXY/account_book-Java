package com.accountbook.proxy;

/**
 * 前端请求封装类：统一封装请求类型、业务参数和搜索条件
 */
public class FrontendRequest<T> {
    // 请求类型枚举（补充批量更新类型，覆盖所有业务操作）
    public enum RequestType {
        // 账单相关
        ADD_BILL,         // 增加账单
        DELETE_BILL,      // 删除账单
        CHANGE_BILL,      // 修改单个账单
        BATCH_UPDATE_BILL,// 批量更新账单（新增，用于类型删除时迁移关联账单）
        SEARCH_BILL,      // 查找账单
        STATISTIC_BILL,   // 统计账单
        VISUAL_BILL,      // 可视化账单

        // 大类相关
        ADD_CATEGORY,     // 增加大类
        DELETE_CATEGORY,  // 删除大类
        CHANGE_CATEGORY,  // 修改大类
        SEARCH_CATEGORY,  // 查询大类

        // 具体类型相关
        ADD_SPECIFIC_TYPE,    // 增加具体类型
        DELETE_SPECIFIC_TYPE, // 删除具体类型
        CHANGE_SPECIFIC_TYPE, // 修改具体类型
        SEARCH_SPECIFIC_TYPE, // 查询具体类型

        // 预算相关
        ADD_BUDGET,       // 增加预算
        DELETE_BUDGET,    // 删除预算
        CHANGE_BUDGET,    // 修改预算
        SEARCH_BUDGET     // 查找预算
    }

    private final RequestType type;         // 请求类型（不可修改）
    private final T params;                // 业务参数（如新增/修改的具体数据）
    private final Object searchParams;     // 搜索条件参数（用于批量操作时的筛选，可选）

    // 基础构造器：适用于无需搜索条件的请求（如新增、单条修改、删除）
    public FrontendRequest(RequestType type, T params) {
        this.type = type;
        this.params = params;
        this.searchParams = null;
    }

    // 扩展构造器：适用于需要搜索条件的请求（如批量更新、带条件的查询）
    public FrontendRequest(RequestType type, T params, Object searchParams) {
        this.type = type;
        this.params = params;
        this.searchParams = searchParams;
    }

    // Getter方法（只提供查询，不允许修改）
    public RequestType getType() {
        return type;
    }

    public T getParams() {
        return params;
    }

    public Object getSearchParams() {
        return searchParams;
    }

    // 移除setter方法：确保请求对象不可变（创建后不能修改类型和参数，保证数据一致性）
}