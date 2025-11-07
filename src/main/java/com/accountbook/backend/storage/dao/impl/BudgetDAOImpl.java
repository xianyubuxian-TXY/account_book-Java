package com.accountbook.backend.storage.dao.impl;

import com.accountbook.backend.common.exception.BusinessServiceException;
import com.accountbook.backend.common.util.BudgetConvertUtils;
import com.accountbook.backend.storage.dao.BaseDAO;
import com.accountbook.backend.storage.dao.BudgetDAO;
import com.accountbook.backend.storage.entity.Budget;

import java.util.List;
import java.util.Map;

/**
 * 预算DAO实现类：适配新表结构，SQL语句与字段名对齐
 */
public class BudgetDAOImpl extends BaseDAO implements BudgetDAO {
    private static final String TABLE_BUDGET = "budget";

    @Override
    public int addBudget(Map<String, Object> budgetMap) {
        try {
            return super.insert(TABLE_BUDGET, budgetMap);
        } catch (Exception e) {
            System.err.println("新增预算失败：" + e.getMessage());
            return -1;
        }
    }

    @Override
    public int updateBudget(Map<String, Object> fieldMap, String condition, Object... params) {
        try {
            return super.update(TABLE_BUDGET, fieldMap, condition, params);
        } catch (Exception e) {
            System.err.println("更新预算失败：" + e.getMessage());
            return -1;
        }
    }

    @Override
    public int deleteBudget(String condition, Object... params) {
        try {
            return super.delete(TABLE_BUDGET, condition, params);
        } catch (Exception e) {
            System.err.println("删除预算失败：" + e.getMessage());
            return -1;
        }
    }

    @Override
    public List<Map<String, Object>> queryBudgets(String fields, String condition, Object... params) {
        try {
            return super.query(TABLE_BUDGET, fields, condition, params);
        } catch (Exception e) {
            System.err.println("查询预算失败：" + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Map<String, Object>> queryBudgetsWithJoin(String customSql, Object... params) {
        try {
            return super.queryByCustomSql(customSql, params);
        } catch (Exception e) {
            System.err.println("关联查询预算失败：" + e.getMessage());
            return null;
        }
    }

    @Override
    public Budget queryBudgetById(Integer id) {
        if (id == null || id <= 0) {
            throw new BusinessServiceException("预算ID必须为正整数");
        }

        try {
            List<Map<String, Object>> mapList = super.query(TABLE_BUDGET, "*", "id = ?", id);
            if (mapList == null || mapList.isEmpty()) {
                throw new BusinessServiceException("未找到ID为" + id + "的预算");
            }
            return BudgetConvertUtils.mapToBudget(mapList.get(0));
        } catch (BusinessServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessServiceException("查询预算ID[" + id + "]失败：" + e.getMessage());
        }
    }

    @Override
    public int deleteBudgetById(Integer id) {
        if (id == null || id <= 0) {
            throw new BusinessServiceException("预算ID必须为正整数");
        }

        try {
            return super.delete(TABLE_BUDGET, "id = ?", id);
        } catch (Exception e) {
            System.err.println("删除预算ID[" + id + "]失败：" + e.getMessage());
            return -1;
        }
    }

    @Override
    public int updateBudgetById(Integer id, Map<String, Object> fieldMap) {
        if (id == null || id <= 0) {
            throw new BusinessServiceException("预算ID必须为正整数");
        }
        if (fieldMap == null || fieldMap.isEmpty()) {
            throw new BusinessServiceException("至少需要一个更新字段");
        }

        try {
            return super.update(TABLE_BUDGET, fieldMap, "id = ?", id);
        } catch (Exception e) {
            System.err.println("更新预算ID[" + id + "]失败：" + e.getMessage());
            return -1;
        }
    }

    @Override
    public List<Budget> queryAllBudgetsOrderByMonthDesc() {
        try {
            // 全查询并按月份从大到小排序（DESC）
            String sql = "SELECT * FROM " + TABLE_BUDGET + " ORDER BY month DESC";
            List<Map<String, Object>> mapList = super.queryByCustomSql(sql);
            return BudgetConvertUtils.mapListToBudgetList(mapList);
        } catch (Exception e) {
            throw new BusinessServiceException("查询所有预算（按月份倒序）失败：" + e.getMessage());
        }
    }

    @Override
    public List<Budget> queryBudgetsByMonth(String month) {
        if (month == null || month.trim().isEmpty()) {
            throw new BusinessServiceException("预算月份不能为空");
        }

        try {
            List<Map<String, Object>> mapList = super.query(TABLE_BUDGET, "*", "month = ?", month);
            return BudgetConvertUtils.mapListToBudgetList(mapList);
        } catch (Exception e) {
            throw new BusinessServiceException("查询月份[" + month + "]的预算失败：" + e.getMessage());
        }
    }

    @Override
    public List<Budget> queryBudgetsByCategoryId(Integer categoryId) {
        if (categoryId == null || categoryId <= 0) {
            throw new BusinessServiceException("分类ID必须为正整数");
        }

        try {
            List<Map<String, Object>> mapList = super.query(TABLE_BUDGET, "*", "category_id = ?", categoryId);
            return BudgetConvertUtils.mapListToBudgetList(mapList);
        } catch (Exception e) {
            throw new BusinessServiceException("查询分类ID[" + categoryId + "]的预算失败：" + e.getMessage());
        }
    }
}