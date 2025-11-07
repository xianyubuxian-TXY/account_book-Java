package com.accountbook.proxy.request.category;

import java.util.HashMap;
import java.util.Map;

/**
 * 分类查询请求参数类（参考BillSearchParams设计风格）
 * 支持按ID精确查询、按名称模糊查询，包含参数转换为Map的方法
 */
public class CategorySearchParams {
    // 1. 核心查询字段
    private Integer categoryId;  // 分类ID（用于精确查询单条分类）
    private String nameKey;      // 名称关键字（用于模糊查询，如"餐"匹配"餐饮"）

    // 默认构造函数：通过Setter动态设置查询条件（支持灵活组合）
    public CategorySearchParams() {}

    // 2. 按ID查询的构造方法（专门用于单ID精确查询）
    public CategorySearchParams(Integer categoryId) {
        this.categoryId = categoryId;
    }

    // 3. 按名称查询的构造方法（专门用于名称模糊查询）
    public CategorySearchParams(String nameKey) {
        this.nameKey = nameKey;
    }

    // 4. 全参构造方法（支持同时设置ID和名称关键字）
    public CategorySearchParams(Integer categoryId, String nameKey) {
        this.categoryId = categoryId;
        this.nameKey = nameKey;
    }

    // 5. Getter/Setter（明确字段用途）
    public Integer getCategoryId() {
        return categoryId;
    }

    /**
     * 设置分类ID（用于精确查询，正整数）
     */
    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getNameKey() {
        return nameKey;
    }

    /**
     * 设置名称关键字（用于模糊查询，如"食"可匹配"饮食"、"食品"）
     */
    public void setNameKey(String nameKey) {
        this.nameKey = nameKey;
    }

    /**
     * 转换为Map（键为数据库查询条件字段名，值为查询参数）
     * 仅包含非空字段，避免无效查询条件
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        // 精确查询：分类ID（键为"id"，对应数据库主键字段）
        if (this.categoryId != null) {
            map.put("id", this.categoryId);
        }
        // 模糊查询：名称关键字（键为"name_like"，后端可拼接%实现模糊匹配）
        if (this.nameKey != null && !this.nameKey.trim().isEmpty()) {
            map.put("name_like", this.nameKey.trim());
        }
        return map;
    }
}