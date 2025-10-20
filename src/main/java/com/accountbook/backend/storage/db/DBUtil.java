package com.accountbook.backend.storage.db;

import java.io.InputStream;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/*数据库工具类：读取配置文件并获取数据库连接 */
public class DBUtil {
    private static final Properties props = new Properties();

    // 新增：提供公共方法获取 props 对象
    public static Properties getProps() {
        return props;
    }

    // 静态块：加载配置文件（项目启动时执行一次）
    static {
        try (InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            props.load(in); // 加载 resources 目录下的配置文件
            // 加载 MySQL 8.0 驱动（可选，MySQL 8.0 驱动会自动注册，但显式加载更稳妥）
            Class.forName(props.getProperty("db.driver"));
        } catch (Exception e) {
            // 加载失败时抛出异常，提示用户检查配置
            throw new RuntimeException("数据库配置错误或驱动缺失：" + e.getMessage());
        }
    }

    // 获取数据库连接（供外部调用）
    // 修正后的 getConnection 方法（强制切换数据库）
    public static Connection getConnection() throws SQLException {
        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");

        // 获取连接
        Connection conn = DriverManager.getConnection(url, user, password);
        
        // 强制切换到 account_book 数据库
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("USE account_book");
        }
        
        return conn;
    }
}