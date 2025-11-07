package com.accountbook.proxy.response.specific_type;

import com.accountbook.backend.storage.entity.SpecificType;

/**
 * 单条具体类型详情响应：适配新增/修改/单查场景
 */
public class SpecificTypeSingleResponse {
    // 具体类型ID（前端后续删除/修改需用，核心标识）
    private Integer specificTypeId;
    // 具体类型名称（前端核心展示数据）
    private String name;
    // 关联的大类ID（前端用于大类-具体类型联动展示）
    private Integer categoryId;

    // 无参构造（供JSON反序列化等场景使用）
    public SpecificTypeSingleResponse() {}

    // 全参构造（明确初始化所有字段）
    public SpecificTypeSingleResponse(Integer specificTypeId, String name, Integer categoryId) {
        this.specificTypeId = specificTypeId;
        this.name = name;
        this.categoryId = categoryId;
    }

    // Getter + Setter（与其他响应类保持一致，提供完整访问能力）
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

    /**
     * 静态转换方法：从 SpecificType 实体构建响应对象
     * @param specificType 数据库查询得到的具体类型实体
     * @return 转换后的具体类型详情响应
     * @throws IllegalArgumentException 当输入实体为 null 时抛出
     */
    public static SpecificTypeSingleResponse fromSpecificType(SpecificType specificType) {
        if (specificType == null) {
            throw new IllegalArgumentException("SpecificType 实体不能为空，无法转换为 SpecificTypeAddResponse");
        }

        SpecificTypeSingleResponse response = new SpecificTypeSingleResponse();
        response.setSpecificTypeId(specificType.getId());
        response.setName(specificType.getName());
        response.setCategoryId(specificType.getCategoryId());
        return response;
    }

    /**
     * 格式化字符串：供列表拼接或日志打印使用
     * @return 单条具体类型的格式化信息
     */
    public String getFormattedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("├─ 具体类型ID：").append(specificTypeId).append("\n")
          .append("├─ 具体类型名称：").append(name == null ? "无" : name).append("\n")
          .append("└─ 关联大类ID：").append(categoryId).append("\n");
        return sb.toString();
    }

    /**
     * 打印具体类型详情（与其他响应类的打印逻辑一致）
     */
    public void printSelf() {
        StringBuilder sb = new StringBuilder();
        sb.append("SpecificTypeAddResponse 详情：\n")
          .append(getFormattedString());
        System.out.println(sb.toString());
    }
}