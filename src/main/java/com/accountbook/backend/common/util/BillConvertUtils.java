package com.accountbook.backend.common.util;

import com.accountbook.backend.common.exception.BusinessServiceException;
import com.accountbook.backend.storage.entity.Bill;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * Bill实体转换工具类：提供 Map ↔ Bill 的转换方法（时间字段用String存储）
 * 静态方法设计，无需实例化，供各服务类直接调用
 */
public class BillConvertUtils {
    // 数据库时间字段格式（根据实际数据库存储格式调整，如"yyyy-MM-dd HH:mm:ss"）
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    // 空值提示常量（统一空值异常信息）
    private static final String NULL_FIELD_MSG = "%s不能为空（数据库主键/必选字段）";

    /**
     * 单条 Map 转 Bill 实体
     * @param billMap 数据库查询返回的 Map（键为列名，值为字段值）
     * @return 转换后的 Bill 实体
     * @throws BusinessServiceException 转换失败时抛出业务异常
     */
    public static Bill mapToBill(Map<String, Object> billMap) {
        // 1. 校验输入Map
        if (billMap == null || billMap.isEmpty()) {
            throw new BusinessServiceException("map to bill failed：输入Map为空，无法转换");
        }

        Bill bill = new Bill();
        try {
            // 2. 字段赋值（按数据库列名映射，需与Bill实体字段对应）
            // 账单ID（必选字段，主键非空）
            Integer id = getIntegerValue(billMap, "id", "账单ID");
            if (id == null) {
                throw new BusinessServiceException(String.format(NULL_FIELD_MSG, "账单ID"));
            }
            bill.setId(id);

            // 账单时间（必选字段，String类型存储，校验格式）
            String billTime = getTimeStringValue(billMap, "bill_time", "账单时间");
            if (billTime == null) {
                throw new BusinessServiceException(String.format(NULL_FIELD_MSG, "账单时间"));
            }
            bill.setTime(billTime); // 实体字段为String类型，直接赋值

            // 收支类型（必选字段，-1=支出，1=收入）
            Integer type = getIntegerValue(billMap, "type", "收支类型");
            if (type == null) {
                throw new BusinessServiceException(String.format(NULL_FIELD_MSG, "收支类型"));
            }
            // 校验收支类型合法性
            if (type != -1 && type != 1) {
                throw new BusinessServiceException("收支类型值非法：" + type + "，仅支持-1（支出）或1（收入）");
            }
            bill.setType(type);

            // 大类ID（必选字段）
            Integer categoryId = getIntegerValue(billMap, "category_id", "大类ID");
            if (categoryId == null) {
                throw new BusinessServiceException(String.format(NULL_FIELD_MSG, "大类ID"));
            }
            bill.setCategoryId(categoryId);

            // 具体类型ID（必选字段）
            Integer specificTypeId = getIntegerValue(billMap, "specific_type_id", "具体类型ID");
            if (specificTypeId == null) {
                throw new BusinessServiceException(String.format(NULL_FIELD_MSG, "具体类型ID"));
            }
            bill.setSpecificTypeId(specificTypeId);

            // 金额（必选字段，DECIMAL类型，避免精度丢失）
            BigDecimal amount = getBigDecimalValue(billMap, "amount", "金额");
            if (amount == null) {
                throw new BusinessServiceException(String.format(NULL_FIELD_MSG, "金额"));
            }
            // 校验金额大于0
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessServiceException("金额非法：" + amount + "，需大于0");
            }
            bill.setAmount(amount);

            // 备注（可选字段，允许为null）
            bill.setRemark(getStringValue(billMap, "remark", "备注"));

            // 若Bill实体有其他字段（如create_time、update_time），补充以下格式的赋值
            // bill.setCreateTime(getTimeStringValue(billMap, "create_time", "创建时间"));

        } catch (BusinessServiceException e) {
            // 业务异常直接抛出
            throw e;
        } catch (Exception e) {
            // 其他异常包装为业务异常，明确错误原因并携带异常链
            throw new BusinessServiceException("map to bill failed：转换异常，原因：" + e.getMessage());
        }

