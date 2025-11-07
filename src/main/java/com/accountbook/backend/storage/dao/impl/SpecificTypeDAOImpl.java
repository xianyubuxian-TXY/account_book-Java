package com.accountbook.backend.storage.dao.impl;

import com.accountbook.backend.common.exception.BusinessServiceException;
import com.accountbook.backend.common.util.SpecificTypeConvertUtils;
import com.accountbook.backend.storage.dao.BaseDAO;
import com.accountbook.backend.storage.dao.SpecificTypeDAO;
import com.accountbook.backend.storage.entity.SpecificType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 具体类型数据访问实现类（对应specific_type表）
 */
public class SpecificTypeDAOImpl extends BaseDAO implements SpecificTypeDAO {

    // 表名常量（与数据库表名一致）
    private static final String TABLE_SPECIFIC_TYPE = "specific_type";

    /**
     * 新增具体类型
     */
    @Override
    public int addSpecificType(Map<String, Object> specificTypeMap) {
        try {
            return super.insert(TABLE_SPECIFIC_TYPE, specificTypeMap);
        } catch (Exception e) {
            System.err.println("新增具体类型失败：" + e.getMessage());
            return -1;
        }
    }

    /**
     * 根据条件更新具体类型
     */
    @Override
    public int updateSpecificType(Map<String, Object> fieldMap, String condition, Object... params) {
        try {
            return super.update(TABLE_SPECIFIC_TYPE, fieldMap, condition, params);
        } catch (Exception e) {
            System.err.println("更新具体类型失败：" + e.getMessage());
            return -1;
        }
    }

    /**
     * 根据条件删除具体类型
     */
    @Override
    public int deleteSpecificType(String condition, Object... params) {
        try {
            return super.delete(TABLE_SPECIFIC_TYPE, condition, params);
        } catch (Exception e) {
            System.err.println("删除具体类型失败：" + e.getMessage());
            return -1;
        }
    }

    /**
     * 单表查询具体类型
     */
    @Override
    public List<Map<String, Object>> querySpecificTypes(String fields, String condition, Object... params) {
        try {
            return super.query(TABLE_SPECIFIC_TYPE, fields, condition, params);
        } catch (Exception e) {
            System.err.println("查询具体类型失败：" + e.getMessage());
            return null;
        }
    }

    /**
     * 根据ID查询具体类型实体
     */
    @Override
    public SpecificType querySpecificTypeById(Integer specificTypeId) {
        // 1. 校验ID合法性
        validateSpecificTypeId(specificTypeId);

        try {
            // 2. 调用父类查询方法：查询所有字段，条件为id=?
            List<Map<String, Object>> mapList = super.query(
                    TABLE_SPECIFIC_TYPE,
                    "*",
                    "id = ?",
                    specificTypeId
            );

            // 3. 处理查询结果（无数据则抛异常）
            if (mapList == null || mapList.isEmpty()) {
                throw new BusinessServiceException("查询失败：未找到ID为" + specificTypeId + "的具体类型");
            }

            // 4. 转换Map为SpecificType实体
            return SpecificTypeConvertUtils.mapToSpecificType(mapList.get(0));

        } catch (BusinessServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessServiceException("查询ID为" + specificTypeId + "的具体类型时发生系统异常：" + e.getMessage());
        }
    }

    /**
     * 根据ID删除具体类型
     */
    @Override
    public int deleteSpecificTypeById(Integer specificTypeId) {
        // 1. 校验ID合法性
        validateSpecificTypeId(specificTypeId);

        try {
            // 2. 调用通用删除方法，条件为"id=?"
            return super.delete(TABLE_SPECIFIC_TYPE, "id = ?", specificTypeId);
        } catch (Exception e) {
            System.err.println("删除ID为" + specificTypeId + "的具体类型失败：" + e.getMessage());
            return -1;
        }
    }

    /**
     * 根据ID更新具体类型
     */
    @Override
    public int updateSpecificTypeById(Integer specificTypeId, Map<String, Object> fieldMap) {
        // 1. 校验ID合法性
        validateSpecificTypeId(specificTypeId);

        // 2. 校验更新字段非空
        if (fieldMap == null || fieldMap.isEmpty()) {
            System.err.println("更新具体类型失败：至少需要指定一个待修改的字段");
            return -1;
        }

        try {
            // 3. 调用通用更新方法，条件为"id=?"
            return super.update(TABLE_SPECIFIC_TYPE, fieldMap, "id = ?", specificTypeId);
        } catch (Exception e) {
            System.err.println("更新ID为" + specificTypeId + "的具体类型失败：" + e.getMessage());
            return -1;
        }
    }

    /**
     * 查询所有具体类型（按ID正序排列）
     */
    @Override
    public List<SpecificType> queryAllSpecificTypesOrderByIdAsc() {
        try {
            String sql = "SELECT * FROM " + TABLE_SPECIFIC_TYPE + " ORDER BY id ASC";
            List<Map<String, Object>> mapList = super.queryByCustomSql(sql);

            List<SpecificType> specificTypeList = new ArrayList<>();
            if (mapList != null && !mapList.isEmpty()) {
                for (Map<String, Object> map : mapList) {
                    specificTypeList.add(SpecificTypeConvertUtils.mapToSpecificType(map));
                }
            }

            return specificTypeList;

        } catch (Exception e) {
            throw new BusinessServiceException("查询所有具体类型（按ID排序）失败：" + e.getMessage());
        }
    }

    /**
     * 根据大类ID查询具体类型（用于联动）
     */
    @Override
    public List<SpecificType> querySpecificTypesByCategoryId(Integer categoryId) {
        // 1. 校验大类ID合法性
        if (categoryId == null || categoryId <= 0) {
            throw new BusinessServiceException("大类ID非法：" + categoryId + "，必须为正整数");
        }

        try {
            // 2. 按category_id查询，按具体类型ID正序排列
            List<Map<String, Object>> mapList = super.query(
                    TABLE_SPECIFIC_TYPE,
                    "*",
                    "category_id = ? ORDER BY id ASC",
                    categoryId
            );

            // 3. 转换为实体列表
            return SpecificTypeConvertUtils.mapListToSpecificTypeList(mapList);

        } catch (Exception e) {
            throw new BusinessServiceException("查询大类ID为" + categoryId + "的具体类型失败：" + e.getMessage());
        }
    }

    /**
     * 辅助方法：校验具体类型ID合法性
     */
    private void validateSpecificTypeId(Integer specificTypeId) {
        if (specificTypeId == null) {
            throw new BusinessServiceException("具体类型ID不能为空");
        }
        if (specificTypeId <= 0) {
            throw new BusinessServiceException("具体类型ID必须为正整数（当前值：" + specificTypeId + "）");
        }
    }
}