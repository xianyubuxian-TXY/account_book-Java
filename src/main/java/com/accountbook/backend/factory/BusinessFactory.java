package com.accountbook.backend.factory;

import java.util.List;

import com.accountbook.backend.service.BusinessService;
import com.accountbook.backend.storage.entity.Bill;
import com.accountbook.proxy.request.bill.BillAddParams;
import com.accountbook.proxy.request.bill.BillChangeParams;
import com.accountbook.proxy.request.bill.BillDeleteParams;
import com.accountbook.proxy.request.bill.BillSearchParams;
import com.accountbook.proxy.request.budget.BudgetAddParams;
import com.accountbook.proxy.request.budget.BudgetChangeParams;
import com.accountbook.proxy.request.budget.BudgetDeleteParams;
import com.accountbook.proxy.request.budget.BudgetSearchParams;
import com.accountbook.proxy.request.category.CategoryAddParams;
import com.accountbook.proxy.request.category.CategoryChangeParams;
import com.accountbook.proxy.request.category.CategoryDeleteParams;
import com.accountbook.proxy.request.category.CategorySearchParams;
import com.accountbook.proxy.request.specific_type.SpecificTypeAddParams;
import com.accountbook.proxy.request.specific_type.SpecificTypeChangeParams;
import com.accountbook.proxy.request.specific_type.SpecificTypeDeleteParams;
import com.accountbook.proxy.request.specific_type.SpecificTypeSearchParams;
import com.accountbook.proxy.response.bill.BillListResponse;
import com.accountbook.proxy.response.bill.BillDeleteResponse;
import com.accountbook.proxy.response.bill.BillSingleResponse;
import com.accountbook.proxy.response.budget.BudgetDeleteResponse;
import com.accountbook.proxy.response.budget.BudgetListResponse;
import com.accountbook.proxy.response.budget.BudgetSingleResponse;
import com.accountbook.proxy.response.category.CategorySingleResponse;
import com.accountbook.proxy.response.category.CategoryDeleteResponse;
import com.accountbook.proxy.response.category.CategoryListResponse;
import com.accountbook.proxy.response.specific_type.SpecificTypeSingleResponse;
import com.accountbook.proxy.response.specific_type.SpecificTypeDeleteResponse;
import com.accountbook.proxy.response.specific_type.SpecificTypeListResponse;

// 抽象业务工厂（定义创建各类业务服务的方法）
public interface BusinessFactory {
    /*-----------------------------账单业务---------------------------------- */
    // 账目录入业务
    BusinessService<BillAddParams,BillSingleResponse> createAddBillService();
    
    // 账单删除业务
    BusinessService<BillDeleteParams, BillDeleteResponse> createDeleteBillService();
    
    // 账单修改业务
    BusinessService<BillChangeParams, BillSingleResponse> createChangeBillService();
    
    // 账单搜索业务
    BusinessService<BillSearchParams,BillListResponse> createSearchBillService();
    
    // 账单统计业务
    BusinessService<Bill, List<Bill>> createStatisticBillService();
    
    // 数据可视化业务
    BusinessService<String, Void> createVisualService();

    /*-------------------------------类型业务------------------------------- */
    //类型增加业务
    BusinessService<CategoryAddParams,CategorySingleResponse> createAddCategoryService();

    //类型删除业务
    BusinessService<CategoryDeleteParams,CategoryDeleteResponse> createDeleteCategoryService();

    //类型修改业务
    BusinessService<CategoryChangeParams,CategorySingleResponse> createChangeCategoryService();

    //类型查询业务
    BusinessService<CategorySearchParams,CategoryListResponse> createSearchCategoryService();

    /*-------------------------------具体类型业务------------------------ */
    //具体类型增加业务
    BusinessService<SpecificTypeAddParams,SpecificTypeSingleResponse> createAddSpecificTypeService();

    //具体类型删除业务
    BusinessService<SpecificTypeDeleteParams,SpecificTypeDeleteResponse> createDeleteSpecificTypeService();
    
    //具体类型修改业务
    BusinessService<SpecificTypeChangeParams,SpecificTypeSingleResponse> createChangeSpecificTypeService();

    //具体类型查询业务
    BusinessService<SpecificTypeSearchParams,SpecificTypeListResponse> createSearchSpecificTypeService();

    /*-------------------------------预算相关业务------------------------ */
    
    // 增加预算业务
    BusinessService<BudgetAddParams, BudgetSingleResponse> createAddBudgetService();
    
    // 删除预算业务
    BusinessService<BudgetDeleteParams, BudgetDeleteResponse> createDeleteBudgetService();
    
    // 修改预算业务
    BusinessService<BudgetChangeParams, BudgetSingleResponse> createChangeBudgetService();
    
    // 查找预算业务
    BusinessService<BudgetSearchParams, BudgetListResponse> createSearchBudgetService();
}
