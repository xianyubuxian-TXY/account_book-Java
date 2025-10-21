package com.accountbook.backend.storage.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/*数据库工具类：读取配置文件并获取数据库连接 */
public class DBUtil {
    private static final Properties props = new Properties();
    // 目标业务数据库名（常量定义，便于维护）
    private static final String TARGET_DB = "account_book";

    private DBUtil() {
        throw new AssertionError("工具类禁止实例化");
    }

    public static Properties getProps() {
        return props;
    }

    static {
        try (InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            props.load(in);
            Class.forName(props.getProperty("db.driver"));
        } catch (Exception e) {
            throw new RuntimeException("数据库配置错误或驱动缺失：" + e.getMessage());
        }
    }

        /**
     * 获取基础连接（仅连接服务器，不指定数据库）
     * 适用于数据库初始化（创建库、切换库等操作）
     */
    public static Connection getBaseConnection() throws SQLException {
        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");
        return DriverManager.getConnection(url, user, password);
    }

    // 获取基础连接（仅连接服务器，不指定数据库）
    public static Connection getAccountBookConnection() throws SQLException {
        // 先获取基础连接（连接服务器）
        Connection conn = getBaseConnection();
        // 强制切换到 account_book 数据库
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("USE " + TARGET_DB);
        }
        return conn;
    }

    
}