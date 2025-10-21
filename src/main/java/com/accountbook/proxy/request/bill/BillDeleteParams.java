package com.accountbook.proxy.request.bill;

// 删除账单只需账单ID
public class BillDeleteParams {
    private Integer billId; // 要删除的账单ID

    public BillDeleteParams(Integer billId) {
        this.billId = billId;
    }

    // getter/setter
    public Integer getBillId() { return billId; }
    public void setBillId(Integer billId) { this.billId = billId; }
}
