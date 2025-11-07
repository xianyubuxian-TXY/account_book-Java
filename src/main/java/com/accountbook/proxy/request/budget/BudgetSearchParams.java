package com.accountbook.proxy.request.budget;

import java.util.HashMap;
import java.util.Map;

/**
 * 查询预算请求参数类（参考BillSearchParams设计风格）
 * 支持按ID、单值条件、组合条件及范围查询
 */
public class BudgetSearchParams {
    // 1. 单值查询字段
    private Integer budgetId;        // 预算ID（主键，精确查询单条记录）
    private String month;            // 单值月份（YYYY-MM，精确查询）
    private Integer categoryId;      // 分类ID（精确查询）

    // 2. 范围查询字段（月份范围）
    private String monthStart;       // 月份范围起始（YYYY-MM，如"2025-01"）
    private String monthEnd;         // 月份范围结束（YYYY-MM，如"2025-12"）

    // 默认构造函数：通过Setter动态设置查询条件
    public BudgetSearchParams() {}

    // 3. 按ID查询的构造方法（专门用于单ID精确查询）
    public BudgetSearchParams(Integer budgetId) {
        this.budgetId = budgetId;
    }

    // 4. 单值查询构造方法（月份/分类ID）
    public BudgetSearchParams(String month) {
        this.month = month;
    }

    // 5. 组合条件查询构造方法（月份+分类ID）
    public BudgetSearchParams(String month, Integer categoryId) {
        this.month = month;
        this.categoryId = categoryId;
    }

    // 6. 范围查询构造方法（月份范围+其他条件）
    /**
     * 范围查询构造方法
     * @param monthStart 月份起始（YYYY-MM，可为null）
     * @param monthEnd 月份结束（YYYY-MM，可为null）
     * @param categoryId 分类ID（可为null）
     */
    public BudgetSearchParams(String monthStart, String monthEnd, Integer categoryId) {
        this.monthStart = monthStart;
        this.monthEnd = monthEnd;
        this.categoryId = categoryId;
    }

    // 7. Getter/Setter（明确字段格式约束）
    public Integer getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(Integer budgetId) {
        this.budgetId = budgetId;
    }

    public String getMonth() {
        return month;
    }

    /**
     * 设置单值月份（仅支持"YYYY-MM"格式，如"2025-10"）
     */
    public void setMonth(String month) {
        this.month = month;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getMonthStart() {
        return monthStart;
    }

    /**
     * 设置月份范围起始（仅支持"YYYY-MM"格式，如"2025-01"）
     */
    public void setMonthStart(String monthStart) {
        this.monthStart = monthStart;
    }

    public String getMonthEnd() {
        return monthEnd;
    }

    /**
     * 设置月份范围结束（仅支持"YYYY-MM"格式，如"2025-12"）
     */
    public void setMonthEnd(String monthEnd) {
        this.monthEnd = monthEnd;
    }

    /**
     * 转换为Map（仅包含非空字段，用于SQL查询条件绑定）
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        // 优先处理ID查询（精确匹配单条记录）
        if (this.budgetId != null) {
            map.put("id", this.budgetId);
        }
        // 单值月份查询
        if (this.month != null && !this.month.trim().isEmpty()) {
            map.put("month", this.month);
        }
        // 月份范围查询
        if (this.monthStart != null && !this.monthStart.trim().isEmpty()) {
            map.put("month_start", this.monthStart);
        }
        if (this.monthEnd != null && !this.monthEnd.trim().isEmpty()) {
            map.put("month_end", this.monthEnd);
        }
        // 分类ID查询（单值，支持与其他条件组合）
        if (this.categoryId != null) {
            map.put("category_id", this.categoryId);
        }
        return map;
    }
}