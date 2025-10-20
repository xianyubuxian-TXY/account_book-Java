package com.accountbook.backend.storage.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.accountbook.backend.storage.db.DBUtil;

/**
 * 基础数据访问类（所有DAO的父类）
 * 封装通用CRUD操作，仅处理数据访问，不包含业务逻辑
 */
public abstract class BaseDAO {

    // 获取数据库连接（复用DBUtil）
    protected Connection getConnection() throws SQLException {
        return DBUtil.getConnection();
    }

    /**
     * 通用插入操作
     * @param tableName 表名
     * @param fieldMap 字段-值映射（key:字段名，value:字段值）
     * @return 影响行数（1=成功，0=失败）
     */
    public int insert(String tableName, Map<String, Object> fieldMap) {
        if (fieldMap.isEmpty()) {
            throw new IllegalArgumentException("插入字段不能为空");
        }

        // 拼接SQL：INSERT INTO 表名 (字段1,字段2...) VALUES (?,?...)
        StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + " (");
        StringBuilder placeholders = new StringBuilder(") VALUES (");

        for (String field : fieldMap.keySet()) {
            sql.append(field).append(",");
            placeholders.append("?,");
        }

        // 移除最后一个逗号
        sql.deleteCharAt(sql.length() - 1);
        placeholders.deleteCharAt(placeholders.length() - 1);
        sql.append(placeholders).append(")");

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            // 设置参数
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
     * @param tableName 表名
     * @param fieldMap 要更新的字段-值映射
     * @param condition 条件（如"id=?"）
     * @param conditionParams 条件参数
     * @return 影响行数
     */
    public int update(String tableName, Map<String, Object> fieldMap, String condition, Object... conditionParams) {
        if (fieldMap.isEmpty()) {
            throw new IllegalArgumentException("更新字段不能为空");
        }
        if (condition == null || condition.trim().isEmpty()) {
            throw new IllegalArgumentException("更新条件不能为空（防止全表更新）");
        }

        // 拼接SQL：UPDATE 表名 SET 字段1=?,字段2=?... WHERE 条件
        StringBuilder sql = new StringBuilder("UPDATE " + tableName + " SET ");
        for (String field : fieldMap.keySet()) {
            sql.append(field).append("=?,");
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(" WHERE ").append(condition);

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            // 设置更新字段参数
            int index = 1;
            for (Object value : fieldMap.values()) {
                setParameter(pstmt, index++, value);
            }

            // 设置条件参数
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
     * @param tableName 表名
     * @param condition 条件（如"id=?"）
     * @param conditionParams 条件参数
     * @return 影响行数
     */
    public int delete(String tableName, String condition, Object... conditionParams) {
        if (condition == null || condition.trim().isEmpty()) {
            throw new IllegalArgumentException("删除条件不能为空（防止全表删除）");
        }

        String sql = "DELETE FROM " + tableName + " WHERE " + condition;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 设置条件参数
            for (int i = 0; i < conditionParams.length; i++) {
                setParameter(pstmt, i + 1, conditionParams[i]);
            }

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("删除数据失败：" + e.getMessage(), e);
        }
    }

    /**
     * 通用单表查询操作
     * @param tableName 表名
     * @param fields 要查询的字段（如"id,name"，*表示所有字段）
     * @param condition 条件（如"id=?"，可为null表示查询所有）
     * @param conditionParams 条件参数
     * @return 结果列表（Map<字段名, 值>）
     */
    public List<Map<String, Object>> query(String tableName, String fields, String condition, Object... conditionParams) {
        String sql = "SELECT " + (fields == null ? "*" : fields) + " FROM " + tableName;
        if (condition != null && !condition.trim().isEmpty()) {
            sql += " WHERE " + condition;
        }

        List<Map<String, Object>> result = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 修复点：先设置参数，再执行查询（原代码顺序颠倒）
            // 设置条件参数
            for (int i = 0; i < conditionParams.length; i++) {
                setParameter(pstmt, i + 1, conditionParams[i]);
            }

            // 执行查询（此时参数已设置）
            try (ResultSet rs = pstmt.executeQuery()) {
                // 封装结果集
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(metaData.getColumnName(i), rs.getObject(i));
                    }
                    result.add(row);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询数据失败：" + e.getMessage(), e);
        }

        return result;
    }

    /**
     * 执行自定义SQL查询（支持多表关联）
     * @param customSql 自定义查询SQL
     * @param params SQL参数
     * @return 结果列表（Map<字段名, 值>）
     */
    public List<Map<String, Object>> queryByCustomSql(String customSql, Object... params) {
        List<Map<String, Object>> result = new ArrayList<>();
    
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(customSql)) {
    
            // 设置参数
            for (int i = 0; i < params.length; i++) {
                setParameter(pstmt, i + 1, params[i]);
            }
    
            try (ResultSet rs = pstmt.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        // 关键修复：使用getColumnLabel()获取别名，而非getColumnName()
                        String columnKey = metaData.getColumnLabel(i); 
                        row.put(columnKey, rs.getObject(i));
                    }
                    result.add(row);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("自定义查询失败：" + e.getMessage(), e);
        }
    
        return result;
    }

    /**
     * 执行自定义更新SQL（如INSERT IGNORE、CREATE等）
     * @param customSql 自定义SQL
     * @param params SQL参数
     * @return 影响行数
     */
    public int executeCustomUpdate(String customSql, Object... params) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(customSql)) {

            // 设置参数
            for (int i = 0; i < params.length; i++) {
                setParameter(pstmt, i + 1, params[i]);
            }

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("自定义更新失败：" + e.getMessage(), e);
        }
    }

    /**
     * 设置SQL参数（根据值类型自动适配）
     */
    protected void setParameter(PreparedStatement pstmt, int index, Object value) throws SQLException {
        if (value == null) {
            pstmt.setNull(index, java.sql.Types.NULL);
        } else if (value instanceof String) {
            pstmt.setString(index, (String) value);
        } else if (value instanceof Integer) {
            pstmt.setInt(index, (Integer) value);
        } else if (value instanceof Double) {
            pstmt.setDouble(index, (Double) value);
        } else if (value instanceof java.math.BigDecimal) {
            pstmt.setBigDecimal(index, (java.math.BigDecimal) value);
        } else if (value instanceof java.sql.Date) {
            pstmt.setDate(index, (java.sql.Date) value);
        } else if (value instanceof java.util.Date) {
            pstmt.setTimestamp(index, new java.sql.Timestamp(((java.util.Date) value).getTime()));
        } else if (value instanceof java.time.LocalDateTime) {
            pstmt.setTimestamp(index, java.sql.Timestamp.valueOf((java.time.LocalDateTime) value));
        } else {
            // 其他类型默认转为字符串
            pstmt.setString(index, value.toString());
        }
    }
}