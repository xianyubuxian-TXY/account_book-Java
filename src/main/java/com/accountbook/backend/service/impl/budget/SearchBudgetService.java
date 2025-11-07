package com.accountbook.backend.service.impl.budget;

import com.accountbook.backend.common.util.BudgetConvertUtils;
import com.accountbook.backend.service.BusinessService;
import com.accountbook.backend.storage.dao.BudgetDAO;
import com.accountbook.backend.storage.dao.factory.DAOFactory;
import com.accountbook.backend.storage.entity.Budget;
import com.accountbook.proxy.request.budget.BudgetSearchParams;
import com.accountbook.proxy.response.budget.BudgetListResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchBudgetService implements BusinessService<BudgetSearchParams, BudgetListResponse> {
    private final BudgetDAO budgetDAO = DAOFactory.getBudgetDAO();

    @Override
    public BudgetListResponse execute(BudgetSearchParams params) throws Exception {
        System.out.println("执行预算查询业务");

        // 处理参数为空的情况（直接查询所有并按月份倒序）
        if (params == null || isAllParamsNull(params)) {
            System.out.println("无有效查询条件，默认返回所有预算（按月份由近到远）");
            List<Budget> allBudgets = budgetDAO.queryAllBudgetsOrderByMonthDesc();
            return BudgetListResponse.fromBudgetList(allBudgets);
        }

        // 1. 将查询参数转为 Map
        Map<String, Object> paramMap = params.toMap();
        if (paramMap.isEmpty()) {
            // 理论上不会走到这里，因为isAllParamsNull已过滤
            System.out.println("参数转换后为空，默认返回所有预算（按月份由近到远）");
            List<Budget> allBudgets = budgetDAO.queryAllBudgetsOrderByMonthDesc();
            return BudgetListResponse.fromBudgetList(allBudgets);
        }

        // 2. 动态拼接 SQL 条件和参数列表
        StringBuilder condition = new StringBuilder();
        List<Object> sqlParams = new ArrayList<>();

        // 遍历 Map 生成 AND 连接的条件
        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value == null) {
                continue; // 跳过 null 值条件
            }

            // 根据字段类型拼接条件（重点处理范围查询字段）
            switch (key) {
                case "id":
                    // 预算ID：精确匹配
                    condition.append("id = ? AND ");
                    sqlParams.add(value);
                    break;
                case "month":
                    // 单值月份：精确匹配
                    condition.append("month = ? AND ");
                    sqlParams.add(value);
                    break;
                case "month_start":
                    // 月份范围起始：>=
                    condition.append("month >= ? AND ");
                    sqlParams.add(value);
                    break;
                case "month_end":
                    // 月份范围结束：<=
                    condition.append("month <= ? AND ");
                    sqlParams.add(value);
                    break;
                case "category_id":
                    // 分类ID：精确匹配
                    condition.append("category_id = ? AND ");
                    sqlParams.add(value);
                    break;
                default:
                    // 忽略未知字段
                    break;
            }
        }

        // 3. 处理条件字符串（移除末尾多余的 "AND "）
        String finalCondition = "";
        if (condition.length() > 0) {
            finalCondition = condition.substring(0, condition.length() - 4);
        } else {
            // 所有参数都为null（但params不为null），默认返回所有预算
            System.out.println("所有查询条件均为null，默认返回所有预算（按月份由近到远）");
            List<Budget> allBudgets = budgetDAO.queryAllBudgetsOrderByMonthDesc();
            return BudgetListResponse.fromBudgetList(allBudgets);
        }

        // 4. 调用 DAO 查询完整字段（字段参数传 "*"）
        List<Map<String, Object>> mapList = budgetDAO.queryBudgets("*", finalCondition, sqlParams.toArray());

        // 5. 将 Map 列表转为 Budget 实体列表
        List<Budget> budgetList = BudgetConvertUtils.mapListToBudgetList(mapList);
        // 6. 将Budget实体列表转为 BudgetListResponse
        return BudgetListResponse.fromBudgetList(budgetList);
    }

    /**
     * 判断查询参数是否全为null（辅助方法）
     * @param params 查询参数对象
     * @return true=所有参数都为null；false=存在非null参数
     */
    private boolean isAllParamsNull(BudgetSearchParams params) {
        return params.getBudgetId() == null &&
               params.getMonth() == null &&
               params.getMonthStart() == null &&
               params.getMonthEnd() == null &&
               params.getCategoryId() == null;
    }
}