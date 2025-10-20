package com.accountbook.backend.storage.dao;

import java.util.List;
import java.util.Map;

// 账单 DAO 接口
public interface BudgetDAO {
    int addBudget(Map<String, Object> billMap);
    int updateBudget(Map<String, Object> fieldMap, String condition, Object... params);
    int deleteBudget(String condition, Object... params);
    List<Map<String, Object>> queryBudgets(String fields, String condition, Object... params) ;
    List<Map<String, Object>> queryBudgetsWithJoin(String customSql, Object... params);
}