package com.accountbook.backend.storage.dao;

import java.util.List;
import java.util.Map;

// 账单 DAO 接口
public interface BillDAO {
    int addBill(Map<String, Object> billMap);
    int updateBill(Map<String, Object> fieldMap, String condition, Object... params);
    int deleteBill(String condition, Object... params);
    List<Map<String, Object>> queryBills(String fields, String condition, Object... params) ;
    List<Map<String, Object>> queryBillsWithJoin(String customSql, Object... params);
}