package com.accountbook.backend.storage.entity;

public class Category {
    private Integer id;         // 大类ID
    private String name;        // 大类名称（如“饮食”）

    // 构造方法
    public Category() {}
    public Category(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    // getter和setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return "Category{id=" + id + ", name='" + name + "'}";
    }
}
