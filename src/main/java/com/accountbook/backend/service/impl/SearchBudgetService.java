package com.accountbook.backend.service.impl;

import java.util.List;

import com.accountbook.backend.service.BusinessService;
import com.accountbook.backend.storage.dao.BillDAO;
import com.accountbook.backend.storage.dao.factory.DAOFactory;
import com.accountbook.backend.storage.entity.Budget;

public class SearchBudgetService implements BusinessService<Budget,List<Budget>>{
    private final BillDAO billBAO=DAOFactory.getBillDAO();

    @Override
    /*budget中非null属性为”搜索条件“，返回符合条件的”budget列表“ */
    public List<Budget> execute(Budget budget) {
        System.out.println("执行删除预算业务");
        return null;
    }
}
