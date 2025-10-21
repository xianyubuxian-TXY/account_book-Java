package com.accountbook.proxy.response.bill;


import com.accountbook.backend.storage.entity.Bill;
import java.math.BigDecimal;

/**
 * 单条账单详情响应：适配新增/修改/单查场景
 */
public class BillSingleResponse {
    // 账单ID（前端后续删除/修改需用，核心标识）
    private Integer billId;
    // 账单时间（前端展示用）
    private String billTime;
    // 收支类型（-1=支出，1=收入，前端判断展示颜色/图标）
    private Integer type;
    // 大类ID（前端若需跳转分类页，用此ID）
    private Integer categoryId;
    // 具体类型ID（前端展示具体消费类型）
    private Integer specificTypeId;
    // 金额（前端核心展示数据）
    private BigDecimal amount;
    // 备注（前端可选展示）
    private String remark;

    // Getter + Setter（仅保留基础访问方法，无复杂逻辑）
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

    /**
     * 静态转换方法：从 Bill 实体构建 BillSingleResponse
     * @param bill 数据库查询得到的 Bill 实体
     * @return 转换后的账单详情响应对象
     * @throws IllegalArgumentException 当输入 Bill 为 null 时抛出
     */
    public static BillSingleResponse fromBill(Bill bill) {
        // 校验输入实体非空
        if (bill == null) {
            throw new IllegalArgumentException("Bill 实体不能为空，无法转换为 BillSingleResponse");
        }

        BillSingleResponse response = new BillSingleResponse();
        // 字段一一映射（确保 Bill 实体的 Getter 方法名与字段对应）
        response.setBillId(bill.getId());
        response.setBillTime(bill.getTime());
        response.setType(bill.getType());
        response.setCategoryId(bill.getCategoryId());
        response.setSpecificTypeId(bill.getSpecificTypeId());
        response.setAmount(bill.getAmount());
        response.setRemark(bill.getRemark());

        return response;
    }

    /**
     * 新增：生成格式化的字符串（供列表拼接使用）
     * @return 单条账单的格式化字符串
     */
    public String getFormattedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("├─ 账单ID：").append(billId).append("\n")
          .append("├─ 账单时间：").append(billTime).append("\n")
          .append("├─ 收支类型：").append(type).append("（").append(type == 1 ? "收入" : "支出").append("）\n")
          .append("├─ 大类ID：").append(categoryId).append("\n")
          .append("├─ 具体类型ID：").append(specificTypeId).append("\n")
          .append("├─ 金额：").append(amount).append("\n")
          .append("└─ 备注：").append(remark == null ? "无" : remark).append("\n");
        return sb.toString();
    }

    /**
     * 保留原有功能：独立打印单条账单（不影响原有使用场景）
     */
    public void printSelf() {
        StringBuilder sb = new StringBuilder();
        sb.append("BillSingleResponse 详情：\n")
          .append(getFormattedString()); // 复用格式化字符串逻辑
        System.out.println(sb.toString());
    }
}