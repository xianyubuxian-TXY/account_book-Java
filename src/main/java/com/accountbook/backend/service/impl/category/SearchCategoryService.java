package com.accountbook.backend.service.impl.category;

import com.accountbook.backend.common.util.CategoryConvertUtils; // 假设存在分类转换工具类
import com.accountbook.backend.service.BusinessService;
import com.accountbook.backend.storage.dao.CategoryDAO;
import com.accountbook.backend.storage.dao.factory.DAOFactory;
import com.accountbook.backend.storage.entity.Category;
import com.accountbook.proxy.request.category.CategorySearchParams;
import com.accountbook.proxy.response.category.CategoryListResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 分类查询业务实现类（参考SearchBillService设计风格）
 * 注意：类名修正为SearchCategoryService（原拼写"SearchCagetoryService"为笔误）
 */
public class SearchCategoryService implements BusinessService<CategorySearchParams, CategoryListResponse> {

    // 注入分类DAO（通过工厂获取，与账单查询服务保持一致）
    private final CategoryDAO categoryDAO = DAOFactory.getCategoryDAO();

    @Override
    public CategoryListResponse execute(CategorySearchParams params) throws Exception {
        System.out.println("执行分类查询业务");

        // 处理参数为空的情况（直接查询所有并按ID正序）
        if (params == null || isAllParamsNull(params)) {
            System.out.println("无有效查询条件，默认返回所有分类（按ID由小到大）");
            List<Category> allCategories = categoryDAO.queryAllCategoriesOrderByIdAsc();
            return CategoryListResponse.fromCategoryList(allCategories);
        }

        // 1. 将查询参数转为 Map
        Map<String, Object> paramMap = params.toMap();
        if (paramMap.isEmpty()) {
            // 所有参数均为null（但params不为null），默认返回所有分类
            System.out.println("所有查询条件均为null，默认返回所有分类（按ID由小到大）");
            List<Category> allCategories = categoryDAO.queryAllCategoriesOrderByIdAsc();
            return CategoryListResponse.fromCategoryList(allCategories);
        }

        // 2. 动态拼接 SQL 条件和参数列表
        StringBuilder condition = new StringBuilder();
        List<Object> sqlParams = new ArrayList<>();

        // 遍历 Map 生成 AND 连接的条件（分类查询仅涉及ID精确查询和名称模糊查询）
        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value == null) {
                continue; // 跳过 null 值条件
            }

            // 根据字段类型拼接条件（对应CategorySearchParams的toMap字段）
            switch (key) {
                case "id":
                    // 分类ID：精确匹配
                    condition.append("id = ? AND ");
                    sqlParams.add(value);
                    break;
                case "name_like":
                    // 名称关键字：模糊查询（前后加%）
                    condition.append("name LIKE CONCAT('%', ?, '%') AND ");
                    sqlParams.add(value);
                    break;
                default:
                    // 忽略未知字段
                    break;
            }
        }

        // 3. 处理条件字符串（移除末尾多余的 "AND "）
        String finalCondition = "";
        if (condition.length() > 0) {
            finalCondition = condition.substring(0, condition.length() - 4);
        } else {
            // 理论上不会走到这里，因paramMap非空且已过滤null
            System.out.println("条件处理后为空，默认返回所有分类（按ID由小到大）");
            List<Category> allCategories = categoryDAO.queryAllCategoriesOrderByIdAsc();
            return CategoryListResponse.fromCategoryList(allCategories);
        }

        // 4. 调用 DAO 查询完整字段（字段参数传 "*"）
        List<Map<String, Object>> mapList = categoryDAO.queryCategories("*", finalCondition, sqlParams.toArray());

        // 5. 将 Map 列表转为 Category 实体列表（复用转换工具类）
        List<Category> categoryList = CategoryConvertUtils.mapListToCategoryList(mapList);

        // 6. 将实体列表转为 CategoryListResponse
        return CategoryListResponse.fromCategoryList(categoryList);
    }

    /**
     * 判断查询参数是否全为null（辅助方法）
     * @param params 分类查询参数对象
     * @return true=所有参数都为null；false=存在非null参数
     */
    private boolean isAllParamsNull(CategorySearchParams params) {
        // 检查分类查询的核心字段是否全为null
        return params.getCategoryId() == null &&
               (params.getNameKey() == null || params.getNameKey().trim().isEmpty());
    }
}