package com.accountbook.backend.common.util;

import com.accountbook.backend.common.exception.BusinessServiceException;
import com.accountbook.backend.storage.entity.Budget;

import java.math.BigDecimal;
import java.time.YearMonth; // 改为YearMonth处理年月
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BudgetConvertUtils {
    // 月份格式器（保持"yyyy-MM"，但关联YearMonth）
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final String NULL_FIELD_MSG = "%s不能为空（预算核心字段）";

    /**
     * 单条Map转Budget实体
     */
    public static Budget mapToBudget(Map<String, Object> budgetMap) {
        if (budgetMap == null || budgetMap.isEmpty()) {
            throw new BusinessServiceException("map to budget failed：输入Map为空");
        }

        Budget budget = new Budget();
        try {
            // 映射主键ID
            Integer id = getIntegerValue(budgetMap, "id", "预算ID");
            budget.setId(id);

            // 映射分类ID（非空校验）
            Integer categoryId = getIntegerValue(budgetMap, "category_id", "分类ID");
            if (categoryId == null) {
                throw new BusinessServiceException(String.format(NULL_FIELD_MSG, "分类ID"));
            }
            budget.setCategoryId(categoryId);

            // 映射月份（格式校验，修复核心问题）
            String month = getMonthStringValue(budgetMap, "month", "预算月份");
            if (month == null) {
                throw new BusinessServiceException(String.format(NULL_FIELD_MSG, "预算月份"));
            }
            budget.setMonth(month);

            // 映射总预算（非空校验）
            BigDecimal totalBudget = getBigDecimalValue(budgetMap, "total_budget", "总预算金额");
            if (totalBudget == null) {
                throw new BusinessServiceException(String.format(NULL_FIELD_MSG, "总预算金额"));
            }
            budget.setTotalBudget(totalBudget);

            // 映射已支出（可选，默认0）
            BigDecimal spent = getBigDecimalValue(budgetMap, "spent", "已支出金额");
            budget.setSpent(spent == null ? BigDecimal.ZERO : spent);

            // 映射剩余预算（非空）
            BigDecimal remaining = getBigDecimalValue(budgetMap, "remaining", "剩余预算金额");
            budget.setRemaining(remaining);

        } catch (BusinessServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessServiceException("map to budget failed：" + e.getMessage());
        }

        return budget;
    }

    /**
     * 批量Map转Budget实体列表
     */
    public static List<Budget> mapListToBudgetList(List<Map<String, Object>> budgetMapList) {
        List<Budget> budgetList = new ArrayList<>();
        if (budgetMapList == null || budgetMapList.isEmpty()) {
            return budgetList;
        }

        for (Map<String, Object> map : budgetMapList) {
            try {
                budgetList.add(mapToBudget(map));
            } catch (Exception e) {
                int index = budgetMapList.indexOf(map);
                System.err.printf("map list to budget list failed：索引[%d]转换失败，原因：%s%n", index, e.getMessage());
            }
        }
        return budgetList;
    }

    // ==================== 私有辅助方法：类型转换与校验 ====================
    private static Integer getIntegerValue(Map<String, Object> map, String key, String fieldName) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            String strValue = ((String) value).trim();
            if (strValue.isEmpty()) {
                return null;
            }
            try {
                return Integer.parseInt(strValue);
            } catch (NumberFormatException e) {
                throw new BusinessServiceException(fieldName + "格式错误：" + value);
            }
        }
        String actualType = value.getClass().getSimpleName();
        throw new BusinessServiceException(fieldName + "类型错误：" + actualType);
    }

    private static BigDecimal getBigDecimalValue(Map<String, Object> map, String key, String fieldName) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof String) {
            String strValue = ((String) value).trim();
            if (strValue.isEmpty()) {
                return null;
            }
            try {
                return new BigDecimal(strValue);
            } catch (NumberFormatException e) {
                throw new BusinessServiceException(fieldName + "格式错误：" + value);
            }
        }
        if (value instanceof Number) {
            return new BigDecimal(value.toString());
        }
        String actualType = value.getClass().getSimpleName();
        throw new BusinessServiceException(fieldName + "类型错误：" + actualType);
    }

    /**
     * 修复核心：使用YearMonth解析"yyyy-MM"格式（无需日信息）
     */
    private static String getMonthStringValue(Map<String, Object> map, String key, String fieldName) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        String monthStr;
        if (value instanceof String) {
            monthStr = ((String) value).trim();
            if (monthStr.isEmpty()) {
                return null;
            }
        } else if (value instanceof YearMonth) {
            // 兼容数据库返回YearMonth的场景
            monthStr = ((YearMonth) value).format(MONTH_FORMATTER);
        } else {
            String actualType = value.getClass().getSimpleName();
            throw new BusinessServiceException(fieldName + "类型错误：" + actualType);
        }

        // 关键修复：用YearMonth.parse替代LocalDate.parse，支持"yyyy-MM"
        try {
            YearMonth.parse(monthStr, MONTH_FORMATTER);
        } catch (DateTimeParseException e) {
            String example = MONTH_FORMATTER.format(YearMonth.now()); // 示例：2025-10
            throw new BusinessServiceException(fieldName + "格式错误：" + monthStr + "，需符合" + example);
        }

        return monthStr;
    }
}