        return bill;
    }

    /**
     * 批量 Map 转 Bill 实体列表
     * @param billMapList 数据库查询返回的 Map 列表
     * @return 转换后的 Bill 实体列表（空列表而非null，避免空指针）
     */
    public static List<Bill> mapListToBillList(List<Map<String, Object>> billMapList) {
        List<Bill> billList = new ArrayList<>();
        if (billMapList == null || billMapList.isEmpty()) {
            return billList;
        }

        for (Map<String, Object> map : billMapList) {
            try {
                billList.add(mapToBill(map));
            } catch (Exception e) {
                // 单条转换失败不影响整体，打印日志后跳过（补充Map索引，便于定位问题）
                int index = billMapList.indexOf(map);
                System.err.printf("map list to bill list failed：索引[%d]的单条数据转换失败，Map：%s，原因：%s%n",
                        index, map, e.getMessage());
            }
        }
        return billList;
    }

    // ========================== 私有辅助方法：处理字段类型转换 ==========================
    /**
     * 获取Integer类型字段值
     * @param map 数据Map
     * @param key 列名
     * @param fieldName 字段描述（用于异常提示）
     * @return Integer值（允许为null，若数据库字段可空）
     */
    private static Integer getIntegerValue(Map<String, Object> map, String key, String fieldName) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        // 兼容所有数字类型（Long、Short、Byte等）
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            String strValue = ((String) value).trim();
            if (strValue.isEmpty()) {
                return null; // 空字符串视为null
            }
            try {
                return Integer.parseInt(strValue);
            } catch (NumberFormatException e) {
                throw new BusinessServiceException(fieldName + "格式错误：" + value + "，需为整数");
            }
        }
        // 简化类型名称，提升异常可读性
        String actualType = value.getClass().getSimpleName();
        throw new BusinessServiceException(fieldName + "类型错误：" + actualType + "，不支持转换为Integer");
    }

    /**
     * 获取BigDecimal类型字段值（金额专用）
     * @param map 数据Map
     * @param key 列名
     * @param fieldName 字段描述（用于异常提示）
     * @return BigDecimal值（允许为null，若数据库字段可空）
     */
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
                return null; // 空字符串视为null
            }
            try {
                return new BigDecimal(strValue);
            } catch (NumberFormatException e) {
                throw new BusinessServiceException(fieldName + "格式错误：" + value + "，需为数字（支持整数/小数）");
            }
        }
        // 兼容所有数字类型，避免浮点精度丢失
        if (value instanceof Number) {
            return new BigDecimal(value.toString());
        }
        // 简化类型名称，提升异常可读性
        String actualType = value.getClass().getSimpleName();
        throw new BusinessServiceException(fieldName + "类型错误：" + actualType + "，不支持转换为BigDecimal");
    }

    /**
     * 获取String类型的时间字段值（时间专用，校验格式）
     * @param map 数据Map
     * @param key 列名
     * @param fieldName 字段描述（用于异常提示）
     * @return 格式化后的时间字符串（允许为null，若数据库字段可空）
     */
    private static String getTimeStringValue(Map<String, Object> map, String key, String fieldName) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        // 直接获取String类型时间
        String timeStr;
        if (value instanceof String) {
            timeStr = ((String) value).trim();
            if (timeStr.isEmpty()) {
                return null; // 空字符串视为null
            }
        } else if (value instanceof LocalDateTime) {
            // 兼容数据库返回LocalDateTime的场景，转为目标格式字符串
            timeStr = ((LocalDateTime) value).format(TIME_FORMATTER);
        } else {
            // 其他类型不支持
            String actualType = value.getClass().getSimpleName();
            throw new BusinessServiceException(fieldName + "类型错误：" + actualType + "，不支持转换为时间字符串");
        }

        // 校验时间格式是否符合要求
        try {
            LocalDateTime.parse(timeStr, TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            String example = TIME_FORMATTER.format(LocalDateTime.now());
            throw new BusinessServiceException(fieldName + "格式错误：" + timeStr + "，需符合格式：" + example);
        }

        return timeStr;
    }

    /**
     * 获取String类型字段值
     * @param map 数据Map
     * @param key 列名
     * @param fieldName 字段描述（用于异常提示）
     * @return String值（允许为null，若数据库字段可空）
     */
    private static String getStringValue(Map<String, Object> map, String key, String fieldName) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        // 处理空字符串场景
        String strValue = value.toString().trim();
        return strValue.isEmpty() ? null : strValue;
    }
}