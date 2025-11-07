package com.accountbook.proxy.request.category;

import java.util.HashMap;
import java.util.Map;

/**
 * 修改分类请求参数类（参考BillChangeParams设计风格）
 * 支持部分字段修改，包含参数转换为Map的方法
 */
public class CategoryChangeParams {
    // 必填字段：待修改的分类ID
    private Integer categoryId;
    // 可选字段：新的分类名称（如"餐饮"→"美食"）
    private String name;

    /**
     * 构造方法：必填分类ID，其他字段通过Setter动态设置（支持部分修改）
     * @param categoryId 待修改的分类ID（必填，正整数）
     */
    public CategoryChangeParams(Integer categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * 全参构造方法（可选，适用于需要一次性设置所有字段的场景）
     */
    public CategoryChangeParams(Integer categoryId, String name) {
        this.categoryId = categoryId;
        this.name = name;
    }

    /**
     * 将参数转换为 Map（键为数据库字段名，值为字段值）
     * 用于 SQL 更新操作的参数绑定，仅包含非空字段（避免覆盖原有值）
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        if (this.name != null) {
            map.put("name", this.name.trim()); // 去除首尾空格，避免无效空格更新
        }
        return map;
    }

    /**
     * 获取分类ID的Map（用于SQL的WHERE条件绑定）
     */
    public Map<String, Object> getCategoryIdMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", this.categoryId);
        return map;
    }

    // 紧凑格式的 Getter/Setter
    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}