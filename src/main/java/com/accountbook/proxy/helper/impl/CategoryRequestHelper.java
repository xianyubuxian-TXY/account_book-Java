package com.accountbook.proxy.helper.impl;

import com.accountbook.proxy.FrontendRequest;
import com.accountbook.proxy.FrontendRequest.RequestType;
import com.accountbook.proxy.helper.BaseRequestHelper;
import com.accountbook.proxy.request.category.CategoryAddParams;
import com.accountbook.proxy.request.category.CategoryChangeParams;
import com.accountbook.proxy.request.category.CategoryDeleteParams;
import com.accountbook.proxy.request.category.CategorySearchParams;
import com.accountbook.proxy.response.category.CategoryDeleteResponse;
import com.accountbook.proxy.response.category.CategoryListResponse;
import com.accountbook.proxy.response.category.CategorySingleResponse;

import java.util.List;

/**
 * 分类请求助手：封装大类相关的请求处理，与其他业务助手保持一致设计风格
 */
public class CategoryRequestHelper extends BaseRequestHelper {

    /**
     * 新增分类：返回新增的分类详情
     * 业务规则：分类名称不可重复，且长度限制在1-20字符
     */
    public CategorySingleResponse addCategory(String name) {
        // 基础非空校验
        validateParamNotNull(name, "分类名称不能为空");
        String trimmedName = name.trim();
        validateParamTrue(!trimmedName.isEmpty(), "分类名称不能为空白字符");
        
        // 格式与业务规则校验
        validateParamTrue(trimmedName.length() <= 20, 
                "分类名称过长（最大20字符，当前：" + trimmedName.length() + "）");
        
        // 唯一性校验：避免重复添加同名分类
        CategoryListResponse existing = searchCategory(trimmedName);
        if (existing.getItems() != null && !existing.getItems().isEmpty()) {
            throw new RuntimeException("分类名称'" + trimmedName + "'已存在，不可重复添加");
        }

        // 发送新增请求
        CategoryAddParams params = new CategoryAddParams(trimmedName);
        FrontendRequest<CategoryAddParams> request = new FrontendRequest<>(RequestType.ADD_CATEGORY, params);
        return parseResponse(sendRequest(request));
    }

    /**
     * 修改分类：返回修改后的分类详情（统一命名为update，与其他助手类保持一致）
     */
    public CategorySingleResponse updateCategory(Integer categoryId, String newName) {
        // 基础标识校验
        validateParamNotNull(categoryId, "分类ID不能为空");
        validateParamTrue(categoryId > 0, "分类ID必须为正整数（当前值：" + categoryId + "）");
        
        // 校验：至少修改一个有效字段
        boolean hasValidUpdate = (newName != null) && !newName.trim().isEmpty();
        validateParamTrue(hasValidUpdate, "新分类名称不能为空或空白字符");
        
        // 新名称格式校验
        String trimmedNewName = newName.trim();
        validateParamTrue(trimmedNewName.length() <= 20, 
                "新分类名称过长（最大20字符，当前：" + trimmedNewName.length() + "）");
        
        // 校验：新名称与原名称是否一致（避免无效修改）
        CategorySingleResponse original = searchCategoryById(categoryId);
        if (trimmedNewName.equals(original.getName())) {
            throw new RuntimeException("新名称与原名称一致，无需修改");
        }
        
        // 校验：新名称是否已被其他分类使用
        CategoryListResponse existing = searchCategory(trimmedNewName);
        if (existing.getItems() != null) {
            for (CategorySingleResponse cat : existing.getItems()) {
                if (!cat.getCategoryId().equals(categoryId)) { // 排除自身
                    throw new RuntimeException("分类名称'" + trimmedNewName + "'已被其他分类使用");
                }
            }
        }

        // 发送修改请求
        CategoryChangeParams params = new CategoryChangeParams(categoryId, trimmedNewName);
        FrontendRequest<CategoryChangeParams> request = new FrontendRequest<>(RequestType.CHANGE_CATEGORY, params);
        return parseResponse(sendRequest(request));
    }

    /**
     * 删除分类：返回被删除的分类ID信息
     * 业务规则：系统默认分类（ID=1）不可删除；存在关联数据时需先处理
     */
    public CategoryDeleteResponse deleteCategory(Integer categoryId) {
        // 基础标识校验
        validateParamNotNull(categoryId, "分类ID不能为空");
        validateParamTrue(categoryId > 0, "分类ID必须为正整数（当前值：" + categoryId + "）");
        
        // 系统保护：默认分类不可删除
        if (categoryId == 1) {
            throw new RuntimeException("系统默认分类（ID=1）不可删除");
        }

        // TODO：实际业务中需添加关联校验（示例）
        // 1. 检查是否有关联的具体类型
        // 2. 检查是否有关联的账单
        // 若存在关联数据，需提示："该分类存在XX条关联数据，请先迁移或删除关联数据"

        // 发送删除请求
        CategoryDeleteParams params = new CategoryDeleteParams(categoryId);
        FrontendRequest<CategoryDeleteParams> request = new FrontendRequest<>(RequestType.DELETE_CATEGORY, params);
        return parseResponse(sendRequest(request));
    }

    /**
     * 搜索分类（按名称关键字模糊查询）
     */
    public CategoryListResponse searchCategory(String nameKey) {
        // 构建查询参数（支持null，此时查询所有）
        CategorySearchParams params = new CategorySearchParams(nameKey);
        FrontendRequest<CategorySearchParams> request = new FrontendRequest<>(RequestType.SEARCH_CATEGORY, params);
        return parseResponse(sendRequest(request));
    }

    /**
     * 按ID查询分类详情
     */
    public CategorySingleResponse searchCategoryById(Integer categoryId) {
        validateParamNotNull(categoryId, "分类ID不能为空");
        validateParamTrue(categoryId > 0, "分类ID必须为正整数（当前值：" + categoryId + "）");

        CategorySearchParams params = new CategorySearchParams(categoryId);
        FrontendRequest<CategorySearchParams> request = new FrontendRequest<>(RequestType.SEARCH_CATEGORY, params);
        CategoryListResponse listResponse = parseResponse(sendRequest(request));

        List<CategorySingleResponse> categories = listResponse.getItems();
        if (categories == null || categories.isEmpty()) {
            throw new RuntimeException("未找到ID为" + categoryId + "的分类");
        }
        if (categories.size() > 1) {
            throw new RuntimeException("查询异常：ID为" + categoryId + "的分类存在" + categories.size() + "条记录");
        }

        return categories.get(0);
    }

    /**
     * 查询所有分类（按创建时间正序排列）
     */
    public CategoryListResponse searchAllCategories() {
        CategorySearchParams emptyParams = new CategorySearchParams();
        FrontendRequest<CategorySearchParams> request = new FrontendRequest<>(RequestType.SEARCH_CATEGORY, emptyParams);
        return parseResponse(sendRequest(request));
    }
}