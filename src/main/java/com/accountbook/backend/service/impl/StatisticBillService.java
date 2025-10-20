package com.accountbook.backend.service.impl;

import java.util.List;

import com.accountbook.backend.service.BusinessService;
import com.accountbook.backend.storage.dao.BillDAO;
import com.accountbook.backend.storage.dao.factory.DAOFactory;
import com.accountbook.backend.storage.entity.Bill;

public class StatisticBillService implements BusinessService<Bill,List<Bill>>{
    private final BillDAO billBAO=DAOFactory.getBillDAO();

    @Override
    /*bill中非null属性为”统计条件“，返回符合条件的”bill列表“ */
    public List<Bill> execute(Bill bill) throws Exception{
        System.out.println("执行账单统计业务");
        return null;
    }
}
