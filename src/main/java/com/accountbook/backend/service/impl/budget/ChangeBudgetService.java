package com.accountbook.backend.service.impl.budget;

import com.accountbook.backend.common.exception.BusinessServiceException;
import com.accountbook.backend.service.BusinessService;
import com.accountbook.backend.storage.dao.BudgetDAO;
import com.accountbook.backend.storage.dao.factory.DAOFactory;
import com.accountbook.backend.storage.entity.Budget;
import com.accountbook.proxy.request.budget.BudgetChangeParams;
import com.accountbook.proxy.response.budget.BudgetSingleResponse;

import java.util.Map;

public class ChangeBudgetService implements BusinessService<BudgetChangeParams, BudgetSingleResponse> {
    private final BudgetDAO budgetDAO = DAOFactory.getBudgetDAO();
    
    @Override
    public BudgetSingleResponse execute(BudgetChangeParams params) throws Exception {
        System.out.println("执行预算修改业务");
        Map<String, Object> updateMap = params.toMap(); // 获取更新字段的映射

        Integer budgetId = params.getBudgetId(); // 从参数中获取预算ID（唯一标识）
        Integer ret = budgetDAO.updateBudgetById(budgetId, updateMap); // 调用DAO更新预算
        
        if (ret == -1 || ret == 0) { // 更新失败（-1：系统异常，0：无匹配记录）
            throw new BusinessServiceException("change budget failed");
        }
        
        // 更新成功后，查询最新的预算信息并转换为响应对象
        Budget budget = budgetDAO.queryBudgetById(budgetId);
        return BudgetSingleResponse.fromBudget(budget);
    }
}