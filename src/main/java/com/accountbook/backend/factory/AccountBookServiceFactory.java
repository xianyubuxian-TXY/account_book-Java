package com.accountbook.backend.factory;

import com.accountbook.backend.service.BusinessService;
import com.accountbook.backend.service.impl.AddBillService;
import com.accountbook.backend.service.impl.AddBudgetService;
import com.accountbook.backend.service.impl.ChangeBillService;
import com.accountbook.backend.service.impl.DeleteBillService;
import com.accountbook.backend.service.impl.DeleteBudgetService;
import com.accountbook.backend.service.impl.SearchBillService;
import com.accountbook.backend.service.impl.StatisticBillService;
import com.accountbook.backend.service.impl.VisualService;

public class AccountBookServiceFactory implements BusinessFactory{

    @Override
    public BusinessService createAddBillService() {
        return new AddBillService();
    }

    @Override
    public BusinessService createDeleteBillService(){
        return new DeleteBillService();
    }

    @Override
    public BusinessService createChangeBillService(){
        return new ChangeBillService();
    }

    @Override
    public BusinessService createSearchBillService() {
        return new SearchBillService();
    }

    @Override
    public BusinessService createStatisticBillService() {
        return new StatisticBillService();
    }


    @Override
    public BusinessService createVisualService() {
        return new VisualService();
    }

    @Override
    public BusinessService createAddBudgetService() {
        return new AddBudgetService();
    }

    @Override
    public BusinessService createDeleteBudgetService() {
        return new DeleteBudgetService();
    }

    @Override
    public BusinessService createChangeBudgetService() {
        return new ChangeBillService();
    }

    @Override
    public BusinessService createSearchBudgetService() {
        return new SearchBillService();
    }
}
