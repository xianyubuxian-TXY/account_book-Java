package com.accountbook.backend.storage.db;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* 通用SQL操作类（单例模式） */
public class DBSql {

    // 1. 私有静态实例（饿汉式：类加载时初始化，JVM保证线程安全）
    private static final DBSql INSTANCE = new DBSql();

    // 2. 私有构造方法（禁止外部实例化）
    private DBSql() {}

    // 3. 公共静态方法（唯一实例访问入口）
    public static DBSql getInstance() {
        return INSTANCE;
    }

    // ---------------------- 原有核心方法（保持不变） ----------------------

    // 获取数据库连接（复用DBUtil）
    protected Connection getConnection() throws SQLException {
        return DBUtil.getConnection();
    }

    /**
     * 通用插入操作
     */
    public int insert(String tableName, Map<String, Object> fieldMap) {
        // 原有逻辑不变...
        if (fieldMap.isEmpty()) {
            throw new IllegalArgumentException("插入字段不能为空");
        }

        StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + " (");
        StringBuilder placeholders = new StringBuilder(") VALUES (");

        for (String field : fieldMap.keySet()) {
            sql.append(field).append(",");
            placeholders.append("?,");
        }

        sql.deleteCharAt(sql.length() - 1);
        placeholders.deleteCharAt(placeholders.length() - 1);
        sql.append(placeholders).append(")");

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int index = 1;
            for (Object value : fieldMap.values()) {
                setParameter(pstmt, index++, value);
            }

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("插入数据失败：" + e.getMessage(), e);
        }
    }

    /**
     * 通用更新操作
     */
    public int update(String tableName, Map<String, Object> fieldMap, String condition, Object... conditionParams) {
        // 原有逻辑不变...
        if (fieldMap.isEmpty()) {
            throw new IllegalArgumentException("更新字段不能为空");
        }
        if (condition == null || condition.trim().isEmpty()) {
            throw new IllegalArgumentException("更新条件不能为空（防止全表更新）");
        }

        StringBuilder sql = new StringBuilder("UPDATE " + tableName + " SET ");
        for (String field : fieldMap.keySet()) {
            sql.append(field).append("=?,");
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(" WHERE ").append(condition);

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int index = 1;
            for (Object value : fieldMap.values()) {
                setParameter(pstmt, index++, value);
            }

            for (Object param : conditionParams) {
                setParameter(pstmt, index++, param);
            }

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("更新数据失败：" + e.getMessage(), e);
        }
    }

    /**
     * 通用删除操作
     */
    public int delete(String tableName, String condition, Object... conditionParams) {
        // 原有逻辑不变...
        if (condition == null || condition.trim().isEmpty()) {
            throw new IllegalArgumentException("删除条件不能为空（防止全表删除）");
        }

        String sql = "DELETE FROM " + tableName + " WHERE " + condition;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < conditionParams.length; i++) {
                setParameter(pstmt, i + 1, conditionParams[i]);
            }

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("删除数据失败：" + e.getMessage(), e);
        }
    }

    /**
     * 通用查询操作（单表）
     */
    public List<Map<String, Object>> query(String tableName, String fields, String condition, Object... conditionParams) {
        // 原有逻辑不变...
        String sql = "SELECT " + (fields == null ? "*" : fields) + " FROM " + tableName;
        if (condition != null && !condition.trim().isEmpty()) {
            sql += " WHERE " + condition;
        }

        List<Map<String, Object>> result = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            for (int i = 0; i < conditionParams.length; i++) {
                setParameter(pstmt, i + 1, conditionParams[i]);
            }

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new java.util.HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    row.put(columnName, rs.getObject(i));
                }
                result.add(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询数据失败：" + e.getMessage(), e);
        }

        return result;
    }

    /**
     * 自定义SQL查询（多表关联）
     */
    public List<Map<String, Object>> queryByCustomSql(String customSql, Object... params) {
        // 原有逻辑不变...
        List<Map<String, Object>> result = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(customSql);
             ResultSet rs = pstmt.executeQuery()) {

            for (int i = 0; i < params.length; i++) {
                setParameter(pstmt, i + 1, params[i]);
            }

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (rs.next()) {
                Map<String, Object> row = new java.util.HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                result.add(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException("自定义查询失败：" + e.getMessage(), e);
        }
        return result;
    }

    /**
     * 执行自定义更新SQL
     */
    public int executeCustomUpdate(String customSql, Object... params) {
        // 原有逻辑不变...
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(customSql)) {

            for (int i = 0; i < params.length; i++) {
                setParameter(pstmt, i + 1, params[i]);
            }

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("自定义更新失败：" + e.getMessage(), e);
        }
    }

    /**
     * 参数设置工具方法
     */
    private void setParameter(PreparedStatement pstmt, int index, Object value) throws SQLException {
        // 原有逻辑不变...
        if (value == null) {
            pstmt.setNull(index, Types.NULL);
        } else if (value instanceof String) {
            pstmt.setString(index, (String) value);
        } else if (value instanceof Integer) {
            pstmt.setInt(index, (Integer) value);
        } else if (value instanceof Double) {
            pstmt.setDouble(index, (Double) value);
        } else if (value instanceof BigDecimal) {
            pstmt.setBigDecimal(index, (BigDecimal) value);
        } else if (value instanceof Date) {
            pstmt.setTimestamp(index, new Timestamp(((Date) value).getTime()));
        } else if (value instanceof LocalDateTime) {
            pstmt.setTimestamp(index, Timestamp.valueOf((LocalDateTime) value));
        } else {
            pstmt.setString(index, value.toString());
        }
    }
}