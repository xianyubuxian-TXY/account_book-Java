package com.accountbook.backend.factory;

import com.accountbook.backend.service.BusinessService;
import com.accountbook.backend.service.impl.AddBillService;
import com.accountbook.backend.service.impl.AddBudgetService;
import com.accountbook.backend.service.impl.ChangeBillService;
import com.accountbook.backend.service.impl.ChangeBudgetService; // 需确保有此类
import com.accountbook.backend.service.impl.DeleteBillService;
import com.accountbook.backend.service.impl.DeleteBudgetService;
import com.accountbook.backend.service.impl.SearchBillService;
import com.accountbook.backend.service.impl.SearchBudgetService; // 需确保有此类
import com.accountbook.backend.service.impl.StatisticBillService;
import com.accountbook.backend.service.impl.VisualService;
import com.accountbook.backend.storage.entity.Bill;
import com.accountbook.backend.storage.entity.Budget;
import com.accountbook.proxy.response.bill.BillListResponse;
import com.accountbook.proxy.response.bill.BillDeleteResponse;
import com.accountbook.proxy.response.bill.BillSingleResponse;
import com.accountbook.proxy.request.bill.BillAddParams;
import com.accountbook.proxy.request.bill.BillChangeParams;
import com.accountbook.proxy.request.bill.BillDeleteParams;
import com.accountbook.proxy.request.bill.BillSearchParams;

import java.util.List;

public class AccountBookServiceFactory implements BusinessFactory {

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

    @Override
    public BusinessService<Budget, Long> createAddBudgetService() {
        return new AddBudgetService();
    }

    @Override
    public BusinessService<Long, Void> createDeleteBudgetService() {
        return new DeleteBudgetService();
    }

    // 修正：之前错返回了 ChangeBillService，应返回预算相关的修改服务
    @Override
    public BusinessService<Long, Void> createChangeBudgetService() {
        return new ChangeBudgetService(); // 需确保有 ChangeBudgetService 实现类
    }

    // 修正：之前错返回了 SearchBillService，应返回预算相关的搜索服务
    @Override
    public BusinessService<Budget, List<Budget>> createSearchBudgetService() {
        return new SearchBudgetService(); // 需确保有 SearchBudgetService 实现类
    }
}