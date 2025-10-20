package com.accountbook.backend.storage.entity;

import java.math.BigDecimal;

// 预算实体
public class Budget {
    // 不可变字段（初始化后不修改）
    private final String month;
    private final String category;
    private final BigDecimal monthlyBudget;

    // 可变字段（可动态更新）
    private BigDecimal actualSpend;
    private BigDecimal remainingBudget;

    // 前端初始化：仅接收前端传递的字段
    public Budget(String month, String category, BigDecimal monthlyBudget) {
        this.month = month;
        this.category = category;
        this.monthlyBudget = monthlyBudget;
        this.actualSpend = BigDecimal.ZERO;
        this.remainingBudget = monthlyBudget;
    }

    // 后端初始化：接收数据库查询的完整字段
    public Budget(String month, String category, BigDecimal monthlyBudget,
                 BigDecimal actualSpend, BigDecimal remainingBudget) {
        this.month = month;
        this.category = category;
        this.monthlyBudget = monthlyBudget;
        this.actualSpend = actualSpend;
        this.remainingBudget = remainingBudget;
    }

    // 不可变字段的getter（无setter）
    public String getMonth() { return month; }
    public String getCategory() { return category; }
    public BigDecimal getMonthlyBudget() { return monthlyBudget; }

    // 可变字段的getter和setter
    public BigDecimal getActualSpend() { return actualSpend; }
    public void setActualSpend(BigDecimal actualSpend) { this.actualSpend = actualSpend; }

    public BigDecimal getRemainingBudget() { return remainingBudget; }
    public void setRemainingBudget(BigDecimal remainingBudget) { this.remainingBudget = remainingBudget; }
}