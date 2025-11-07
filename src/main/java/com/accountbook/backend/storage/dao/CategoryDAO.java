package com.accountbook.backend.storage.dao;

import com.accountbook.backend.storage.entity.Category;
import java.util.List;
import java.util.Map;

/**
 * 分类数据访问接口（对应category表）
 * 定义分类的CRUD及专用查询方法
 */
public interface CategoryDAO {

    /**
     * 新增分类
     * @param categoryMap 分类字段映射（key:数据库字段名，如"name"；value:对应值）
     * @return 新增分类的主键ID
     */
    int addCategory(Map<String, Object> categoryMap);

    /**
     * 根据条件更新分类
     * @param fieldMap 要更新的字段-值映射
     * @param condition 条件（如"id=?"）
     * @param params 条件参数
     * @return 影响行数
     */
    int updateCategory(Map<String, Object> fieldMap, String condition, Object... params);

    /**
     * 根据条件删除分类
     * @param condition 条件（如"id=?"）
     * @param params 条件参数
     * @return 影响行数
     */
    int deleteCategory(String condition, Object... params);

    /**
     * 单表查询分类（支持条件过滤）
     * @param fields 要查询的字段（如"id,name"）
     * @param condition 条件（如"id>?"）
     * @param params 条件参数
     * @return 结果列表（Map<字段名, 值>）
     */
    List<Map<String, Object>> queryCategories(String fields, String condition, Object... params);

    /**
     * 以ID为条件查询分类
     * @param categoryId 分类主键ID
     * @return 分类实体（Category）
     */
    Category queryCategoryById(Integer categoryId);

    /**
     * 以ID为条件删除分类
     * @param categoryId 分类主键ID
     * @return 影响行数
     */
    int deleteCategoryById(Integer categoryId);

    /**
     * 以ID为条件修改分类
     * @param categoryId 分类主键ID
     * @param fieldMap 要更新的字段-值映射
     * @return 影响行数
     */
    int updateCategoryById(Integer categoryId, Map<String, Object> fieldMap);

    /**
     * 查询所有分类（按ID正序排列）
     * @return 所有分类的实体列表（按id ASC排序）
     */
    List<Category> queryAllCategoriesOrderByIdAsc();
}