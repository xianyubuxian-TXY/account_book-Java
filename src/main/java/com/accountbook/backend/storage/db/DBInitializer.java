package com.accountbook.backend.storage.db;

import java.sql.Connection;
import java.sql.Statement;

/*数据库初始化类 */
public class DBInitializer {
    public static void init() {
        try (
            // 获取基础连接（仅连接服务器，未指定数据库）
            Connection conn = DBUtil.getBaseConnection();
            Statement stmt = conn.createStatement()
        ) {

            // 1. 创建数据库（在未指定库的连接上执行，无需依赖任何现有库）
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS account_book CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci");
            // 2. 切换到新创建的数据库
            stmt.executeUpdate("USE account_book");
            // 3. 后续创建表、插入数据（此时已在 account_book 库中）
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS category (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(20) NOT NULL COMMENT '大类名称',
                    UNIQUE KEY uk_name (name)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS specific_type (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(20) NOT NULL COMMENT '具体类型名称',
                    category_id INT NOT NULL,
                    FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE CASCADE,
                    UNIQUE KEY uk_category_name (category_id, name)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS bill (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    bill_time DATETIME NOT NULL,
                    type TINYINT NOT NULL,
                    category_id INT NOT NULL,
                    specific_type_id INT NOT NULL,
                    amount DECIMAL(10,2) NOT NULL,
                    remark VARCHAR(100),
                    FOREIGN KEY (category_id) REFERENCES category(id),
                    FOREIGN KEY (specific_type_id) REFERENCES specific_type(id)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS budget (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    category_id INT NOT NULL,
                    month VARCHAR(7) NOT NULL,
                    total_budget DECIMAL(10,2) NOT NULL,
                    spent DECIMAL(10,2) NOT NULL DEFAULT 0.00,
                    remaining DECIMAL(10,2) NOT NULL,
                    FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE CASCADE,
                    UNIQUE KEY uk_category_month (category_id, month)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """);

            // 初始化默认数据
            stmt.executeUpdate("INSERT IGNORE INTO category (name) VALUES ('无')");
            stmt.executeUpdate("""
                INSERT IGNORE INTO specific_type (name, category_id)
                SELECT '无', id FROM category WHERE name = '无'
            """);

            System.out.println("数据库和表初始化成功，已添加默认数据！");

        } catch (Exception e) {
            throw new RuntimeException("初始化数据库失败：" + e.getMessage());
        }
    }
}