package com.accountbook.backend.storage.dao;

import com.accountbook.backend.storage.entity.SpecificType;
import java.util.List;
import java.util.Map;

/**
 * 具体类型数据访问接口（对应specific_type表）
 */
public interface SpecificTypeDAO {

    /**
     * 新增具体类型
     * @param specificTypeMap 具体类型字段映射（key:数据库字段名，如"name"；value:对应值）
     * @return 新增具体类型的主键ID
     */
    int addSpecificType(Map<String, Object> specificTypeMap);

    /**
     * 根据条件更新具体类型
     * @param fieldMap 要更新的字段-值映射
     * @param condition 条件（如"id=?"）
     * @param params 条件参数
     * @return 影响行数
     */
    int updateSpecificType(Map<String, Object> fieldMap, String condition, Object... params);

    /**
     * 根据条件删除具体类型
     * @param condition 条件（如"id=?"）
     * @param params 条件参数
     * @return 影响行数
     */
    int deleteSpecificType(String condition, Object... params);

    /**
     * 单表查询具体类型（支持条件过滤）
     * @param fields 要查询的字段（如"id,name"）
     * @param condition 条件（如"category_id=?"）
     * @param params 条件参数
     * @return 结果列表（Map<字段名, 值>）
     */
    List<Map<String, Object>> querySpecificTypes(String fields, String condition, Object... params);

    /**
     * 以ID为条件查询具体类型
     * @param specificTypeId 具体类型主键ID
     * @return 具体类型实体（SpecificType）
     */
    SpecificType querySpecificTypeById(Integer specificTypeId);

    /**
     * 以ID为条件删除具体类型
     * @param specificTypeId 具体类型主键ID
     * @return 影响行数
     */
    int deleteSpecificTypeById(Integer specificTypeId);

    /**
     * 以ID为条件修改具体类型
     * @param specificTypeId 具体类型主键ID
     * @param fieldMap 要更新的字段-值映射
     * @return 影响行数
     */
    int updateSpecificTypeById(Integer specificTypeId, Map<String, Object> fieldMap);

    /**
     * 查询所有具体类型（按ID正序排列）
     * @return 所有具体类型的实体列表（按id ASC排序）
     */
    List<SpecificType> queryAllSpecificTypesOrderByIdAsc();

    /**
     * 根据大类ID查询具体类型（用于联动查询）
     * @param categoryId 大类ID
     * @return 该大类下的所有具体类型列表
     */
    List<SpecificType> querySpecificTypesByCategoryId(Integer categoryId);
}