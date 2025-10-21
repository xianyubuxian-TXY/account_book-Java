package com.accountbook.backend.storage.dao.impl;

import java.util.List;
import java.util.Map;

import com.accountbook.backend.storage.dao.BaseDAO;
import com.accountbook.backend.storage.dao.BudgetDAO;

/**
 * 预算数据访问类（仅处理budget表的数据操作，无业务逻辑）
 */
public class BudgetDAOImpl extends BaseDAO implements BudgetDAO{

    // 表名常量（与数据库表名一致）
    private static final String TABLE_BUDGET = "budget";

    /**
     * 新增预算
     * @param budgetMap 预算字段映射（key:数据库字段名，如"category_id"；value:对应值）
     * @return 影响行数
     */
    public int addBudget(Map<String, Object> budgetMap) {
        try
        {
            return super.insert(TABLE_BUDGET, budgetMap);
        }catch(Exception e)
        {
            System.err.println(e);
            return -1;
        }
    }

    /**
     * 根据条件更新预算
     * @param fieldMap 要更新的字段-值映射
     * @param condition 条件（如"id=?"）
     * @param params 条件参数
     * @return 影响行数
     */
    public int updateBudget(Map<String, Object> fieldMap, String condition, Object... params) {
        try
        {
            return super.update(TABLE_BUDGET, fieldMap, condition, params);
        }catch(Exception e)
        {
            System.err.println(e);
            return -1;
        }
    }

    /**
     * 根据条件删除预算
     * @param condition 条件（如"category_id=? AND month=?"）
     * @param params 条件参数
     * @return 影响行数
     */
    public int deleteBudget(String condition, Object... params) {
        try
        {
            return super.delete(TABLE_BUDGET, condition, params);
        }catch(Exception e)
        {
            System.err.println(e);
            return -1;
        }
    }

    /**
     * 单表查询预算（支持条件过滤）
     * @param fields 要查询的字段（如"total_budget,spent"）
     * @param condition 条件（如"month=?"）
     * @param params 条件参数
     * @return 结果列表（Map<字段名, 值>）
     */
    public List<Map<String, Object>> queryBudgets(String fields, String condition, Object... params) {
        try
        {
            return super.query(TABLE_BUDGET, fields, condition, params);
        }catch(Exception e)
        {
            System.err.println(e);
            return null;
        }
    }

    /**
     * 关联查询预算（如关联category表获取大类名称）
     * @param customSql 自定义关联查询SQL
     * @param params SQL参数
     * @return 结果列表（Map<字段名, 值>）
     */
    public List<Map<String, Object>> queryBudgetsWithJoin(String customSql, Object... params) {
        try
        {
            return super.queryByCustomSql(customSql, params);
        }catch(Exception e)
        {
            System.err.println(e);
            return null;
        }
    }
}