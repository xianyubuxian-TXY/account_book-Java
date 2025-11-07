package com.accountbook.backend.common.util;

import com.accountbook.backend.common.exception.BusinessServiceException;
import com.accountbook.backend.storage.entity.SpecificType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SpecificType实体转换工具类：提供 Map ↔ SpecificType 的转换
 */
public final class SpecificTypeConvertUtils {

    private SpecificTypeConvertUtils() {
        throw new AssertionError("工具类不允许实例化");
    }

    private static final String NULL_FIELD_MSG = "%s不能为空（数据库主键/必选字段）";

    /**
     * 单条 Map 转 SpecificType 实体
     */
    public static SpecificType mapToSpecificType(Map<String, Object> specificTypeMap) {
        if (specificTypeMap == null || specificTypeMap.isEmpty()) {
            throw new BusinessServiceException("map to specificType failed：输入Map为空，无法转换");
        }

        SpecificType specificType = new SpecificType();
        try {
            // 具体类型ID（必选，正整数）
            Integer id = getIntegerValue(specificTypeMap, "id", "具体类型ID");
            if (id == null) {
                throw new BusinessServiceException(String.format(NULL_FIELD_MSG, "具体类型ID"));
            }
            if (id <= 0) {
                throw new BusinessServiceException("具体类型ID非法：" + id + "，必须为正整数");
            }
            specificType.setId(id);

            // 具体类型名称（必选）
            String name = getStringValue(specificTypeMap, "name", "具体类型名称");
            if (name == null || name.trim().isEmpty()) {
                throw new BusinessServiceException(String.format(NULL_FIELD_MSG, "具体类型名称"));
            }
            specificType.setName(name.trim());

            // 关联的大类ID（必选，正整数）
            Integer categoryId = getIntegerValue(specificTypeMap, "category_id", "关联的大类ID");
            if (categoryId == null) {
                throw new BusinessServiceException(String.format(NULL_FIELD_MSG, "关联的大类ID"));
            }
            if (categoryId <= 0) {
                throw new BusinessServiceException("关联的大类ID非法：" + categoryId + "，必须为正整数");
            }
            specificType.setCategoryId(categoryId);

        } catch (BusinessServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessServiceException("map to specificType failed：转换异常，原因：" + e.getMessage());
        }

        return specificType;
    }

    /**
     * 批量 Map 转 SpecificType 实体列表
     */
    public static List<SpecificType> mapListToSpecificTypeList(List<Map<String, Object>> specificTypeMapList) {
        List<SpecificType> specificTypeList = new ArrayList<>();
        if (specificTypeMapList == null || specificTypeMapList.isEmpty()) {
            return specificTypeList;
        }

        for (Map<String, Object> map : specificTypeMapList) {
            try {
                specificTypeList.add(mapToSpecificType(map));
            } catch (Exception e) {
                int index = specificTypeMapList.indexOf(map);
                System.err.printf("map list to specificType list failed：索引[%d]转换失败，原因：%s%n",
                        index, e.getMessage());
            }
        }
        return specificTypeList;
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