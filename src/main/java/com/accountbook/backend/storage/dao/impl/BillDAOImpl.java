package com.accountbook.backend.storage.dao.impl;

import com.accountbook.backend.common.exception.BusinessServiceException;
import com.accountbook.backend.common.util.BillConvertUtils;
import com.accountbook.backend.common.util.NameConvertUtils;
import com.accountbook.backend.storage.dao.BaseDAO;
import com.accountbook.backend.storage.dao.BillDAO;
import com.accountbook.backend.storage.entity.Bill;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*具体BillDAO实现 */
public class BillDAOImpl extends BaseDAO implements BillDAO{

    // 表名常量（与数据库表名一致）
    private static final String TABLE_BILL = "bill";

    /**
     * 新增账单
     * @param billMap 账单字段映射（key:数据库字段名，如"bill_time"；value:对应值）
     * @return 主键id
     */
    public int addBill(Map<String, Object> billMap) {
        try{
            return super.insert(TABLE_BILL, billMap);
        } catch(Exception e)
        {
            System.err.println(e);
            return -1;
        }
    }

    /**
     * 根据条件更新账单
     * @param fieldMap 要更新的字段-值映射
     * @param condition 条件（如"id=?"）
     * @param params 条件参数
     * @return 影响行数
     */
    public int updateBill(Map<String, Object> fieldMap, String condition, Object... params) {
        try{
            return super.update(TABLE_BILL, fieldMap, condition, params);
        }catch(Exception e)
        {
            System.err.println(e);
            return -1;
        }
    }

    /**
     * 根据条件删除账单
     * @param condition 条件（如"id=?"）
     * @param params 条件参数
     * @return 影响行数
     */
    public int deleteBill(String condition, Object... params) {
        try{
            return super.delete(TABLE_BILL, condition, params);
        }catch(Exception e)
        {
            System.err.println(e);
            return -1;
        }
    }

    /**
     * 单表查询账单（支持条件过滤）
     * @param fields 要查询的字段（如"id,amount"）
     * @param condition 条件（如"category_id=?"）
     * @param params 条件参数
     * @return 结果列表（Map<字段名, 值>）
     */
    public List<Map<String, Object>> queryBills(String fields, String condition, Object... params) {
        try{
            return super.query(TABLE_BILL, fields, condition, params);
        }catch(Exception e)
        {
            System.err.println(e);
            return null;
        }
    }

    /**
     * 关联查询账单（多表JOIN，如关联category和specific_type表）
     * @param customSql 自定义关联查询SQL
     * @param params SQL参数
     * @return 结果列表（Map<字段名, 值>）
     */
    public List<Map<String, Object>> queryBillsWithJoin(String customSql, Object... params) {
        try{
            return super.queryByCustomSql(customSql, params);
        }catch(Exception e)
        {
            System.err.println(e);
            return null;
        }
    }

    /**
     * 新增实现：通过ID查询完整Bill实体
     * 整合原BillByIdQueryUtils的核心逻辑
     */
    @Override
    public Bill queryBillById(Integer billId) {
        // 1. 校验ID合法性
        validateBillId(billId);

        try {
            // 2. 调用父类查询方法：查完整字段（*），条件为id=?
            List<Map<String, Object>> mapList = super.query(
                TABLE_BILL,
                "*",  // 查询所有字段
                "id = ?",  // 条件：ID精确匹配
                billId  // 参数：账单ID
            );

            // 3. 处理查询结果（无数据则抛异常）
            if (mapList == null || mapList.isEmpty()) {
                throw new BusinessServiceException("查询失败：未找到ID为" + billId + "的账单");
            }

            // 4. 转换Map为Bill实体（复用转换工具类）
            return BillConvertUtils.mapToBill(mapList.get(0));

        } catch (BusinessServiceException e) {
            // 业务异常直接抛出（已包含明确信息）
            throw e;
        } catch (Exception e) {
            // 其他异常（如SQL异常）包装为业务异常
            throw new BusinessServiceException("查询ID为" + billId + "的账单时发生系统异常：" + e.getMessage());
        }
    }

    /**
     * 通过ID删除账单（封装deleteBill方法，语义更明确）
     * @param billId 账单ID（主键）
     * @return 影响行数（1=删除成功，0=未找到对应账单，-1=删除失败）
     */
    @Override
    public int deleteBillById(Integer billId) {
        // 1. 校验ID合法性（复用已有的校验逻辑）
        validateBillId(billId);
        try {
            // 2. 调用已有的deleteBill方法，条件为"id=?"，参数为billId
            return super.delete(TABLE_BILL, "id = ?", billId);
        } catch (Exception e) {
            System.err.println("删除ID为" + billId + "的账单失败：" + e.getMessage());
            return -1; // 异常时返回-1标识失败
        }
    }

