package com.accountbook.backend.service.impl.category;

import com.accountbook.backend.common.exception.BusinessServiceException;
import com.accountbook.backend.service.BusinessService;
import com.accountbook.backend.storage.dao.CategoryDAO;
import com.accountbook.backend.storage.dao.factory.DAOFactory;
import com.accountbook.proxy.request.category.CategoryDeleteParams;
import com.accountbook.proxy.response.category.CategoryDeleteResponse;

/**
 * 删除分类服务：处理分类删除的数据库操作，返回代理层响应对象
 */
public class DeleteCategoryService implements BusinessService<CategoryDeleteParams, CategoryDeleteResponse> {

    // 获取分类DAO实例（通过工厂模式）
    private final CategoryDAO categoryDAO = DAOFactory.getCategoryDAO();

    @Override
    public CategoryDeleteResponse execute(CategoryDeleteParams params) throws Exception {
        System.out.println("执行删除分类服务");

        // 1. 校验参数（分类ID）
        Integer categoryId = params.getId();
        if (categoryId == null || categoryId <= 0) {
            throw new BusinessServiceException("删除分类失败：无效的分类ID=" + categoryId);
        }

        // 2. 调用DAO删除数据，返回受影响的行数（0表示无此记录，1表示删除成功）
        int deleteRows = categoryDAO.deleteCategoryById(categoryId);
        if (deleteRows == 0) {
            throw new BusinessServiceException("删除分类失败：未找到ID=" + categoryId + "的分类");
        }
        if (deleteRows < 0) { // 假设DAO返回<0表示系统异常
            throw new BusinessServiceException("删除分类失败：数据库操作异常");
        }

        // 3. 返回包含被删除ID的响应对象
        return new CategoryDeleteResponse(categoryId);
    }
}