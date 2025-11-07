package com.accountbook.backend.service.impl.budget;

import com.accountbook.backend.common.exception.BusinessServiceException;
import com.accountbook.backend.service.BusinessService;
import com.accountbook.backend.storage.dao.BudgetDAO;
import com.accountbook.backend.storage.dao.factory.DAOFactory;
import com.accountbook.proxy.request.budget.BudgetDeleteParams;
import com.accountbook.proxy.response.budget.BudgetDeleteResponse;

public class DeleteBudgetService implements BusinessService<BudgetDeleteParams, BudgetDeleteResponse> {
    private final BudgetDAO budgetDAO = DAOFactory.getBudgetDAO();
    
    @Override
    public BudgetDeleteResponse execute(BudgetDeleteParams params) throws Exception {
        System.out.println("执行预算删除服务");
        Integer budgetId = params.getBudgetId(); // 从参数中获取预算ID
        Integer ret = budgetDAO.deleteBudgetById(budgetId); // 调用DAO删除预算
        
        if (ret == -1 || ret == 0) { // 删除失败（-1：系统异常，0：无匹配记录）
            throw new BusinessServiceException("delete budget failed");
        }
        
        // 删除成功，返回包含被删除预算ID的响应
        return new BudgetDeleteResponse(budgetId);
    }
}