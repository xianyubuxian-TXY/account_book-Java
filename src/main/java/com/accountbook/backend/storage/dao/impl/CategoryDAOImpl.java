package com.accountbook.backend.storage.dao.impl;

import com.accountbook.backend.common.exception.BusinessServiceException;
import com.accountbook.backend.common.util.CategoryConvertUtils;
import com.accountbook.backend.storage.dao.BaseDAO;
import com.accountbook.backend.storage.dao.CategoryDAO;
import com.accountbook.backend.storage.entity.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CategoryDAOImpl extends BaseDAO implements CategoryDAO {

    private static final String TABLE_CATEGORY = "category";

    @Override
    public int addCategory(Map<String, Object> categoryMap) {
        try {
            return super.insert(TABLE_CATEGORY, categoryMap);
        } catch (Exception e) {
            System.err.println("新增分类失败：" + e.getMessage());
            return -1;
        }
    }

    @Override
    public int updateCategory(Map<String, Object> fieldMap, String condition, Object... params) {
        try {
            return super.update(TABLE_CATEGORY, fieldMap, condition, params);
        } catch (Exception e) {
            System.err.println("更新分类失败：" + e.getMessage());
            return -1;
        }
    }

    @Override
    public int deleteCategory(String condition, Object... params) {
        try {
            return super.delete(TABLE_CATEGORY, condition, params);
        } catch (Exception e) {
            System.err.println("删除分类失败：" + e.getMessage());
            return -1;
        }
    }

    @Override
    public List<Map<String, Object>> queryCategories(String fields, String condition, Object... params) {
        try {
            return super.query(TABLE_CATEGORY, fields, condition, params);
        } catch (Exception e) {
            System.err.println("查询分类失败：" + e.getMessage());
            return null;
        }
    }

    @Override
    public Category queryCategoryById(Integer categoryId) {
        validateCategoryId(categoryId);

        try {
            List<Map<String, Object>> mapList = super.query(
                    TABLE_CATEGORY,
                    "*",
                    "id = ?",
                    categoryId
            );

            if (mapList == null || mapList.isEmpty()) {
                throw new BusinessServiceException("查询失败：未找到ID为" + categoryId + "的分类");
            }

            return CategoryConvertUtils.mapToCategory(mapList.get(0));

        } catch (BusinessServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessServiceException("查询ID为" + categoryId + "的分类时发生系统异常：" + e.getMessage());
        }
    }

    @Override
    public int deleteCategoryById(Integer categoryId) {
        validateCategoryId(categoryId);

        try {
            return super.delete(TABLE_CATEGORY, "id = ?", categoryId);
        } catch (Exception e) {
            System.err.println("删除ID为" + categoryId + "的分类失败：" + e.getMessage());
            return -1;
        }
    }

    @Override
    public int updateCategoryById(Integer categoryId, Map<String, Object> fieldMap) {
        validateCategoryId(categoryId);

        if (fieldMap == null || fieldMap.isEmpty()) {
            System.err.println("更新分类失败：至少需要指定一个待修改的字段");
            return -1;
        }

        try {
            return super.update(TABLE_CATEGORY, fieldMap, "id = ?", categoryId);
        } catch (Exception e) {
            System.err.println("更新ID为" + categoryId + "的分类失败：" + e.getMessage());
            return -1;
        }
    }

    @Override
    public List<Category> queryAllCategoriesOrderByIdAsc() {
        try {
            String sql = "SELECT * FROM " + TABLE_CATEGORY + " ORDER BY id ASC";
            List<Map<String, Object>> mapList = super.queryByCustomSql(sql);

            List<Category> categoryList = new ArrayList<>();
            if (mapList != null && !mapList.isEmpty()) {
                for (Map<String, Object> map : mapList) {
                    categoryList.add(CategoryConvertUtils.mapToCategory(map));
                }
            }

            return categoryList;

        } catch (Exception e) {
            throw new BusinessServiceException("查询所有分类（按ID排序）失败：" + e.getMessage());
        }
    }

    private void validateCategoryId(Integer categoryId) {
        if (categoryId == null) {
            throw new BusinessServiceException("分类ID不能为空");
        }
        if (categoryId <= 0) {
            throw new BusinessServiceException("分类ID必须为正整数（当前值：" + categoryId + "）");
        }
    }
}