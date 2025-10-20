package com.accountbook.backend.storage.dao.impl;

import java.util.List;
import java.util.Map;

import com.accountbook.backend.storage.dao.BaseDAO;
import com.accountbook.backend.storage.dao.BillDAO;

/*具体BillDAO实现 */
public class BillDAOImpl extends BaseDAO implements BillDAO{

    // 表名常量（与数据库表名一致）
    private static final String TABLE_BILL = "bill";

    /**
     * 新增账单
     * @param billMap 账单字段映射（key:数据库字段名，如"bill_time"；value:对应值）
     * @return 影响行数
     */
    public int addBill(Map<String, Object> billMap) {
        return super.insert(TABLE_BILL, billMap);
    }

    /**
     * 根据条件更新账单
     * @param fieldMap 要更新的字段-值映射
     * @param condition 条件（如"id=?"）
     * @param params 条件参数
     * @return 影响行数
     */
    public int updateBill(Map<String, Object> fieldMap, String condition, Object... params) {
        return super.update(TABLE_BILL, fieldMap, condition, params);
    }

    /**
     * 根据条件删除账单
     * @param condition 条件（如"id=?"）
     * @param params 条件参数
     * @return 影响行数
     */
    public int deleteBill(String condition, Object... params) {
        return super.delete(TABLE_BILL, condition, params);
    }

    /**
     * 单表查询账单（支持条件过滤）
     * @param fields 要查询的字段（如"id,amount"）
     * @param condition 条件（如"category_id=?"）
     * @param params 条件参数
     * @return 结果列表（Map<字段名, 值>）
     */
    public List<Map<String, Object>> queryBills(String fields, String condition, Object... params) {
        return super.query(TABLE_BILL, fields, condition, params);
    }

    /**
     * 关联查询账单（多表JOIN，如关联category和specific_type表）
     * @param customSql 自定义关联查询SQL
     * @param params SQL参数
     * @return 结果列表（Map<字段名, 值>）
     */
    public List<Map<String, Object>> queryBillsWithJoin(String customSql, Object... params) {
        return super.queryByCustomSql(customSql, params);
    }
}
