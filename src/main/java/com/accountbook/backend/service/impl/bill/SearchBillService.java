package com.accountbook.backend.service.impl.bill;

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

public class SearchBillService implements BusinessService<BillSearchParams, BillListResponse> {
    private final BillDAO billDAO = DAOFactory.getBillDAO();

    @Override
    public BillListResponse execute(BillSearchParams params) throws Exception {
        System.out.println("执行账单查询业务");

        // 处理参数为空的情况（直接查询所有并按时间倒序）
        if (params == null || isAllParamsNull(params)) {
            System.out.println("无有效查询条件，默认返回所有账单（按时间由近到远）");
            List<Bill> allBills = billDAO.queryAllBillsOrderByTimeDesc();
            return BillListResponse.fromBillList(allBills);
        }

        // 1. 将查询参数转为 Map
        Map<String, Object> paramMap = params.toMap();
        if (paramMap.isEmpty()) {
            System.out.println("参数转换后为空，默认返回所有账单（按时间由近到远）");
            List<Bill> allBills = billDAO.queryAllBillsOrderByTimeDesc();
            return BillListResponse.fromBillList(allBills);
        }

        // 2. 动态拼接 SQL 条件和参数列表（重点修复：添加bill_id处理）
        StringBuilder condition = new StringBuilder();
        List<Object> sqlParams = new ArrayList<>();

        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value == null) {
                continue;
            }

            // 重点修复：添加对bill_id的处理
            switch (key) {
                case "id":  // 处理按ID查询的条件
                    condition.append("id = ? AND ");
                    sqlParams.add(value);
                    break;
                case "bill_date":
                    condition.append("bill_time BETWEEN CONCAT(?, ' 00:00:00') AND CONCAT(?, ' 23:59:59') AND ");
                    sqlParams.add(value);
                    sqlParams.add(value);
                    break;
                case "bill_date_start":
                    condition.append("bill_time >= CONCAT(?, ' 00:00:00') AND ");
                    sqlParams.add(value);
                    break;
                case "bill_date_end":
                    condition.append("bill_time <= CONCAT(?, ' 23:59:59') AND ");
                    sqlParams.add(value);
                    break;
                case "amount_min":
                    condition.append("amount >= ? AND ");
                    sqlParams.add(value);
                    break;
                case "amount_max":
                    condition.append("amount <= ? AND ");
                    sqlParams.add(value);
                    break;
                case "amount":
                    condition.append("amount = ? AND ");
                    sqlParams.add(value);
                    break;
                case "type":
                case "category_id":
                case "specific_type_id":
                    condition.append(key).append(" = ? AND ");
                    sqlParams.add(value);
                    break;
                case "remark":
                    condition.append("remark LIKE CONCAT('%', ?, '%') AND ");
                    sqlParams.add(value);
                    break;
                default:
                    break;
            }
        }

        // 3. 处理条件字符串（移除末尾多余的 "AND "）
        String finalCondition = "";
        if (condition.length() > 0) {
            finalCondition = condition.substring(0, condition.length() - 4);
        } else {
            System.out.println("所有查询条件均为null，默认返回所有账单（按时间由近到远）");
            List<Bill> allBills = billDAO.queryAllBillsOrderByTimeDesc();
            return BillListResponse.fromBillList(allBills);
        }

        // 4. 调用 DAO 查询
        List<Map<String, Object>> mapList = billDAO.queryBills("*", finalCondition, sqlParams.toArray());

        // 5. 转换结果并返回
        List<Bill> billList = BillConvertUtils.mapListToBillList(mapList);
        return BillListResponse.fromBillList(billList);
    }

    /**
     * 修复：添加对billId的检查，确保有billId时不视为"无有效条件"
     */
    private boolean isAllParamsNull(BillSearchParams params) {
        return params.getId() == null &&  // 新增：检查billId是否为null
               params.getTime() == null &&
               params.getTimeStart() == null &&
               params.getTimeEnd() == null &&
               params.getAmount() == null &&
               params.getAmountMin() == null &&
               params.getAmountMax() == null &&
               params.getType() == null &&
               params.getCategoryId() == null &&
               params.getSpecificTypeId() == null &&
               params.getRemark() == null;
    }
}