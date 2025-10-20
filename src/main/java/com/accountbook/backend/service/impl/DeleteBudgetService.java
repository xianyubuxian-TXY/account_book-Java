package com.accountbook.backend.service.impl;

import com.accountbook.backend.service.BusinessService;
import com.accountbook.backend.storage.dao.BillDAO;
import com.accountbook.backend.storage.dao.factory.DAOFactory;

public class DeleteBudgetService implements BusinessService<Long,Void>{
    private final BillDAO billBAO=DAOFactory.getBillDAO();

    @Override
    public Void execute(Long budgetId) throws Exception {
        System.out.println("执行删除预算业务");
        return null;
    }
}
