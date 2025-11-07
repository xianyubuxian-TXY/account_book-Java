package com.accountbook.backend.factory;

import com.accountbook.backend.service.BusinessService;
import com.accountbook.backend.service.impl.bill.AddBillService;
import com.accountbook.backend.service.impl.bill.ChangeBillService;
import com.accountbook.backend.service.impl.bill.DeleteBillService;
import com.accountbook.backend.service.impl.bill.SearchBillService;
import com.accountbook.backend.service.impl.bill.StatisticBillService;
import com.accountbook.backend.service.impl.bill.VisualService;
import com.accountbook.backend.service.impl.budget.AddBudgetService;
import com.accountbook.backend.service.impl.budget.ChangeBudgetService;
import com.accountbook.backend.service.impl.budget.DeleteBudgetService;
import com.accountbook.backend.service.impl.budget.SearchBudgetService;
import com.accountbook.backend.service.impl.category.AddCategoryService;
import com.accountbook.backend.service.impl.category.ChangeCategoryService;
import com.accountbook.backend.service.impl.category.DeleteCategoryService;
import com.accountbook.backend.service.impl.category.SearchCategoryService;
import com.accountbook.backend.service.impl.specific_type.AddSpecificTypeService;
import com.accountbook.backend.service.impl.specific_type.ChangeSpecificTypeService;
import com.accountbook.backend.service.impl.specific_type.DeleteSpecificTypeService;
import com.accountbook.backend.service.impl.specific_type.SearchSpecificTypeService;
import com.accountbook.backend.storage.entity.Bill;
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

import java.util.List;

public class AccountBookServiceFactory implements BusinessFactory {

    /*----------账单相关------------ */
    @Override
    public BusinessService<BillAddParams,BillSingleResponse> createAddBillService() {
        return new AddBillService();
    }

    @Override
    public BusinessService<BillDeleteParams, BillDeleteResponse> createDeleteBillService() {
        return new DeleteBillService();
    }

    @Override
    public BusinessService<BillChangeParams, BillSingleResponse> createChangeBillService() {
        return new ChangeBillService();
    }

    @Override
    public BusinessService<BillSearchParams, BillListResponse> createSearchBillService() {
        return new SearchBillService();
    }

    @Override
    public BusinessService<Bill, List<Bill>> createStatisticBillService() {
        return new StatisticBillService();
    }

    @Override
    public BusinessService<String, Void> createVisualService() {
        return new VisualService();
    }

    /*----------大类相关------------ */
    @Override
    public BusinessService<CategoryAddParams,CategorySingleResponse> createAddCategoryService(){
        return new AddCategoryService();
    }

    @Override
    public BusinessService<CategoryDeleteParams,CategoryDeleteResponse> createDeleteCategoryService(){
        return new DeleteCategoryService();
    }

    @Override
    public BusinessService<CategoryChangeParams,CategorySingleResponse> createChangeCategoryService(){
        return new ChangeCategoryService();
    }

    @Override
    public BusinessService<CategorySearchParams,CategoryListResponse> createSearchCategoryService(){
        return new SearchCategoryService();
    }

    /*----------具体类型相关----------- */
    @Override
    public BusinessService<SpecificTypeAddParams,SpecificTypeSingleResponse> createAddSpecificTypeService(){
        return new AddSpecificTypeService();
    }

    @Override
    public BusinessService<SpecificTypeDeleteParams,SpecificTypeDeleteResponse> createDeleteSpecificTypeService(){
        return new DeleteSpecificTypeService();
    }

    @Override
    public BusinessService<SpecificTypeChangeParams,SpecificTypeSingleResponse> createChangeSpecificTypeService(){
        return new ChangeSpecificTypeService(); 
    }

    @Override
    public BusinessService<SpecificTypeSearchParams,SpecificTypeListResponse> createSearchSpecificTypeService(){
        return new SearchSpecificTypeService();
    }

    /*----------预算相关------------ */
    @Override
    public BusinessService<BudgetAddParams, BudgetSingleResponse> createAddBudgetService() {
        return new AddBudgetService();
    }

    @Override
    public BusinessService<BudgetDeleteParams, BudgetDeleteResponse> createDeleteBudgetService() {
        return new DeleteBudgetService();
    }

    @Override
    public BusinessService<BudgetChangeParams, BudgetSingleResponse> createChangeBudgetService() {
        return new ChangeBudgetService();
    }


    @Override
    public BusinessService<BudgetSearchParams, BudgetListResponse> createSearchBudgetService() {
        return new SearchBudgetService();
    }
}