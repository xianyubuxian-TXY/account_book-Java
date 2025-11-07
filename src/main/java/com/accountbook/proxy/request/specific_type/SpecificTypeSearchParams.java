package com.accountbook.proxy.request.specific_type;

import java.util.HashMap;
import java.util.Map;

/**
 * 具体类型查询请求参数类（参考BillSearchParams、CategorySearchParams设计风格）
 * 支持按ID精确查询、按名称模糊查询、按关联大类ID查询，包含参数转换为Map的方法
 */
public class SpecificTypeSearchParams {
    // 1. 核心查询字段
    private Integer specificTypeId;  // 具体类型ID（用于精确查询单条具体类型）
    private String nameKey;          // 名称关键字（用于模糊查询，如"外"匹配"外卖"）
    private Integer categoryId;      // 关联的大类ID（用于查询某大类下的所有具体类型）

    // 默认构造函数：通过Setter动态设置查询条件（支持灵活组合）
    public SpecificTypeSearchParams() {}

    // 2. 按ID查询的构造方法（专门用于单ID精确查询）
    public SpecificTypeSearchParams(Integer specificTypeId) {
        this.specificTypeId = specificTypeId;
    }

    // 3. 按名称查询的构造方法（专门用于名称模糊查询）
    public SpecificTypeSearchParams(String nameKey) {
        this.nameKey = nameKey;
    }

    // 4. 按大类ID查询的构造方法（专门用于查询某大类下的具体类型）
    public SpecificTypeSearchParams(Integer categoryId, String nameKey) {
        this.categoryId = categoryId;
        this.nameKey = nameKey;
    }

    // 5. 全参构造方法（支持同时设置所有查询条件）
    public SpecificTypeSearchParams(Integer specificTypeId, String nameKey, Integer categoryId) {
        this.specificTypeId = specificTypeId;
        this.nameKey = nameKey;
        this.categoryId = categoryId;
    }

    // 6. Getter/Setter（明确字段用途）
    public Integer getSpecificTypeId() {
        return specificTypeId;
    }

    /**
     * 设置具体类型ID（用于精确查询，正整数）
     */
    public void setSpecificTypeId(Integer specificTypeId) {
        this.specificTypeId = specificTypeId;
    }

    public String getNameKey() {
        return nameKey;
    }

    /**
     * 设置名称关键字（用于模糊查询，如"交"可匹配"公交"、"交通"）
     */
    public void setNameKey(String nameKey) {
        this.nameKey = nameKey;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    /**
     * 设置关联的大类ID（用于查询某大类下的具体类型，正整数）
     */
    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * 转换为Map（键为数据库查询条件字段名，值为查询参数）
     * 仅包含非空字段，避免无效查询条件
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        // 精确查询：具体类型ID（键为"id"，对应数据库主键字段）
        if (this.specificTypeId != null) {
            map.put("id", this.specificTypeId);
        }
        // 模糊查询：名称关键字（键为"name_like"，后端可拼接%实现模糊匹配）
        if (this.nameKey != null && !this.nameKey.trim().isEmpty()) {
            map.put("name_like", this.nameKey.trim());
        }
        // 关联查询：大类ID（键为"category_id"，对应数据库外键字段）
        if (this.categoryId != null) {
            map.put("category_id", this.categoryId);
        }
        return map;
    }
}