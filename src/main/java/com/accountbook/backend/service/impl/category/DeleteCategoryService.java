package com.accountbook.backend.service.impl.category;

import com.accountbook.backend.common.exception.BusinessServiceException;
import com.accountbook.backend.service.BusinessService;
import com.accountbook.backend.storage.dao.*;
import com.accountbook.backend.storage.dao.factory.DAOFactory;
import com.accountbook.backend.storage.entity.*;

import com.accountbook.proxy.request.category.CategoryDeleteParams;
import com.accountbook.proxy.response.category.CategoryDeleteResponse;

import java.math.BigDecimal;
import java.util.*;

/**
 * 删除分类服务（带账单与预算迁移）
 */
public class DeleteCategoryService implements BusinessService<CategoryDeleteParams, CategoryDeleteResponse> {

    private final CategoryDAO categoryDAO = DAOFactory.getCategoryDAO();
    private final BillDAO billDAO = DAOFactory.getBillDAO();
    private final SpecificTypeDAO specificTypeDAO = DAOFactory.getSpecificTypeDAO();
    private final BudgetDAO budgetDAO = DAOFactory.getBudgetDAO();

    private static final int SYSTEM_DEFAULT_CATEGORY_ID = 1;
    private static final int SYSTEM_DEFAULT_SPECIFIC_ID = 1; // ✅ 固定“无”类型ID
    private static final String DEFAULT_SPEC_NAME = "无";

    @Override
    public CategoryDeleteResponse execute(CategoryDeleteParams params) throws Exception {
        System.out.println("执行删除分类服务（带账单与预算迁移）");

        // 1) 校验
        Integer categoryId = params.getId();
        if (categoryId == null || categoryId <= 0)
            throw new BusinessServiceException("删除分类失败：无效的分类ID=" + categoryId);
        if (categoryId.equals(SYSTEM_DEFAULT_CATEGORY_ID))
            throw new BusinessServiceException("删除分类失败：系统默认分类不可删除");

        Category target = categoryDAO.queryCategoryById(categoryId);
        if (target == null)
            throw new BusinessServiceException("删除分类失败：未找到ID=" + categoryId + "的分类");

        // 2) 获取系统默认分类与默认具体类型
        Category defaultCategory = categoryDAO.queryCategoryById(SYSTEM_DEFAULT_CATEGORY_ID);
        if (defaultCategory == null)
            throw new BusinessServiceException("删除分类失败：系统默认分类(ID=1)不存在");

        SpecificType defaultSpec = specificTypeDAO.querySpecificTypeById(SYSTEM_DEFAULT_SPECIFIC_ID);
        if (defaultSpec == null || !DEFAULT_SPEC_NAME.equals(defaultSpec.getName())) {
            throw new BusinessServiceException("删除分类失败：系统默认具体类型(ID=1,“无”)丢失，请检查数据库初始化");
        }

        // 3) 迁移账单
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("categoryId", categoryId);

        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("categoryId", SYSTEM_DEFAULT_CATEGORY_ID);
        updateMap.put("specificTypeId", SYSTEM_DEFAULT_SPECIFIC_ID);

        int movedBills = billDAO.updateBillsByCondition(queryMap, updateMap);
        if (movedBills < 0)
            throw new BusinessServiceException("删除分类失败：迁移账单时发生异常");

        // 4) 迁移/合并预算
        List<Budget> budgetsOfCategory = budgetDAO.queryBudgetsByCategoryId(categoryId);
        if (budgetsOfCategory != null) {
            for (Budget b : budgetsOfCategory) {
                String month = b.getMonth();
                List<Map<String, Object>> existingList =
                        budgetDAO.queryBudgets("*", "category_id = ? AND month = ?", SYSTEM_DEFAULT_CATEGORY_ID, month);

                if (existingList != null && !existingList.isEmpty()) {
                    Map<String, Object> exist = existingList.get(0);
                    Integer existId = (Integer) exist.get("id");

                    BigDecimal existTotal = toDecimal(exist.get("total_budget"));
                    BigDecimal existSpent = toDecimal(exist.get("spent"));
                    BigDecimal addTotal = nz(b.getTotalBudget());
                    BigDecimal addSpent = nz(b.getSpent());
                    BigDecimal newTotal = existTotal.add(addTotal);
                    BigDecimal newSpent = existSpent.add(addSpent);
                    BigDecimal newRemaining = newTotal.subtract(newSpent);

                    Map<String, Object> upd = new HashMap<>();
                    upd.put("total_budget", newTotal);
                    upd.put("spent", newSpent);
                    upd.put("remaining", newRemaining);

                    if (budgetDAO.updateBudgetById(existId, upd) <= 0)
                        throw new BusinessServiceException("删除分类失败：合并预算失败（" + month + "）");

                    budgetDAO.deleteBudgetById(b.getId());
                } else {
                    Map<String, Object> upd = new HashMap<>();
                    upd.put("category_id", SYSTEM_DEFAULT_CATEGORY_ID);
                    if (budgetDAO.updateBudgetById(b.getId(), upd) <= 0)
                        throw new BusinessServiceException("删除分类失败：迁移预算归属失败");
                }
            }
        }

        // 5) 删除分类
        int deleteRows = categoryDAO.deleteCategoryById(categoryId);
        if (deleteRows <= 0)
            throw new BusinessServiceException("删除分类失败：分类不存在或数据库异常");

        return new CategoryDeleteResponse(categoryId);
    }

    private static BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private static BigDecimal toDecimal(Object o) {
        if (o == null) return BigDecimal.ZERO;
        if (o instanceof BigDecimal) return (BigDecimal) o;
        if (o instanceof Number) return new BigDecimal(((Number) o).toString());
        return new BigDecimal(String.valueOf(o));
    }
}
