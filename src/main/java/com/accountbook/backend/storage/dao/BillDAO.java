package com.accountbook.backend.storage.dao;

import com.accountbook.backend.storage.entity.Bill;
import java.util.List;
import java.util.Map;

public interface BillDAO {
    int addBill(Map<String, Object> billMap);
    int updateBill(Map<String, Object> fieldMap, String condition, Object... params);
    int deleteBill(String condition, Object... params);
    List<Map<String, Object>> queryBills(String fields, String condition, Object... params);
    List<Map<String, Object>> queryBillsWithJoin(String customSql, Object... params);

    /**
     * 以ID为条件查询账单
     * @param billId 账单主键ID
     * @return 影响行数
     */
    Bill queryBillById(Integer billId);

    /**
     * 以ID为条件删除账单
     * @param billId 账单主键ID
     * @return 影响行数
     */
    int deleteBillById(Integer billId);
    
    /**
     * 以ID为条件修改账单
     * @param billId 账单主键ID
     * @param fieldMap 要更新的字段-值映射
     * @return 影响行数
     */
    int updateBillById(Integer billId, Map<String, Object> fieldMap);

    /**
     * 查询所有账单（包含全部字段），按时间由近到远排序（最新的在前）
     * @return 所有账单的实体列表（按bill_time DESC排序）
     */
    List<Bill> queryAllBillsOrderByTimeDesc();

    // 按条件更新符合条件的账单
    int updateBillsByCondition(Map<String, Object> queryMap, Map<String, Object> updateMap);
}