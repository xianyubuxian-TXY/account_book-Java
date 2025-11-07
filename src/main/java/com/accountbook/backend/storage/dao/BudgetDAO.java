package com.accountbook.backend.storage.dao;

import com.accountbook.backend.storage.entity.Budget;
import java.util.List;
import java.util.Map;

/**
 * 预算DAO接口：适配新表结构，新增按月份倒序查询方法
 */
public interface BudgetDAO {
    int addBudget(Map<String, Object> budgetMap);
    int updateBudget(Map<String, Object> fieldMap, String condition, Object... params);
    int deleteBudget(String condition, Object... params);
    List<Map<String, Object>> queryBudgets(String fields, String condition, Object... params);
    List<Map<String, Object>> queryBudgetsWithJoin(String customSql, Object... params);

    // 主键ID相关操作
    Budget queryBudgetById(Integer id);
    int deleteBudgetById(Integer id);
    int updateBudgetById(Integer id, Map<String, Object> fieldMap);

    // 业务特定查询
    List<Budget> queryAllBudgetsOrderByMonthDesc(); // 全查询按月份从大到小排序
    List<Budget> queryBudgetsByMonth(String month);
    List<Budget> queryBudgetsByCategoryId(Integer categoryId);
}