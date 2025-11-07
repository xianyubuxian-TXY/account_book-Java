package com.accountbook.proxy.response.specific_type;

/**
 * 具体类型删除响应：返回被删除的具体类型ID及关联的大类ID
 */
public class SpecificTypeDeleteResponse {
    // 被删除的具体类型ID（前端用于刷新列表或校验）
    private Integer specificTypeId;
    // 关联的大类ID（前端用于大类-具体类型联动刷新）
    private Integer categoryId;

    // 全参构造：初始化被删除的具体类型ID和关联大类ID
    public SpecificTypeDeleteResponse(Integer specificTypeId, Integer categoryId) {
        this.specificTypeId = specificTypeId;
        this.categoryId = categoryId;
    }

    // Getter + Setter（与其他响应类保持一致，提供完整访问能力）
    public Integer getSpecificTypeId() {
        return specificTypeId;
    }

    public void setSpecificTypeId(Integer specificTypeId) {
        this.specificTypeId = specificTypeId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * 格式化字符串：用于日志打印或前端提示拼接
     * @return 格式化的删除信息
     */
    public String getFormattedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("├─ 已删除的具体类型ID：").append(specificTypeId).append("\n")
          .append("└─ 关联的大类ID：").append(categoryId).append("\n");
        return sb.toString();
    }

    /**
     * 打印删除响应详情（与其他响应类的printSelf逻辑一致）
     */
    public void printSelf() {
        StringBuilder sb = new StringBuilder();
        sb.append("SpecificTypeDeleteResponse 详情：\n")
          .append(getFormattedString());
        System.out.println(sb.toString());
    }
}