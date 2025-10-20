package com.accountbook.db;

import com.accountbook.backend.storage.dao.impl.BillDAOImpl;
import com.accountbook.backend.storage.db.DBInitializer;
import com.accountbook.backend.storage.db.DBUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BillDAO 测试类：仅验证数据操作的正确性
 */
public class BillDAOTest {
    private static BillDAOImpl billDAO;
    private static int testBillId; // 用于存储测试用账单ID，方便后续更新/删除操作
    private static int categoryId; // 动态获取的饮食大类ID
    private static int specificTypeId; // 动态获取的午餐具体类型ID

    // 初始化：执行一次（创建数据库表、初始化DAO实例）
    @BeforeAll
    static void init() {
        // 初始化数据库和表结构
        DBInitializer.init();
        // 创建 BillDAO 实例
        billDAO = new BillDAOImpl();
        // 初始化测试数据（大类和具体类型）并动态获取ID
        initTestCategoryAndSpecificType();
        // 新增测试账单，保存ID供后续使用
        testBillId = addTestBill();
    }

    // 清理：测试结束后删除测试数据
    @AfterAll
    static void clean() {
        // 删除测试账单
        billDAO.deleteBill("id = ?", testBillId);
        System.out.println("测试数据清理完成");
    }

    /**
     * 测试新增账单
     */
    @Test
    void testAddBill() {
        // 验证初始化时新增的账单是否存在
        List<Map<String, Object>> bills = billDAO.queryBills("id", "id = ?", testBillId);
        assertFalse(bills.isEmpty(), "新增账单失败：未查询到测试账单");
        assertEquals(testBillId, bills.get(0).get("id"), "新增账单ID不匹配");
    }

    /**
     * 测试查询账单（单表查询）
     */
    @Test
    void testQueryBills() {
        // 按动态获取的大类ID查询
        List<Map<String, Object>> bills = billDAO.queryBills("id, amount, bill_time", "category_id = ?", categoryId);
        assertFalse(bills.isEmpty(), "按大类查询账单失败：未查询到数据");
        
        // 验证查询结果字段正确性
        Map<String, Object> bill = bills.get(0);
        assertTrue(bill.containsKey("id"), "查询结果缺少id字段");
        assertTrue(bill.containsKey("amount"), "查询结果缺少amount字段");
        assertTrue(bill.containsKey("bill_time"), "查询结果缺少bill_time字段");
    }

    /**
     * 测试关联查询账单（关联category和specific_type表）
     */
    @Test
    void testQueryBillsWithJoin() {
        String joinSql = "SELECT b.id, c.name AS category_name, st.name AS specific_name " +
                         "FROM bill b " +
                         "JOIN category c ON b.category_id = c.id " +
                         "JOIN specific_type st ON b.specific_type_id = st.id " +
                         "WHERE b.id = ?";
        List<Map<String, Object>> bills = billDAO.queryBillsWithJoin(joinSql, testBillId);
        
        // 新增：打印查询结果，辅助调试
        System.out.println("关联查询结果：" + bills);
        
        assertFalse(bills.isEmpty(), "关联查询失败：未查询到数据");
        Map<String, Object> bill = bills.get(0);
        // 新增：验证category_name字段存在
        assertTrue(bill.containsKey("category_name"), "查询结果缺少category_name字段");
        assertEquals("饮食", bill.get("category_name"), "关联查询大类名称错误");
        assertEquals("午餐", bill.get("specific_name"), "关联查询具体类型名称错误");
    }

    /**
     * 测试更新账单
     */
    @Test
    void testUpdateBill() {
        // 构造更新字段（修改备注）
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("remark", "测试更新后的备注");
        
        // 执行更新操作
        int rows = billDAO.updateBill(updateMap, "id = ?", testBillId);
        assertEquals(1, rows, "更新账单失败：影响行数不为1");
        
        // 验证更新结果
        List<Map<String, Object>> bills = billDAO.queryBills("remark", "id = ?", testBillId);
        assertEquals("测试更新后的备注", bills.get(0).get("remark"), "账单备注更新失败");
    }

    /**
     * 测试删除账单（单独测试，不影响其他用例）
     */
    @Test
    void testDeleteBill() {
        // 新增一条临时账单用于测试删除
        int tempBillId = addTestBill();
        
        // 执行删除操作
        int rows = billDAO.deleteBill("id = ?", tempBillId);
        assertEquals(1, rows, "删除账单失败：影响行数不为1");
        
        // 验证删除结果
        List<Map<String, Object>> bills = billDAO.queryBills("id", "id = ?", tempBillId);
        assertTrue(bills.isEmpty(), "删除账单失败：仍能查询到该账单");
    }

    // ---------------------- 测试辅助方法 ----------------------
    /**
     * 初始化测试用的大类和具体类型，并动态获取ID
     */
    private static void initTestCategoryAndSpecificType() {
        try (Connection conn = DBUtil.getConnection()) {
            // 开启事务
            conn.setAutoCommit(false);
    
            try (Statement stmt = conn.createStatement()) {
                // 插入大类
                stmt.executeUpdate("INSERT IGNORE INTO category (name) VALUES ('饮食')");
                // 查询大类ID
                ResultSet catRs = stmt.executeQuery("SELECT id FROM category WHERE name = '饮食'");
                if (catRs.next()) {
                    categoryId = catRs.getInt("id");
                    System.out.println("饮食大类ID：" + categoryId);
                } else {
                    throw new RuntimeException("未查询到'饮食'大类，初始化失败");
                }
                
                // 插入具体类型（关联大类）
                stmt.executeUpdate("INSERT IGNORE INTO specific_type (name, category_id) VALUES ('午餐', " + categoryId + ")");
                // 查询具体类型ID
                ResultSet specRs = stmt.executeQuery("SELECT id FROM specific_type WHERE name = '午餐' AND category_id = " + categoryId);
                if (specRs.next()) {
                    specificTypeId = specRs.getInt("id");
                    System.out.println("午餐具体类型ID：" + specificTypeId);
                } else {
                    throw new RuntimeException("未查询到'午餐'具体类型，初始化失败");
                }
                
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("初始化测试分类失败：" + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("获取数据库连接失败：" + e.getMessage(), e);
        }
    }

    /**
     * 新增测试账单，使用动态获取的ID
     */
    private static int addTestBill() {
        Map<String, Object> billMap = new HashMap<>();
        billMap.put("bill_time", "2025-10-20 12:00:00");
        billMap.put("type", -1); // 支出
        billMap.put("category_id", categoryId); // 使用动态获取的大类ID
        billMap.put("specific_type_id", specificTypeId); // 使用动态获取的具体类型ID
        billMap.put("amount", 35.50);
        billMap.put("remark", "测试账单");
        
        // 执行新增操作
        billDAO.addBill(billMap);
        
        // 查询刚新增的账单ID
        List<Map<String, Object>> bills = billDAO.queryBills("id", 
                "bill_time = ? AND amount = ?", "2025-10-20 12:00:00", 35.50);
        return (Integer) bills.get(0).get("id");
    }
}