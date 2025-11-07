package com.accountbook.helper;

import com.accountbook.proxy.common.ProxyHandler;
import com.accountbook.proxy.helper.impl.BudgetRequestHelper;
import com.accountbook.proxy.response.budget.BudgetDeleteResponse;
import com.accountbook.proxy.response.budget.BudgetListResponse;
import com.accountbook.proxy.response.budget.BudgetSingleResponse;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class BudgetRequestHelperTest {

    // 获取当前月份（格式：YYYY-MM）
    private static String getCurrentMonth() {
        return YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }

    // 获取下个月（用于范围查询测试）
    private static String getNextMonth() {
        return YearMonth.now().plusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }

    public static void main(String[] args) {
        // 初始化代理和预算助手
        ProxyHandler.init();
        BudgetRequestHelper budgetHelper = ProxyHandler.getBudgetHelper();
        System.out.println("=== 开始测试BudgetRequestHelper ===");

        // 测试用参数（请根据实际环境修改有效的分类ID）
        int testCategoryId = 1; // 替换为实际存在的分类ID
        String testMonth = getCurrentMonth(); // 测试月份：当前月
        String testNextMonth = getNextMonth(); // 测试月份：下个月
        Integer testBudgetId = null; // 存储新增的预算ID，供后续测试

        try {
            // 1. 新增预算测试
            System.out.println("\n=== 测试1：新增预算 ===");
            BudgetSingleResponse newBudget = budgetHelper.addBudget(
                    testMonth,             // 月份（YYYY-MM）
                    testCategoryId,        // 分类ID
                    new BigDecimal("3000") // 总预算（3000元）
            );
            testBudgetId = newBudget.getBudgetId();
            System.out.println("新增预算成功！ID：" + testBudgetId);
            System.out.println("预算详情：月份=" + newBudget.getMonth() + " | 分类ID=" + newBudget.getCategoryId() 
                    + " | 总预算=" + newBudget.getTotalBudget() + "元");

            // 1.1 测试重复添加（同一分类同一月份，预期抛出异常）
            System.out.println("\n=== 测试1.1：重复添加预算（预期失败） ===");
            try {
                budgetHelper.addBudget(testMonth, testCategoryId, new BigDecimal("4000"));
                System.out.println("错误：未拦截重复添加的预算！");
            } catch (RuntimeException e) {
                System.out.println("验证成功：" + e.getMessage()); // 预期输出“已存在预算”
            }

            // 2. 按ID查询预算
            System.out.println("\n=== 测试2：按ID查询预算 ===");
            BudgetSingleResponse queriedBudget = budgetHelper.searchBudgetById(testBudgetId);
            System.out.println("查询到ID=" + testBudgetId + "的预算：" 
                    + queriedBudget.getMonth() + " | " + queriedBudget.getTotalBudget() + "元");

            // 3. 按月份+分类查询预算
            System.out.println("\n=== 测试3：按月份+分类查询预算 ===");
            BudgetListResponse byMonthAndCategory = budgetHelper.searchBudget(testMonth, testCategoryId);
            System.out.println("月份=" + testMonth + "且分类ID=" + testCategoryId + "的预算共" 
                    + byMonthAndCategory.getItems().size() + "条");
            byMonthAndCategory.getItems().forEach(budget -> 
                System.out.println("- ID=" + budget.getBudgetId() + " | 预算=" + budget.getTotalBudget() + "元")
            );

            // 4. 更新预算测试（修改总预算金额）
            System.out.println("\n=== 测试4：更新预算 ===");
            BudgetSingleResponse updatedBudget = budgetHelper.updateBudget(
                    testBudgetId,          // 预算ID
                    null,                  // 不修改月份
                    null,                  // 不修改分类
                    new BigDecimal("3500") // 新预算金额（3500元）
            );
            System.out.println("更新后预算：ID=" + updatedBudget.getBudgetId() 
                    + " | 新总预算=" + updatedBudget.getTotalBudget() + "元");

            // 5. 范围查询测试（查询当前月到下个月的预算）
            System.out.println("\n=== 测试5：按月份范围查询预算 ===");
            // 先新增一个下个月的预算，用于范围查询
            BudgetSingleResponse nextMonthBudget = budgetHelper.addBudget(
                    testNextMonth, testCategoryId, new BigDecimal("2500")
            );
            System.out.println("已新增下个月预算：ID=" + nextMonthBudget.getBudgetId() 
                    + " | 月份=" + testNextMonth);

            // 执行范围查询
            BudgetListResponse rangeResult = budgetHelper.searchBudgetByMonthRange(
                    testMonth, testNextMonth, testCategoryId
            );
            System.out.println("月份范围[" + testMonth + "至" + testNextMonth + "]且分类ID=" + testCategoryId 
                    + "的预算共" + rangeResult.getItems().size() + "条");
            rangeResult.getItems().forEach(budget -> 
                System.out.println("- 月份=" + budget.getMonth() + " | 预算=" + budget.getTotalBudget() + "元")
            );

            // 6. 查询所有预算
            System.out.println("\n=== 测试6：查询所有预算 ===");
            BudgetListResponse allBudgets = budgetHelper.searchAllBudgets();
            System.out.println("系统中所有预算共" + allBudgets.getItems().size() + "条");

            // 7. 删除预算测试
            System.out.println("\n=== 测试7：删除预算 ===");
            BudgetDeleteResponse deleteResponse = budgetHelper.deleteBudget(testBudgetId);
            System.out.println("删除结果：" + deleteResponse);
            System.out.println("删除的预算ID：" + deleteResponse.getBudgetId());

            // 8. 验证删除（预期抛出异常）
            System.out.println("\n=== 测试8：验证删除结果 ===");
            try {
                budgetHelper.searchBudgetById(testBudgetId);
                System.out.println("错误：删除后仍能查询到预算！");
            } catch (RuntimeException e) {
                System.out.println("验证成功：" + e.getMessage()); // 预期输出“未找到预算”
            }

        } catch (Exception e) {
            System.err.println("\n测试失败：" + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=== 所有测试结束 ===");
    }
}