package com.accountbook.backend.service.impl;

import com.accountbook.backend.service.BusinessService;
import com.accountbook.backend.storage.dao.BillDAO;
import com.accountbook.backend.storage.dao.factory.DAOFactory;
import com.accountbook.backend.storage.entity.Bill;;

public class AddBillService implements BusinessService<Bill,Long>{
    private final BillDAO billBAO=DAOFactory.getBillDAO();

    @Override
    // 新增账单服务：参数类型bill，返回类型Long（账单ID）
    public Long execute(Bill bill) throws Exception {
        System.out.println("执行账目录入业务");
        return null;
    }
}