    /**
     * 以ID为条件修改账单（专门用于通过主键更新，语义明确）
     * @param billId 账单主键ID（必填，正整数）
     * @param fieldMap 要更新的字段-值映射（key:数据库字段名，value:新值）
     * @return 影响行数（1=更新成功，0=未找到对应账单，-1=更新失败）
     */
    @Override
    public int updateBillById(Integer billId, Map<String, Object> fieldMap) {
        // 1. 校验账单ID合法性（复用已有校验逻辑）
        validateBillId(billId);
        
        // 2. 校验更新字段Map非空（避免无意义的更新操作）
        if (fieldMap == null || fieldMap.isEmpty()) {
            System.err.println("更新失败：至少需要指定一个待修改的字段");
            return -1;
        }
        
        try {
            // 3. 调用已有的updateBill方法，条件固定为"id=?"，参数为billId
            return super.update(TABLE_BILL, fieldMap, "id = ?", billId);
        } catch (Exception e) {
            System.err.println("更新ID为" + billId + "的账单失败：" + e.getMessage());
            return -1;
        }
    }

        /**
     * 实现：查询所有账单（全字段），按时间由近到远排序
     */
    @Override
    public List<Bill> queryAllBillsOrderByTimeDesc() {
        try {
            // 1. 构建SQL：查询所有字段，按bill_time（账单时间）倒序（DESC）
            String sql = "SELECT * FROM " + TABLE_BILL + " ORDER BY bill_time DESC";

            // 2. 执行查询（调用父类自定义SQL查询方法）
            List<Map<String, Object>> mapList = super.queryByCustomSql(sql);

            // 3. 转换为Bill实体列表（空结果返回空列表，避免null）
            List<Bill> billList = new ArrayList<>();
            if (mapList != null && !mapList.isEmpty()) {
                for (Map<String, Object> map : mapList) {
                    billList.add(BillConvertUtils.mapToBill(map));
                }
            }

            return billList;

        } catch (Exception e) {
            // 统一包装为业务异常，携带具体错误信息
            throw new BusinessServiceException("查询所有账单（按时间倒序）失败：" + e.getMessage());
        }
    }

    /**
     * 按条件批量更新账单（实现接口方法）
     * @param queryMap 筛选条件（key：Java属性名，如"categoryId"；value：对应值）
     * @param updateMap 要更新的字段（key：Java属性名，如"categoryId"；value：新值）
     * @return 受影响的行数（-1=失败，0=无匹配数据，≥1=成功更新的行数）
     */
    @Override
    public int updateBillsByCondition(Map<String, Object> queryMap, Map<String, Object> updateMap) {
        // 1. 校验更新字段非空（避免无效更新）
        if (updateMap == null || updateMap.isEmpty()) {
            System.err.println("批量更新失败：至少需要一个待更新的字段");
            return -1;
        }

        try {
            // 2. 转换updateMap：Java属性名 → 数据库字段名（生成fieldMap）
            Map<String, Object> fieldMap = convertToDbFieldMap(updateMap);

            // 3. 处理queryMap：生成WHERE条件和参数
            String condition = "";
            List<Object> params = new ArrayList<>();
            if (queryMap != null && !queryMap.isEmpty()) {
                // 3.1 转换查询条件的Java属性名为数据库字段名
                List<String> conditionFragments = new ArrayList<>();
                for (Map.Entry<String, Object> entry : queryMap.entrySet()) {
                    String dbField = NameConvertUtils.camelToUnderline(entry.getKey());
                    conditionFragments.add(dbField + " = ?"); // 生成"field = ?"片段
                    params.add(entry.getValue()); // 收集条件参数值
                }
                // 3.2 拼接条件（如"category_id = ? AND specific_type_id = ?"）
                condition = String.join(" AND ", conditionFragments);
            }

            // 4. 调用父类update方法执行更新（复用BaseDAO的更新逻辑）
            // 注意：BaseDAO的update方法参数为（表名，更新字段，条件，条件参数）
            return super.update(TABLE_BILL, fieldMap, condition, params.toArray());

        } catch (Exception e) {
            System.err.println("批量更新账单失败：" + e.getMessage());
            return -1; // 异常时返回-1标识失败
        }
    }

    /**
     * 辅助方法：校验账单ID合法性（原BillByIdQueryUtils的校验逻辑）
     */
    private void validateBillId(Integer billId) {
        if (billId == null) {
            throw new BusinessServiceException("账单ID不能为空");
        }
        if (billId <= 0) {
            throw new BusinessServiceException("账单ID必须为正整数（当前值：" + billId + "）");
        }
    }

        /**
     * 辅助方法：将Java属性名的Map转换为数据库字段名的Map
     * （如{categoryId:1} → {category_id:1}）
     */
    private Map<String, Object> convertToDbFieldMap(Map<String, Object> javaPropMap) {
        if (javaPropMap == null) {
            return null;
        }
        // 遍历Map，转换key为数据库字段名
        Map<String, Object> dbFieldMap = new java.util.HashMap<>();
        for (Map.Entry<String, Object> entry : javaPropMap.entrySet()) {
            String dbField = NameConvertUtils.camelToUnderline(entry.getKey());
            dbFieldMap.put(dbField, entry.getValue());
        }
        return dbFieldMap;
    }
}