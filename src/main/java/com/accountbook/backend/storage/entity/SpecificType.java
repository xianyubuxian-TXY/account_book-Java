package com.accountbook.backend.storage.entity;

public class SpecificType {
    private Integer id;             // 具体类型ID
    private String name;            // 具体类型名称（如“外卖”）
    private Integer categoryId;     // 关联的大类ID（外键）

    // 构造方法
    public SpecificType() {}
    public SpecificType(Integer id, String name, Integer categoryId) {
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
    }

    // getter和setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

}