package com.accountbook.proxy.response.bill;

/**
 * 适配：删除操作
 */
public class BillDeleteResponse {

    // 被操作的账单ID（核心标识，用于前端同步本地数据）
    private Integer billId;

    public BillDeleteResponse(Integer billId) {
        this.billId = billId;
    }

    // 仅提供Getter（无Setter），确保结果不可篡改
    public Integer getBillId() {
        return billId;
    }
}