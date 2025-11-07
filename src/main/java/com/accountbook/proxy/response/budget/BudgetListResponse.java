package com.accountbook.proxy.response.budget;

import com.accountbook.backend.storage.entity.Budget;
import java.util.ArrayList;
import java.util.List;

/**
 * 预算列表响应：适配列表查询场景（无分页信息，基于 BudgetSingleResponse 作为列表项）
 */
public class BudgetListResponse {
    // 列表数据：直接使用 BudgetSingleResponse 作为列表项，复用单条响应的字段定义
    private List<BudgetSingleResponse> items;

    /**
     * 静态转换方法：从 List<Budget> 转换为 BudgetListResponse（包含 BudgetSingleResponse 列表）
     * @param budgetList 数据库查询得到的 Budget 实体列表
     * @return 转换后的预算列表响应对象
     */
    public static BudgetListResponse fromBudgetList(List<Budget> budgetList) {
        BudgetListResponse response = new BudgetListResponse();
        List<BudgetSingleResponse> itemList = new ArrayList<>();
        
        if (budgetList != null) {
            for (Budget budget : budgetList) {
                // 复用 BudgetSingleResponse 已有的 fromBudget 方法，避免重复转换逻辑
                BudgetSingleResponse singleResponse = BudgetSingleResponse.fromBudget(budget);
                itemList.add(singleResponse);
            }
        }
        
        response.setItems(itemList);
        return response;
    }

    /**
     * 打印当前列表响应的所有信息（包含每条 BudgetSingleResponse 的详情）
     */
    public void printSelf() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== BudgetListResponse 信息 =====\n")
          .append("总条数: ").append(items == null ? 0 : items.size()).append("\n")
          .append("===== 预算列表详情 =====\n");
        
        if (items != null && !items.isEmpty()) {
            for (int i = 0; i < items.size(); i++) {
                sb.append("----- 第 ").append(i + 1).append(" 条 -----").append("\n")
                  .append(items.get(i).getFormattedString()); // 拼接单条预算的格式化字符串
            }
        } else {
            sb.append("无预算数据\n");
        }
        
        System.out.println(sb.toString());
    }

    // Getter + Setter
    public List<BudgetSingleResponse> getItems() {
        return items;
    }

    public void setItems(List<BudgetSingleResponse> items) {
        this.items = items;
    }
}