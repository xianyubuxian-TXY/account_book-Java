package com.accountbook.proxy.response.budget;

/**
 * 预算删除响应：适配删除操作（以预算ID为唯一标识）
 */
public class BudgetDeleteResponse {
    private Integer budgetId; // 被删除的预算ID（主键）

    /**
     * 构造方法：接收被删除的预算ID
     */
    public BudgetDeleteResponse(Integer budgetId) {
        this.budgetId = budgetId;
    }

    // 仅提供Getter（无Setter），确保结果不可篡改
    public Integer getBudgetId() {
        return budgetId;
    }
}