package com.accountbook.backend.factory;

import java.util.List;

import com.accountbook.backend.service.BusinessService;
import com.accountbook.backend.storage.entity.Bill;
import com.accountbook.backend.storage.entity.Budget;
import com.accountbook.proxy.request.bill.BillAddParams;
import com.accountbook.proxy.request.bill.BillChangeParams;
import com.accountbook.proxy.request.bill.BillDeleteParams;
import com.accountbook.proxy.request.bill.BillSearchParams;
import com.accountbook.proxy.response.bill.BillListResponse;
import com.accountbook.proxy.response.bill.BillDeleteResponse;
import com.accountbook.proxy.response.bill.BillSingleResponse;

// 抽象业务工厂（定义创建各类业务服务的方法）
public interface BusinessFactory {
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
    
    // 增加预算业务
    BusinessService<Budget, Long> createAddBudgetService();
    
    // 删除预算业务
    BusinessService<Long, Void> createDeleteBudgetService();
    
    // 修改预算业务
    BusinessService<Long, Void> createChangeBudgetService();
    
    // 查找预算业务
    BusinessService<Budget, List<Budget>> createSearchBudgetService();
}
