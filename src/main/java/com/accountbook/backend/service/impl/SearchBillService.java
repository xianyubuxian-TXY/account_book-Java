package com.accountbook.backend.service.impl;

import com.accountbook.backend.common.util.BillConvertUtils;
import com.accountbook.backend.service.BusinessService;
import com.accountbook.backend.storage.dao.BillDAO;
import com.accountbook.backend.storage.dao.factory.DAOFactory;
import com.accountbook.backend.storage.entity.Bill;
import com.accountbook.proxy.request.bill.BillSearchParams;
import com.accountbook.proxy.response.bill.BillListResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchBillService implements BusinessService<BillSearchParams,BillListResponse> {
    private final BillDAO billDAO = DAOFactory.getBillDAO();

    @Override
    public BillListResponse execute(BillSearchParams params) throws Exception {
        System.out.println("执行账单查询业务");
        if (params == null) {
            throw new IllegalArgumentException("查询参数不能为空");
        }

        // 1. 将查询参数转为 Map
        Map<String, Object> paramMap = params.toMap();
        if (paramMap.isEmpty()) {
            throw new IllegalArgumentException("查询条件不能为空（防止全表查询）");
        }

        // 2. 动态拼接 SQL 条件和参数列表
        StringBuilder condition = new StringBuilder();
        List<Object> sqlParams = new ArrayList<>();

        // 遍历 Map 生成 AND 连接的条件
        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value == null) {
                continue; // 跳过 null 值条件
            }

            // 根据字段类型拼接条件（重点处理范围查询字段）
            switch (key) {
                case "bill_date":
                    // 单值日期：匹配当天时间范围（bill_time 是完整时间字段）
                    condition.append("bill_time BETWEEN CONCAT(?, ' 00:00:00') AND CONCAT(?, ' 23:59:59') AND ");
                    sqlParams.add(value);
                    sqlParams.add(value);
                    break;
                case "bill_date_start":
                    // 日期范围起始：>= 拼接时分后的时间
                    condition.append("bill_time >= CONCAT(?, ' 00:00:00') AND ");
                    sqlParams.add(value);
                    break;
                case "bill_date_end":
                    // 日期范围结束：<= 拼接时分后的时间
                    condition.append("bill_time <= CONCAT(?, ' 23:59:59') AND ");
                    sqlParams.add(value);
                    break;
                case "amount_min":
                    // 金额最小值：>=
                    condition.append("amount >= ? AND ");
                    sqlParams.add(value);
                    break;
                case "amount_max":
                    // 金额最大值：<=
                    condition.append("amount <= ? AND ");
                    sqlParams.add(value);
                    break;
                case "amount":
                    // 单值金额：=
                    condition.append("amount = ? AND ");
                    sqlParams.add(value);
                    break;
                case "type":
                case "category_id":
                case "specific_type_id":
                    // 普通数值字段：=
                    condition.append(key).append(" = ? AND ");
                    sqlParams.add(value);
                    break;
                case "remark":
                    // 备注关键词：模糊查询
                    condition.append("remark LIKE CONCAT('%', ?, '%') AND ");
                    sqlParams.add(value);
                    break;
                default:
                    // 忽略未知字段
                    break;
            }
        }

        // 3. 处理条件字符串（移除末尾多余的 "AND "）
        String finalCondition = "";
        if (condition.length() > 0) {
            finalCondition = condition.substring(0, condition.length() - 4);
        } else {
            throw new IllegalArgumentException("无有效查询条件");
        }

        // 4. 调用 DAO 查询完整字段（字段参数传 "*"）
        List<Map<String, Object>> mapList = billDAO.queryBills("*", finalCondition, sqlParams.toArray());

        // 5. 将 Map 列表转为 Bill 实体列表
        List<Bill> billList=BillConvertUtils.mapListToBillList(mapList);
        // 6.将Bill实体列表 转为 BillListResponse
        return BillListResponse.fromBillList(billList);
    }
}