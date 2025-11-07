package com.accountbook.backend.service.impl.category;

import com.accountbook.backend.common.exception.BusinessServiceException;
import com.accountbook.backend.service.BusinessService;
import com.accountbook.backend.storage.dao.CategoryDAO;
import com.accountbook.backend.storage.dao.factory.DAOFactory;
import com.accountbook.backend.storage.entity.Category;
import com.accountbook.proxy.request.category.CategoryChangeParams;
import com.accountbook.proxy.response.category.CategorySingleResponse;

import java.util.Map;

/**
 * 分类修改业务实现类（参考ChangeBillService设计风格）
 */
public class ChangeCategoryService implements BusinessService<CategoryChangeParams, CategorySingleResponse> {

    // 注入分类DAO（通过工厂获取，与账单服务保持一致）
    private final CategoryDAO categoryDAO = DAOFactory.getCategoryDAO();

    @Override
    public CategorySingleResponse execute(CategoryChangeParams params) throws Exception {
        System.out.println("执行分类修改业务");

        // 1. 获取要修改的分类ID和更新字段Map
        Integer categoryId = params.getCategoryId();
        Map<String, Object> updateMap = params.toMap(); // 仅包含非空的修改字段（如name）

        // 2. 调用DAO更新分类信息，获取影响行数
        int affectRows = categoryDAO.updateCategoryById(categoryId, updateMap);

        // 3. 校验更新结果（-1=更新异常，0=未找到对应分类）
        if (affectRows == -1 || affectRows == 0) {
            throw new BusinessServiceException("修改分类失败：ID=" + categoryId + "，可能不存在或更新异常");
        }

        // 4. 查询更新后的分类信息（确保返回最新数据）
        Category updatedCategory = categoryDAO.queryCategoryById(categoryId);

        // 5. 转换为响应对象并返回
        return CategorySingleResponse.fromCategory(updatedCategory);
    }
}