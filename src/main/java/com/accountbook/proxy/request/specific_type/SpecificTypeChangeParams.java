package com.accountbook.proxy.request.specific_type;

import java.util.HashMap;
import java.util.Map;

/**
 * 修改具体类型请求参数类（参考BillChangeParams、CategoryChangeParams设计风格）
 * 支持部分字段修改，包含参数转换为Map的方法
 */
public class SpecificTypeChangeParams {
    // 必填字段：待修改的具体类型ID
    private Integer specificTypeId;
    // 可选字段：新的具体类型名称（如"外卖"→"餐饮外卖"）
    private String name;
    // 可选字段：新的关联大类ID（支持修改所属大类）
    private Integer categoryId;

    /**
     * 构造方法：必填具体类型ID，其他字段通过Setter动态设置（支持部分修改）
     * @param specificTypeId 待修改的具体类型ID（必填，正整数）
     */
    public SpecificTypeChangeParams(Integer specificTypeId) {
        this.specificTypeId = specificTypeId;
    }

    /**
     * 全参构造方法（可选，适用于需要一次性设置所有字段的场景）
     */
    public SpecificTypeChangeParams(Integer specificTypeId, String name, Integer categoryId) {
        this.specificTypeId = specificTypeId;
        this.name = name;
        this.categoryId = categoryId;
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
        if (this.categoryId != null) {
            map.put("category_id", this.categoryId); // 对应数据库字段名
        }
        return map;
    }

    /**
     * 获取具体类型ID的Map（用于SQL的WHERE条件绑定）
     */
    public Map<String, Object> getSpecificTypeIdMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", this.specificTypeId);
        return map;
    }

    // 紧凑格式的 Getter/Setter
    public Integer getSpecificTypeId() {
        return specificTypeId;
    }

    public void setSpecificTypeId(Integer specificTypeId) {
        this.specificTypeId = specificTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }
}