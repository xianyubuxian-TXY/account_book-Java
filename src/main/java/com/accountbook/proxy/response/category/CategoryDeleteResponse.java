package com.accountbook.proxy.response.category;

/**
 * 分类删除响应：返回被删除的分类ID信息
 */
public class CategoryDeleteResponse {
    // 被删除的分类ID（前端用于刷新列表或提示用户）
    private Integer categoryId;

    // 构造方法：接收被删除的分类ID
    public CategoryDeleteResponse(Integer categoryId) {
        this.categoryId = categoryId;
    }

    // Getter + Setter（与其他响应类保持一致，提供完整访问能力）
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
        return "└─ 已删除的分类ID：" + categoryId + "\n";
    }

    /**
     * 打印删除响应详情（与其他响应类的printSelf逻辑一致）
     */
    public void printSelf() {
        StringBuilder sb = new StringBuilder();
        sb.append("CategoryDeleteResponse 详情：\n")
          .append(getFormattedString());
        System.out.println(sb.toString());
    }
}