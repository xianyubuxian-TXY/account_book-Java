package com.accountbook.backend.service.impl;

import com.accountbook.backend.service.BusinessService;
import com.accountbook.backend.storage.dao.BudgetDAO;
import com.accountbook.backend.storage.dao.factory.DAOFactory;
import com.accountbook.backend.storage.entity.Budget;

public class AddBudgetService implements BusinessService<Budget,Long>{
    private final BudgetDAO budgetDAO=DAOFactory.getBudgetDAO();

    @Override
    // 新增预算服务：参数类型budget，返回类型Budget（完整预算对象）
    public Long execute(Budget budget) throws Exception {
        System.out.println("执行增加预算业务");
        return null;
    }
}
