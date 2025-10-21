package com.accountbook.proxy.request.bill;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 修改账单请求参数类（参考AddBillParams设计风格）
 * 支持部分字段修改，包含参数转换为Map的方法
 */
public class BillChangeParams {
    // 必填字段：待修改的账单ID
    private Integer billId;
    // 可选字段：新的账单时间（格式：YYYY-MM-DD HH:mm）
    private String billTime;
    // 可选字段：新的收支类型（1=收入，-1=支出）
    private Integer type;
    // 可选字段：新的大类ID
    private Integer categoryId;
    // 可选字段：新的具体类型ID
    private Integer specificTypeId;
    // 可选字段：新的金额
    private BigDecimal amount;
    // 可选字段：新的备注
    private String remark;

    /**
     * 构造方法：必填账单ID，其他字段通过Setter动态设置（支持部分修改）
     * @param billId 待修改的账单ID（必填，正整数）
     */
    public BillChangeParams(Integer billId) {
        this.billId = billId;
    }

    /**
     * 全参构造方法（可选，适用于需要一次性设置所有字段的场景）
     */
    public BillChangeParams(Integer billId, String billTime, Integer type,
                           Integer categoryId, Integer specificTypeId, BigDecimal amount, String remark) {
        this.billId = billId;
        this.billTime = billTime;
        this.type = type;
        this.categoryId = categoryId;
        this.specificTypeId = specificTypeId;
        this.amount = amount;
        this.remark = remark;
    }

    /**
     * 将参数转换为 Map（键为数据库字段名，值为字段值）
     * 用于 SQL 更新操作的参数绑定，仅包含非空字段（避免覆盖原有值）
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        if (this.billTime != null) map.put("bill_time", this.billTime);
        if (this.type != null) map.put("type", this.type);
        if (this.categoryId != null) map.put("category_id", this.categoryId);
        if (this.specificTypeId != null) map.put("specific_type_id", this.specificTypeId);
        if (this.amount != null) map.put("amount", this.amount);
        if (this.remark != null) map.put("remark", this.remark);
        return map;
    }

    /**
     * 获取账单ID的Map（用于SQL的WHERE条件绑定）
     */
    public Map<String, Object> getBillIdMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", this.billId);
        return map;
    }

    // 紧凑格式的 Getter/Setter
    public Integer getBillId() { return billId; }
    public void setBillId(Integer billId) { this.billId = billId; }
    public String getBillTime() { return billTime; }
    public void setBillTime(String billTime) { this.billTime = billTime; }
    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public Integer getSpecificTypeId() { return specificTypeId; }
    public void setSpecificTypeId(Integer specificTypeId) { this.specificTypeId = specificTypeId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}