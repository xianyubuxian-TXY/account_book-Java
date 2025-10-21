package com.accountbook.proxy.response.bill;

import com.accountbook.backend.storage.entity.Bill;
import java.util.ArrayList;
import java.util.List;

/**
 * 账单列表响应：适配列表查询场景（无分页信息，基于 BillSingleResponse 作为列表项）
 */
public class BillListResponse {
    // 列表数据：直接使用 BillSingleResponse 作为列表项，复用单条响应的字段定义
    private List<BillSingleResponse> items;

    /**
     * 静态转换方法：从 List<Bill> 转换为 BillListResponse（包含 BillSingleResponse 列表）
     * @param billList 数据库查询得到的 Bill 实体列表
     * @return 转换后的账单列表响应对象
     */
    public static BillListResponse fromBillList(List<Bill> billList) {
        BillListResponse response = new BillListResponse();
        List<BillSingleResponse> itemList = new ArrayList<>();
        
        if (billList != null) {
            for (Bill bill : billList) {
                // 复用 BillSingleResponse 已有的 fromBill 方法，避免重复转换逻辑
                BillSingleResponse singleResponse = BillSingleResponse.fromBill(bill);
                itemList.add(singleResponse);
            }
        }
        
        response.setItems(itemList);
        return response;
    }

    /**
     * 打印当前列表响应的所有信息（包含每条 BillSingleResponse 的详情）
     */
    public void printSelf() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== BillListResponse 信息 =====\n")
          .append("总条数: ").append(items == null ? 0 : items.size()).append("\n")
          .append("===== 账单列表详情 =====\n");
        
        if (items != null && !items.isEmpty()) {
            for (int i = 0; i < items.size(); i++) {
                sb.append("----- 第 ").append(i + 1).append(" 条 -----").append("\n")
                  .append(items.get(i).getFormattedString()); // 拼接单条账单的格式化字符串
            }
        } else {
            sb.append("无账单数据\n");
        }
        
        System.out.println(sb.toString());
    }

    // Getter + Setter
    public List<BillSingleResponse> getItems() {
        return items;
    }

    public void setItems(List<BillSingleResponse> items) {
        this.items = items;
    }
}