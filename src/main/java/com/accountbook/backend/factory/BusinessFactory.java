package com.accountbook.backend.factory;

import com.accountbook.backend.service.BusinessService;

// 抽象业务工厂（定义创建各类业务服务的方法）
public interface BusinessFactory {
    BusinessService createAddBillService(); // 账目录入业务
    BusinessService createDeleteBillService(); //账单删除业务
    BusinessService createChangeBillService(); // 账单修改业务
    BusinessService createSearchBillService(); // 账单搜索业务
    BusinessService createStatisticBillService(); // 账单统计业务
    BusinessService createVisualService(); // 数据可视化业务

    BusinessService createAddBudgetService(); // 增加预算
    BusinessService createDeleteBudgetService(); // 删除预算
    BusinessService createChangeBudgetService(); // 修改预算
    BusinessService createSearchBudgetService(); // 查找预算
}
