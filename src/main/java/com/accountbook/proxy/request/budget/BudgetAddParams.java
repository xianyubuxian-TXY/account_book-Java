package com.accountbook.proxy.request.budget;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 新增预算请求参数类（适配新表字段`total_budget`）
 * 前端需传递月份、分类ID和总预算金额，实际支出（`spent`）和剩余预算（`remaining`）由后端初始化
 */
public class BudgetAddParams {
    private String month;           // 月份（格式：YYYY-MM，如"2025-10"）
    private Integer categoryId;     // 关联的大类ID
    private BigDecimal totalBudget; // 总预算金额（匹配数据库`total_budget`字段）

    public BudgetAddParams() {}

    public BudgetAddParams(String month, Integer categoryId, BigDecimal totalBudget) {
        this.month = month;
        this.categoryId = categoryId;
        this.totalBudget = totalBudget;
    }

    /**
     * 转换为数据库字段映射（`spent`默认0.00，`remaining`由后端计算）
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("month", this.month);
        map.put("category_id", this.categoryId);
        map.put("total_budget", this.totalBudget);
        map.put("spent", BigDecimal.ZERO); // 实际支出默认0.00
        map.put("remaining", this.totalBudget.subtract(BigDecimal.ZERO)); // 剩余预算初始为总预算
        return map;
    }

    // Getter/Setter（字段名与数据库完全对齐）
    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public BigDecimal getTotalBudget() { return totalBudget; }
    public void setTotalBudget(BigDecimal totalBudget) { this.totalBudget = totalBudget; }
}