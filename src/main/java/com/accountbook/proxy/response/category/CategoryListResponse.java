package com.accountbook.proxy.response.category;

import com.accountbook.backend.storage.entity.Category;
import java.util.ArrayList;
import java.util.List;

/**
 * 分类列表响应：适配列表查询场景（无分页信息，基于 CategorySingleResponse 作为列表项）
 */
public class CategoryListResponse {
    // 列表数据：使用 CategorySingleResponse 作为列表项，复用单条响应的字段定义
    private List<CategorySingleResponse> items;

    /**
     * 静态转换方法：从 List<Category> 转换为 CategoryListResponse（包含 CategorySingleResponse 列表）
     * @param categoryList 数据库查询得到的 Category 实体列表
     * @return 转换后的分类列表响应对象
     */
    public static CategoryListResponse fromCategoryList(List<Category> categoryList) {
        CategoryListResponse response = new CategoryListResponse();
        List<CategorySingleResponse> itemList = new ArrayList<>();
        
        if (categoryList != null) {
            for (Category category : categoryList) {
                // 复用 CategorySingleResponse 已有的 fromCategory 方法，避免重复转换逻辑
                CategorySingleResponse singleResponse = CategorySingleResponse.fromCategory(category);
                itemList.add(singleResponse);
            }
        }
        
        response.setItems(itemList);
        return response;
    }

    /**
     * 打印当前列表响应的所有信息（包含每条 CategorySingleResponse 的详情）
     */
    public void printSelf() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== CategoryListResponse 信息 =====\n")
          .append("总条数: ").append(items == null ? 0 : items.size()).append("\n")
          .append("===== 分类列表详情 =====\n");
        
        if (items != null && !items.isEmpty()) {
            for (int i = 0; i < items.size(); i++) {
                sb.append("----- 第 ").append(i + 1).append(" 条 -----").append("\n")
                  .append(items.get(i).getFormattedString()); // 拼接单条分类的格式化字符串
            }
        } else {
            sb.append("无分类数据\n");
        }
        
        System.out.println(sb.toString());
    }

    // Getter + Setter（与 BillListResponse 保持一致）
    public List<CategorySingleResponse> getItems() {
        return items;
    }

    public void setItems(List<CategorySingleResponse> items) {
        this.items = items;
    }
}