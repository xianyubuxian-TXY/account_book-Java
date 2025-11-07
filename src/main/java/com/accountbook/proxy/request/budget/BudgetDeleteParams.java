package com.accountbook.proxy.request.budget;

/**
 * 删除预算请求参数类（参考BillDeleteParams设计风格）
 * 删除预算只需预算ID（主键，唯一标识）
 */
public class BudgetDeleteParams {
    private Integer budgetId; // 要删除的预算ID（主键，唯一标识）

    /**
     * 构造方法：必须传递预算ID
     * @param budgetId 预算ID（正整数，非空）
     */
    public BudgetDeleteParams(Integer budgetId) {
        this.budgetId = budgetId;
    }

    // getter/setter
    public Integer getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(Integer budgetId) {
        this.budgetId = budgetId;
    }
}