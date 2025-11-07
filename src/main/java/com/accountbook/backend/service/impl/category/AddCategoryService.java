package com.accountbook.backend.service.impl.category;

import com.accountbook.backend.common.exception.BusinessServiceException;
import com.accountbook.backend.service.BusinessService;
import com.accountbook.backend.storage.dao.CategoryDAO;
import com.accountbook.backend.storage.dao.factory.DAOFactory;
import com.accountbook.backend.storage.entity.Category;
import com.accountbook.proxy.request.category.CategoryAddParams;
import com.accountbook.proxy.response.category.CategorySingleResponse;

import java.util.Map;

/**
 * 新增分类服务：处理分类新增的数据库操作，返回代理层响应对象
 */
public class AddCategoryService implements BusinessService<CategoryAddParams, CategorySingleResponse> {

    // 获取分类DAO实例（通过工厂模式）
    private final CategoryDAO categoryDAO = DAOFactory.getCategoryDAO();

    @Override
    public CategorySingleResponse execute(CategoryAddParams params) throws Exception {
        System.out.println("执行新增分类服务");

        // 1. 转换参数为数据库字段映射（调用params的toMap方法）
        Map<String, Object> paramMap = params.toMap();
        if (paramMap.isEmpty() || paramMap.get("name") == null) {
            throw new BusinessServiceException("新增分类失败：分类名称不能为空");
        }

        // 2. 调用DAO插入数据，返回新增记录的主键ID
        int newCategoryId = categoryDAO.addCategory(paramMap);
        if (newCategoryId <= 0) { // 假设DAO返回<=0表示插入失败
            throw new BusinessServiceException("新增分类失败：数据库插入操作未生成有效ID");
        }

        // 3. 用新增的ID查询完整分类信息（确保数据一致性）
        Category newCategory = categoryDAO.queryCategoryById(newCategoryId);
        if (newCategory == null) {
            throw new BusinessServiceException("新增分类失败：插入后查询不到数据，ID=" + newCategoryId);
        }

        // 4. 转换为代理层响应对象并返回
        return CategorySingleResponse.fromCategory(newCategory);
    }
}