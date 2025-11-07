package com.accountbook.backend.service.impl.budget;

import com.accountbook.backend.common.exception.BusinessServiceException;
import com.accountbook.backend.service.BusinessService;
import com.accountbook.backend.storage.dao.BudgetDAO;
import com.accountbook.backend.storage.dao.factory.DAOFactory;
import com.accountbook.backend.storage.entity.Budget;
import com.accountbook.proxy.request.budget.BudgetAddParams;
import com.accountbook.proxy.response.budget.BudgetSingleResponse;

import java.util.Map;

public class AddBudgetService implements BusinessService<BudgetAddParams, BudgetSingleResponse> {
    private final BudgetDAO budgetDAO = DAOFactory.getBudgetDAO();

    @Override
    /*新增预算服务：返回代理层响应对象，避免前端直接依赖后端实体*/
    public BudgetSingleResponse execute(BudgetAddParams params) throws Exception {
        System.out.println("执行增加预算服务");

        Map<String, Object> map = params.toMap();
        
        // 调用DAO添加预算，获取新增记录的主键ID
        int id = budgetDAO.addBudget(map);
        if (id == -1) {
            throw new BusinessServiceException("add budget failed");
        }
        
        // 通过ID查询新增的预算实体，转换为响应对象返回
        Budget budget = budgetDAO.queryBudgetById(id);
        return BudgetSingleResponse.fromBudget(budget);
    }
}