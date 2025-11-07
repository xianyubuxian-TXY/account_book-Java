package com.accountbook.proxy.request.budget;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 修改预算请求参数类（参考BillChangeParams设计风格）
 * 以id作为唯一标识，其他字段均为可选，支持部分字段修改
 */
public class BudgetChangeParams {
    // 必填字段：待修改的预算ID（主键，唯一标识）
    private Integer budgetId;
    // 可选字段：新的月份（格式：YYYY-MM）
    private String month;
    // 可选字段：新的分类ID
    private Integer categoryId;
    // 可选字段：新的总预算金额
    private BigDecimal totalBudget;

    /**
     * 构造方法：必填预算ID，其他字段通过Setter动态设置（支持部分修改）
     * @param budgetId 待修改的预算ID（必填，正整数）
     */
    public BudgetChangeParams(Integer budgetId) {
        this.budgetId = budgetId;
    }

    /**
     * 全参构造方法（可选，适用于一次性设置所有字段的场景）
     */
    public BudgetChangeParams(Integer budgetId, String month, Integer categoryId, BigDecimal totalBudget) {
        this.budgetId = budgetId;
        this.month = month;
        this.categoryId = categoryId;
        this.totalBudget = totalBudget;
    }

    /**
     * 将参数转换为 Map（键为数据库字段名，值为字段值）
     * 用于 SQL 更新操作的参数绑定，仅包含非空字段（避免覆盖原有值）
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        if (this.month != null) map.put("month", this.month);
        if (this.categoryId != null) map.put("category_id", this.categoryId);
        if (this.totalBudget != null) map.put("total_budget", this.totalBudget);
        // 实际支出（spent）和剩余预算（remaining）由后端自动计算，不允许前端修改
        return map;
    }

    /**
     * 获取预算ID的Map（用于SQL的WHERE条件绑定）
     */
    public Map<String, Object> getBudgetIdMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", this.budgetId);
        return map;
    }

    // 紧凑格式的 Getter/Setter
    public Integer getBudgetId() { return budgetId; }
    public void setBudgetId(Integer budgetId) { this.budgetId = budgetId; }
    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public BigDecimal getTotalBudget() { return totalBudget; }
    public void setTotalBudget(BigDecimal totalBudget) { this.totalBudget = totalBudget; }
}