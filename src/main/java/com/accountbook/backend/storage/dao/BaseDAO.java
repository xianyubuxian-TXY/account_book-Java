package com.accountbook.backend.storage.dao;

import java.sql.Statement;
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
        return DBUtil.getAccountBookConnection();
    }

    /**
     * 通用插入操作
     * @param tableName 表名
     * @param fieldMap 字段-值映射（key:字段名，value:字段值）
     * @return 主键
     */
    public int insert(String tableName, Map<String, Object> fieldMap) {
        if (fieldMap.isEmpty()) {
            throw new IllegalArgumentException("插入字段不能为空");
        }
    
        // 拼接SQL
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
             PreparedStatement pstmt = conn.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS)) {
    
            // 设置参数
            int index = 1;
            for (Object value : fieldMap.values()) {
                setParameter(pstmt, index++, value);
            }
    
            // 执行插入
            pstmt.executeUpdate();
    
            // 获取生成的主键并返回
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // 正常返回主键（int类型）
                }
                // 如果未获取到主键，抛出异常（覆盖此路径，无需返回）
                throw new RuntimeException("未获取到生成的主键");
            }
    
        } catch (SQLException e) {
            // 异常情况下抛出运行时异常（覆盖此路径，无需返回）
            throw new RuntimeException("插入数据失败：" + e.getMessage(), e);
        }
        // 注意：由于上述代码中所有路径要么return，要么throw，因此无需额外return
    }

    /**
     * 通用更新操作
     * @param tableName 表名
     * @param fieldMap 要更新的字段-值映射：如<"amount",300.0>
     * @param condition 条件（如"id<? and amount>?"）——> ?：占位符
     * @param conditionParams 条件参数：?占位符的值
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
    
        sql.deleteCharAt(sql.length() - 1); // 删除末尾逗号
        sql.append(" WHERE ").append(condition);
    
        try (Connection conn = getConnection();
             // 移除 RETURN_GENERATED_KEYS 参数（UPDATE 不需要）
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            // 设置更新字段的参数
            int index = 1;
            for (Object value : fieldMap.values()) {
                setParameter(pstmt, index++, value);
            }
    
            // 设置条件参数
            for (Object param : conditionParams) {
                setParameter(pstmt, index++, param);
            }
    
            // 执行更新，返回受影响的行数（核心修正）
            return pstmt.executeUpdate();
    
        } catch (SQLException e) {
            throw new RuntimeException("更新数据失败：" + e.getMessage(), e);
        }
    }

    /**
     * 通用删除操作
     * @param tableName 表名
     * @param condition 条件（如"id<? and amount>?"）——> ?：占位符
     * @param conditionParams 条件参数：?占位符的值
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
    
            // 执行删除，返回受影响的行数（核心修正）
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