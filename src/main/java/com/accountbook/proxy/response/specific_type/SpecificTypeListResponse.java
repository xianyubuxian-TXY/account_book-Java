package com.accountbook.proxy.response.specific_type;

import com.accountbook.backend.storage.entity.SpecificType;
import java.util.ArrayList;
import java.util.List;

/**
 * 具体类型列表响应：适配列表查询场景（无分页信息，基于 SpecificTypeSingleResponse 作为列表项）
 */
public class SpecificTypeListResponse {
    // 列表数据：使用 SpecificTypeSingleResponse 作为列表项，复用单条响应的字段定义
    private List<SpecificTypeSingleResponse> items;

    /**
     * 静态转换方法：从 List<SpecificType> 转换为 SpecificTypeListResponse（包含 SpecificTypeSingleResponse 列表）
     * @param specificTypeList 数据库查询得到的 SpecificType 实体列表
     * @return 转换后的具体类型列表响应对象
     */
    public static SpecificTypeListResponse fromSpecificTypeList(List<SpecificType> specificTypeList) {
        SpecificTypeListResponse response = new SpecificTypeListResponse();
        List<SpecificTypeSingleResponse> itemList = new ArrayList<>();
        
        if (specificTypeList != null) {
            for (SpecificType specificType : specificTypeList) {
                // 复用 SpecificTypeSingleResponse 已有的 fromSpecificType 方法，避免重复转换逻辑
                SpecificTypeSingleResponse singleResponse = SpecificTypeSingleResponse.fromSpecificType(specificType);
                itemList.add(singleResponse);
            }
        }
        
        response.setItems(itemList);
        return response;
    }

    /**
     * 打印当前列表响应的所有信息（包含每条 SpecificTypeSingleResponse 的详情）
     */
    public void printSelf() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== SpecificTypeListResponse 信息 =====\n")
          .append("总条数: ").append(items == null ? 0 : items.size()).append("\n")
          .append("===== 具体类型列表详情 =====\n");
        
        if (items != null && !items.isEmpty()) {
            for (int i = 0; i < items.size(); i++) {
                sb.append("----- 第 ").append(i + 1).append(" 条 -----").append("\n")
                  .append(items.get(i).getFormattedString()); // 拼接单条具体类型的格式化字符串
            }
        } else {
            sb.append("无具体类型数据\n");
        }
        
        System.out.println(sb.toString());
    }

    // Getter + Setter（与其他列表响应类保持一致）
    public List<SpecificTypeSingleResponse> getItems() {
        return items;
    }

    public void setItems(List<SpecificTypeSingleResponse> items) {
        this.items = items;
    }
}