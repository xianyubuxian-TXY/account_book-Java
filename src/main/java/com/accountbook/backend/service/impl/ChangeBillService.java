package com.accountbook.backend.service.impl;

import com.accountbook.backend.service.BusinessService;
import com.accountbook.backend.storage.dao.BillDAO;
import com.accountbook.backend.storage.dao.factory.DAOFactory;

public class ChangeBillService implements BusinessService<Long,Void>{
    private final BillDAO billBAO=DAOFactory.getBillDAO();

    @Override
    public Void execute(Long billId) throws Exception {
        System.out.println("执行账单修改业务");
        return null;
    }
}
