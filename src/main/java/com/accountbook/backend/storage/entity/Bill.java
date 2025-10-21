package com.accountbook.backend.storage.entity;

import com.accountbook.backend.common.util.TimeUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Bill {
    private Integer id; // 新增：数据库自增的id
    private String time; // 时间（格式：YYYY-MM-DD HH:MM）
    private Integer type; // 收支类型（用tinyint对应，比如1代表收入，2代表支出）
    private Integer categoryId; // 大类ID（关联category表）
    private Integer specificTypeId; // 具体类型ID（关联specific_type表）
    private BigDecimal amount; // 金额
    private String remark; // 备注

    //分类名称（从category表查询）
    private String categoryName;
    // 具体类型名称（从specific_type表查询）
    private String specificTypeName;
    
    public Bill(){

    }

    // 构造方法：不需要id（id由数据库生成）
    public Bill(String time, Integer type, Integer categoryId, Integer specificTypeId, BigDecimal amount, String remark) {
        if(time==null)
        {
            this.time=TimeUtils.getInstance().getCurrentTime();
        }
        else this.time = time + " " + TimeUtils.getInstance().getCurrentTimeHHmm();
        this.type = type;
        this.categoryId = categoryId;
        this.specificTypeId = specificTypeId;
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
        this.remark = remark;
    }

    // getter 和 setter 方法
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getSpecificTypeId() {
        return specificTypeId;
    }

    public void setSpecificTypeId(Integer specificTypeId) {
        this.specificTypeId = specificTypeId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    // Getter & Setter（省略原有方法，新增以下）
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getSpecificTypeName() {
        return specificTypeName;
    }

    public void setSpecificTypeName(String specificTypeName) {
        this.specificTypeName = specificTypeName;
    }

    // 打印方法（包含名称）
    public void printSelf() {
        System.out.printf(
            "id=%d, time=%s, type=%d, categoryId=%d, categoryName=%s, specificTypeId=%d, specificTypeName=%s, amount=%.2f, remark=%s\n",
            id, time, type, categoryId, categoryName, specificTypeId, specificTypeName, amount.doubleValue(), remark
        );
    }
}