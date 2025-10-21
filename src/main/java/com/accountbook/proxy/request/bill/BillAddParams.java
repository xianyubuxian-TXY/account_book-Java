package com.accountbook.proxy.request.bill;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.accountbook.backend.common.util.TimeUtils;

public class BillAddParams {
    private String time;
    private Integer type;
    private Integer categoryId;
    private Integer specificTypeId;
    private BigDecimal amount;
    private String remark;

    //默认构造函数：其他字段通过Setter动态设置（支持部分修改）
    public BillAddParams()
    {}

    // 构造方法
    public BillAddParams(String billTime, Integer type, Integer categoryId,
                        Integer specificTypeId, BigDecimal amount, String remark) {
        this.time = billTime+" "+TimeUtils.getInstance().getCurrentTimeHHmm();
        this.type = type;
        this.categoryId = categoryId;
        this.specificTypeId = specificTypeId;
        this.amount = amount;
        this.remark = remark;
    }

    // 已有的 getter/setter 方法（省略，保持不变）
    public String getTime() { return time; }
    public void setBillTime(String billTime) { this.time = billTime; }
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

    /**
     * 将参数转换为 Map（键为字段名，值为字段值）
     * 用于 SQL 参数绑定、JSON 序列化等场景
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("bill_time", this.time);      // 账单时间
        map.put("type", this.type);              // 收支类型
        map.put("category_id", this.categoryId);  // 大类ID
        map.put("specific_type_id", this.specificTypeId);  // 具体类型ID
        map.put("amount", this.amount);          // 金额
        map.put("remark", this.remark);          // 备注（允许为null）
        return map;
    }
}
