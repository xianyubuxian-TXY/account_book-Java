package com.accountbook.backend.storage.db;

import java.sql.Connection;
import java.sql.Statement;

/*数据库初始化类 */
public class DBInitializer {
    public static void init() {
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement()) {

            // 1. 创建数据库（如果不存在）
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS account_book CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci");
            // 2. 切换到该数据库
            stmt.executeUpdate("USE account_book");
            // 3. 创建表
            //大类表：饮食、出行等
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS category (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(20) NOT NULL COMMENT '大类名称（如“饮食”“购物”）',
                    UNIQUE KEY uk_name (name) -- 确保大类名称不重复
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """);

            //具体类型表：如外卖、坐地铁（与大类相关联）
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS specific_type (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(20) NOT NULL COMMENT '具体类型名称（如“外卖”“买衣服”）',
                    category_id INT NOT NULL COMMENT '关联的大类ID',
                    FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE CASCADE, -- 大类删除时，关联的具体类型也删除
                    UNIQUE KEY uk_category_name (category_id, name) -- 同一大类下具体类型名称不重复
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """);

            //账单表
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS bill (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    bill_time DATETIME NOT NULL COMMENT '账单时间（格式：YYYY-MM-DD HH:MM）',
                    type TINYINT NOT NULL COMMENT '收支标识（-1=支出，1=收入）',
                    category_id INT NOT NULL COMMENT '关联的大类ID',
                    specific_type_id INT NOT NULL COMMENT '关联的具体类型ID',
                    amount DECIMAL(10,2) NOT NULL COMMENT '金额（单位：元）',
                    remark VARCHAR(100) COMMENT '备注信息',
                    FOREIGN KEY (category_id) REFERENCES category(id),
                    FOREIGN KEY (specific_type_id) REFERENCES specific_type(id)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """);

            //预算表
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS budget (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    category_id INT NOT NULL COMMENT '关联的大类ID（仅支出类）',
                    month VARCHAR(7) NOT NULL COMMENT '预算月份（格式：YYYY-MM，如“2025-10”）',
                    total_budget DECIMAL(10,2) NOT NULL COMMENT '月度总预算',
                    spent DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '已支出金额',
                    remaining DECIMAL(10,2) NOT NULL COMMENT '剩余预算（total_budget - spent）',
                    FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE CASCADE,
                    UNIQUE KEY uk_category_month (category_id, month) -- 同一大类同一月份仅1条预算
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """);
            System.out.println("数据库和表初始化成功;！");

        } catch (Exception e) {
            throw new RuntimeException("初始化数据库失败：" + e.getMessage());
        }
    }
}