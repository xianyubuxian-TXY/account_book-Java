package com.accountbook.backend.service.impl;

import com.accountbook.backend.service.BusinessService;
import com.accountbook.backend.storage.dao.BudgetDAO;
import com.accountbook.backend.storage.dao.factory.DAOFactory;

public class ChangeBudgetService implements BusinessService<Long,Void>{
    private final BudgetDAO budgetDAO=DAOFactory.getBudgetDAO();

    @Override
    public Void execute(Long BudgetId) throws Exception {
        System.out.println("执行修改预算业务");
        return null;
    }
}
