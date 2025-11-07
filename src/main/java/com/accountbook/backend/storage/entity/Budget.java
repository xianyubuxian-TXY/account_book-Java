package com.accountbook.backend.storage.entity;

import java.math.BigDecimal;

/**
 * 预算实体类：与数据库表字段完全对齐
 */
public class Budget {
    private Integer id;          // 主键自增
    private Integer categoryId;  // 分类ID（非空）
    private String month;        // 月份（YYYY-MM，非空）
    private BigDecimal totalBudget; // 总预算（非空）
    private BigDecimal spent;    // 已支出（默认0.00）
    private BigDecimal remaining; // 剩余预算（非空）

    public Budget() {}

    /**
     * 前端初始化用构造方法：仅需传入月份、分类ID、总预算，已支出默认0，剩余预算自动计算
     */
    public Budget(String month, Integer categoryId, BigDecimal totalBudget) {
        this.month = month;
        this.categoryId = categoryId;
        this.totalBudget = totalBudget;
        this.spent = BigDecimal.ZERO;
        this.remaining = totalBudget.subtract(this.spent);
    }

    /**
     * 后端初始化用构造方法：接收所有字段（含主键ID）
     */
    public Budget(Integer id, String month, Integer categoryId, BigDecimal totalBudget, BigDecimal spent, BigDecimal remaining) {
        this.id = id;
        this.month = month;
        this.categoryId = categoryId;
        this.totalBudget = totalBudget;
        this.spent = spent;
        this.remaining = remaining;
    }

    // Getter + Setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }
    public BigDecimal getTotalBudget() { return totalBudget; }
    public void setTotalBudget(BigDecimal totalBudget) { this.totalBudget = totalBudget; }
    public BigDecimal getSpent() { return spent; }
    public void setSpent(BigDecimal spent) { this.spent = spent; }
    public BigDecimal getRemaining() { return remaining; }
    public void setRemaining(BigDecimal remaining) { this.remaining = remaining; }
}