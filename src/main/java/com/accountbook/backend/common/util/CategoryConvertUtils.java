package com.accountbook.backend.common.util;

import com.accountbook.backend.common.exception.BusinessServiceException;
import com.accountbook.backend.storage.entity.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Category实体转换工具类：提供 Map ↔ Category 的转换方法
 */
public final class CategoryConvertUtils {

    private CategoryConvertUtils() {
        throw new AssertionError("工具类不允许实例化");
    }

    private static final String NULL_FIELD_MSG = "%s不能为空（数据库主键/必选字段）";

    /**
     * 单条 Map 转 Category 实体
     */
    public static Category mapToCategory(Map<String, Object> categoryMap) {
        if (categoryMap == null || categoryMap.isEmpty()) {
            throw new BusinessServiceException("map to category failed：输入Map为空，无法转换");
        }

        Category category = new Category();
        try {
            // 分类ID（必选，正整数）
            Integer id = getIntegerValue(categoryMap, "id", "分类ID");
            if (id == null) {
                throw new BusinessServiceException(String.format(NULL_FIELD_MSG, "分类ID"));
            }
            if (id <= 0) {
                throw new BusinessServiceException("分类ID非法：" + id + "，必须为正整数");
            }
            category.setId(id);

            // 分类名称（必选）
            String name = getStringValue(categoryMap, "name", "分类名称");
            if (name == null || name.trim().isEmpty()) {
                throw new BusinessServiceException(String.format(NULL_FIELD_MSG, "分类名称"));
            }
            category.setName(name.trim());

        } catch (BusinessServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessServiceException("map to category failed：转换异常，原因：" + e.getMessage());
        }

        return category;
    }

    /**
     * 批量 Map 转 Category 实体列表
     */
    public static List<Category> mapListToCategoryList(List<Map<String, Object>> categoryMapList) {
        List<Category> categoryList = new ArrayList<>();
        if (categoryMapList == null || categoryMapList.isEmpty()) {
            return categoryList;
        }

        for (Map<String, Object> map : categoryMapList) {
            try {
                categoryList.add(mapToCategory(map));
            } catch (Exception e) {
                int index = categoryMapList.indexOf(map);
                System.err.printf("map list to category list failed：索引[%d]转换失败，原因：%s%n",
                        index, e.getMessage());
            }
        }
        return categoryList;
    }

    // 辅助方法：获取Integer类型值
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
                throw new BusinessServiceException(fieldName + "格式错误：" + value + "，需为整数");
            }
        }
        String actualType = value.getClass().getSimpleName();
        throw new BusinessServiceException(fieldName + "类型错误：" + actualType + "，不支持转换为Integer");
    }

    // 辅助方法：获取String类型值
    private static String getStringValue(Map<String, Object> map, String key, String fieldName) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        String strValue = value.toString().trim();
        return strValue.isEmpty() ? null : strValue;
    }
}