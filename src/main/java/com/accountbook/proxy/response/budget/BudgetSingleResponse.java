package com.accountbook.proxy.response.budget;

import com.accountbook.backend.storage.entity.Budget;
import java.math.BigDecimal;

/**
 * 单条预算详情响应：适配新增/修改/单查场景
 */
public class BudgetSingleResponse {
    // 预算ID（前端后续删除/修改需用，核心标识）
    private Integer budgetId;
    // 预算月份（前端核心展示字段，格式：YYYY-MM）
    private String month;
    // 关联的大类ID（前端展示分类名称时需用）
    private Integer categoryId;
    // 总预算金额（前端核心配置与展示字段）
    private BigDecimal totalBudget;
    // 实际支出金额（后端自动计算，前端展示用）
    private BigDecimal spent;
    // 剩余预算金额（后端自动计算，前端核心展示字段）
    private BigDecimal remaining;

    // Getter + Setter（仅保留基础访问方法）
    public Integer getBudgetId() { return budgetId; }
    public void setBudgetId(Integer budgetId) { this.budgetId = budgetId; }
    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public BigDecimal getTotalBudget() { return totalBudget; }
    public void setTotalBudget(BigDecimal totalBudget) { this.totalBudget = totalBudget; }
    public BigDecimal getSpent() { return spent; }
    public void setSpent(BigDecimal spent) { this.spent = spent; }
    public BigDecimal getRemaining() { return remaining; }
    public void setRemaining(BigDecimal remaining) { this.remaining = remaining; }

    /**
     * 静态转换方法：从 Budget 实体构建 BudgetSingleResponse
     * @param budget 数据库查询得到的 Budget 实体
     * @return 转换后的预算详情响应对象
     * @throws IllegalArgumentException 当输入 Budget 为 null 时抛出
     */
    public static BudgetSingleResponse fromBudget(Budget budget) {
        if (budget == null) {
            throw new IllegalArgumentException("Budget 实体不能为空，无法转换为 BudgetSingleResponse");
        }

        BudgetSingleResponse response = new BudgetSingleResponse();
        // 字段一一映射（与Budget实体字段对应，包含主键ID）
        response.setBudgetId(budget.getId());
        response.setMonth(budget.getMonth());
        response.setCategoryId(budget.getCategoryId());
        response.setTotalBudget(budget.getTotalBudget());
        response.setSpent(budget.getSpent());
        response.setRemaining(budget.getRemaining());

        return response;
    }

    /**
     * 格式化字符串（供列表拼接使用）
     */
    public String getFormattedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("├─ 预算ID：").append(budgetId).append("\n")
          .append("├─ 月份：").append(month).append("\n")
          .append("├─ 分类ID：").append(categoryId).append("\n")
          .append("├─ 总预算：").append(totalBudget).append("\n")
          .append("├─ 实际支出：").append(spent).append("\n")
          .append("└─ 剩余预算：").append(remaining).append("\n");
        return sb.toString();
    }

    /**
     * 打印单条预算详情
     */
    public void printSelf() {
        StringBuilder sb = new StringBuilder();
        sb.append("BudgetSingleResponse 详情：\n")
          .append(getFormattedString());
        System.out.println(sb.toString());
    }
}