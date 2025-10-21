package com.accountbook.proxy.request.bill;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

// 注：移除TimeUtils依赖（不再需要拼接时分）
public class BillSearchParams {
    // 1. 原有单值查询字段（时间字段仅存“YYYY-MM-DD”格式）
    private String time;                // 单值日期（如“2025-10-23”，无时分）
    private Integer type;               // 收支类型（-1=支出，1=收入）
    private Integer categoryId;         // 大类ID
    private Integer specificTypeId;     // 具体类型ID
    private BigDecimal amount;          // 单值金额（如100.00）
    private String key;                 // 备注关键词

    // 2. 新增范围查询字段（时间范围同样仅“YYYY-MM-DD”格式）
    private String timeStart;           // 日期范围起始（如“2025-10-01”）
    private String timeEnd;             // 日期范围结束（如“2025-10-31”）
    private BigDecimal amountMin;       // 金额范围最小值（如50.00）
    private BigDecimal amountMax;       // 金额范围最大值（如200.00）

    //默认构造函数：其他字段通过Setter动态设置（支持部分修改）
    public BillSearchParams()
    {}

    // 3. 原有构造方法（修改：移除时分拼接，时间仅接收“YYYY-MM-DD”）
    public BillSearchParams(String billDate, Integer type, Integer categoryId,
                        Integer specificTypeId, BigDecimal amount, String remark) {
        this.time = billDate; // 直接赋值日期，不再拼接TimeUtils的时分
        this.type = type;
        this.categoryId = categoryId;
        this.specificTypeId = specificTypeId;
        this.amount = amount;
        this.key = remark;
    }

    // 4. 范围查询构造方法（时间参数仅“YYYY-MM-DD”，无时分）
    /**
     * 范围查询构造方法
     * @param timeStart 日期起始（如“2025-10-01”，可为null）
     * @param timeEnd 日期结束（如“2025-10-31”，可为null）
     * @param type 收支类型（可为null）
     * @param categoryId 大类ID（可为null）
     * @param specificTypeId 具体类型ID（可为null）
     * @param amountMin 金额最小值（可为null）
     * @param amountMax 金额最大值（可为null）
     * @param remark 备注关键词（可为null）
     */
    public BillSearchParams(String timeStart, String timeEnd, Integer type,
                        Integer categoryId, Integer specificTypeId,
                        BigDecimal amountMin, BigDecimal amountMax, String remark) {
        this.timeStart = timeStart; // 仅日期格式，无时分
        this.timeEnd = timeEnd;     // 仅日期格式，无时分
        this.type = type;
        this.categoryId = categoryId;
        this.specificTypeId = specificTypeId;
        this.amountMin = amountMin;
        this.amountMax = amountMax;
        this.key = remark;
    }

    // 5. Getter/Setter（时间字段注释明确格式，避免误用）
    // 原有字段 Getter/Setter
    public String getTime() { return time; }
    /**
     * 设置单值日期（仅支持“YYYY-MM-DD”格式，如“2025-10-23”）
     */
    public void setTime(String time) { this.time = time; }
    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public Integer getSpecificTypeId() { return specificTypeId; }
    public void setSpecificTypeId(Integer specificTypeId) { this.specificTypeId = specificTypeId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    // 新增范围字段 Getter/Setter（时间字段明确格式）
    public String getTimeStart() { return timeStart; }
    /**
     * 设置日期范围起始（仅支持“YYYY-MM-DD”格式，如“2025-10-01”）
     */
    public void setTimeStart(String timeStart) { this.timeStart = timeStart; }
    public String getTimeEnd() { return timeEnd; }
    /**
     * 设置日期范围结束（仅支持“YYYY-MM-DD”格式，如“2025-10-31”）
     */
    public void setTimeEnd(String timeEnd) { this.timeEnd = timeEnd; }
    public BigDecimal getAmountMin() { return amountMin; }
    public void setAmountMin(BigDecimal amountMin) { this.amountMin = amountMin; }
    public BigDecimal getAmountMax() { return amountMax; }
    public void setAmountMax(BigDecimal amountMax) { this.amountMax = amountMax; }

    /**
     * 优化 toMap 方法：时间字段仅存“YYYY-MM-DD”，不包含时分
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        // 原有单值字段映射（时间仅“YYYY-MM-DD”）
        if (this.time != null && !this.time.trim().isEmpty()) {
            map.put("bill_date", this.time); // 键名改为bill_date，明确是日期（非完整时间）
        }
        if (this.amount != null) {
            map.put("amount", this.amount);
        }

        // 新增范围字段映射（时间范围仅“YYYY-MM-DD”）
        if (this.timeStart != null && !this.timeStart.trim().isEmpty()) {
            map.put("bill_date_start", this.timeStart); // 日期起始键名
        }
        if (this.timeEnd != null && !this.timeEnd.trim().isEmpty()) {
            map.put("bill_date_end", this.timeEnd);     // 日期结束键名
        }
        if (this.amountMin != null) {
            map.put("amount_min", this.amountMin);
        }
        if (this.amountMax != null) {
            map.put("amount_max", this.amountMax);
        }

        // 非范围字段映射（不变）
        map.put("type", this.type);
        map.put("category_id", this.categoryId);
        map.put("specific_type_id", this.specificTypeId);
        map.put("remark", this.key);

        return map;
    }
}