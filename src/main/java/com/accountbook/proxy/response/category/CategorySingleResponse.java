package com.accountbook.proxy.response.category;

import com.accountbook.backend.storage.entity.Category;

/**
 * 单条分类详情响应：适配新增/修改/单查场景
 */
public class CategorySingleResponse {
    // 分类ID（前端后续删除/修改需用，核心标识）
    private Integer categoryId;
    // 分类名称（前端核心展示数据）
    private String name;

    // Getter + Setter（与 BillSingleResponse 保持一致，提供完整访问方法）
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

    /**
     * 静态转换方法：从 Category 实体构建 CategoryAddResponse
     * @param category 数据库查询得到的 Category 实体
     * @return 转换后的分类详情响应对象
     * @throws IllegalArgumentException 当输入 Category 为 null 时抛出
     */
    public static CategorySingleResponse fromCategory(Category category) {
        // 校验输入实体非空（与 BillSingleResponse.fromBill 逻辑一致）
        if (category == null) {
            throw new IllegalArgumentException("Category 实体不能为空，无法转换为 CategoryAddResponse");
        }

        CategorySingleResponse response = new CategorySingleResponse();
        // 字段一一映射（确保与 Category 实体的 Getter 方法对应）
        response.setCategoryId(category.getId());
        response.setName(category.getName());

        return response;
    }

    /**
     * 格式化字符串：供列表拼接或日志打印使用
     * @return 单条分类的格式化字符串
     */
    public String getFormattedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("├─ 分类ID：").append(categoryId).append("\n")
          .append("└─ 分类名称：").append(name == null ? "无" : name).append("\n");
        return sb.toString();
    }

    /**
     * 打印单条分类详情（与 BillSingleResponse.printSelf 功能一致）
     */
    public void printSelf() {
        StringBuilder sb = new StringBuilder();
        sb.append("CategoryAddResponse 详情：\n")
          .append(getFormattedString()); // 复用格式化逻辑
        System.out.println(sb.toString());
    }
}