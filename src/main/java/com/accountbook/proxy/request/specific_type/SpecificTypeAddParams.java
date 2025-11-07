package com.accountbook.proxy.request.specific_type;

import java.util.HashMap;
import java.util.Map;

public class SpecificTypeAddParams {
    private String name;            // 具体类型名称（如“外卖”）
    private Integer categoryId;     // 关联的大类ID（外键）

    // 构造方法
    public SpecificTypeAddParams() {}
    public SpecificTypeAddParams(String name, Integer categoryId) {
        this.name = name;
        this.categoryId = categoryId;
    }

    // getter和setter
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    /**
     * 将参数转换为 Map（键为字段名，值为字段值）
     * 用于 SQL 参数绑定、JSON 序列化等场景
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("category_id", categoryId);
        return map;
    }
}
