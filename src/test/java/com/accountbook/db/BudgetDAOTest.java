package com.accountbook.db;

import com.accountbook.backend.storage.dao.impl.BudgetDAOImpl;
import com.accountbook.backend.storage.db.DBInitializer;
import com.accountbook.backend.storage.db.DBUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BudgetDAO 测试类：仅验证数据操作的正确性
 */
public class BudgetDAOTest {
    private static BudgetDAOImpl budgetDAO;
    private static int testBudgetId; // 测试用预算ID
    private static int tempBudgetId; // 临时测试预算ID，用于删除测试
    private static final String BASE_MONTH = "2025-10"; // 基础月份（长度为7）

    // 初始化：执行一次
    @BeforeAll
    static void init() {
        DBInitializer.init();
        budgetDAO = new BudgetDAOImpl();
        // 初始化测试大类
        initTestCategory();
        // 新增测试预算（使用基础月份）
        testBudgetId = addTestBudget(BASE_MONTH);
    }

    // 清理测试数据
    @AfterAll
    static void clean() {
        budgetDAO.deleteBudget("id = ?", testBudgetId);
        if (tempBudgetId != 0) {
            budgetDAO.deleteBudget("id = ?", tempBudgetId);
        }
        System.out.println("预算测试数据清理完成");
    }

    /**
     * 测试新增预算
     */
    @Test
    void testAddBudget() {
        List<Map<String, Object>> budgets = budgetDAO.queryBudgets("id", "id = ?", testBudgetId);
        assertFalse(budgets.isEmpty(), "新增预算失败：未查询到测试预算");
        assertEquals(testBudgetId, budgets.get(0).get("id"), "预算ID不匹配");
    }

    /**
     * 测试查询预算
     */
    @Test
    void testQueryBudgets() {
        // 按月份和大类ID查询
        List<Map<String, Object>> budgets = budgetDAO.queryBudgets(
                "total_budget, spent, remaining",
                "month = ? AND category_id = ?",
                BASE_MONTH, 1
        );
        
        assertFalse(budgets.isEmpty(), "按条件查询预算失败");
        Map<String, Object> budget = budgets.get(0);
        // 浮点数断言，允许精度差异
        assertEquals(1000.0, ((Number) budget.get("total_budget")).doubleValue(), "总预算值错误");
        assertEquals(0.0, ((Number) budget.get("spent")).doubleValue(), "已支出金额错误");
        assertEquals(1000.0, ((Number) budget.get("remaining")).doubleValue(), "剩余预算值错误");
    }

    /**
     * 测试更新预算
     */
    @Test
    void testUpdateBudget() {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("spent", 300.00);
        updateMap.put("remaining", 700.00);
        
        int rows = budgetDAO.updateBudget(updateMap, "id = ?", testBudgetId);
        assertEquals(1, rows, "更新预算失败");
        
        // 验证更新结果
        List<Map<String, Object>> budgets = budgetDAO.queryBudgets(
                "spent, remaining", "id = ?", testBudgetId
        );
        Map<String, Object> budget = budgets.get(0);
        assertEquals(300.0, ((Number) budget.get("spent")).doubleValue(), "已支出金额更新失败");
        assertEquals(700.0, ((Number) budget.get("remaining")).doubleValue(), "剩余预算更新失败");
    }

    /**
     * 测试删除预算
     */
    @Test
    void testDeleteBudget() {
        // 生成唯一月份（使用不同年份，确保长度为7）
        String uniqueMonth = "2025-11"; // 与基础月份2025-10不同
        tempBudgetId = addTestBudget(uniqueMonth);
        
        int rows = budgetDAO.deleteBudget("id = ?", tempBudgetId);
        assertEquals(1, rows, "删除预算失败");
        
        List<Map<String, Object>> budgets = budgetDAO.queryBudgets("id", "id = ?", tempBudgetId);
        assertTrue(budgets.isEmpty(), "删除预算失败：仍能查询到数据");
    }

    /**
     * 测试关联查询预算
     */
    @Test
    void testQueryBudgetsWithJoin() {
        String joinSql = "SELECT b.total_budget, c.name AS category_name " +
                         "FROM budget b " +
                         "JOIN category c ON b.category_id = c.id " +
                         "WHERE b.id = ?";
        List<Map<String, Object>> budgets = budgetDAO.queryBudgetsWithJoin(joinSql, testBudgetId);
        
        assertFalse(budgets.isEmpty(), "预算关联查询失败");
        assertEquals("饮食", budgets.get(0).get("category_name"), "关联查询大类名称错误");
    }

    // ---------------------- 测试辅助方法 ----------------------
    /**
     * 初始化测试大类（饮食）
     */
    private static void initTestCategory() {
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "INSERT IGNORE INTO category (name) VALUES ('饮食')";
            conn.createStatement().executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("初始化测试大类失败：" + e.getMessage(), e);
        }
    }

    /**
     * 新增测试预算（使用指定月份，确保长度为7）
     * @param month 预算月份（格式：YYYY-MM，长度为7）
     * @return 预算ID
     */
    private static int addTestBudget(String month) {
        Map<String, Object> budgetMap = new HashMap<>();
        budgetMap.put("category_id", 1); // 饮食大类ID
        budgetMap.put("month", month); // 使用传入的合法月份
        budgetMap.put("total_budget", 1000.00);
        budgetMap.put("spent", 0.00);
        budgetMap.put("remaining", 1000.00);
        
        budgetDAO.addBudget(budgetMap);
        
        // 查询新增的预算ID
        List<Map<String, Object>> budgets = budgetDAO.queryBudgets(
                "id", "category_id = ? AND month = ?", 1, month
        );
        return (Integer) budgets.get(0).get("id");
    }
}