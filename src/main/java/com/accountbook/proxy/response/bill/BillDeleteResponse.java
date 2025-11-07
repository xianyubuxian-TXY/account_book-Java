package com.accountbook.proxy.response.bill;

/**
 * 适配：删除操作
 */
public class BillDeleteResponse {
    private Integer billId;

    public BillDeleteResponse(Integer billId) {
        this.billId = billId;
    }

    // 仅提供Getter（无Setter），确保结果不可篡改
    public Integer getBillId() {
        return billId;
    }
}