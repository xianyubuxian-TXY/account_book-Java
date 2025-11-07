package com.accountbook.backend.service.impl.bill;

import com.accountbook.backend.service.BusinessService;
import com.accountbook.backend.storage.dao.BillDAO;
import com.accountbook.backend.storage.dao.factory.DAOFactory;

public class VisualService implements BusinessService<String,Void>{
    private final BillDAO billBAO=DAOFactory.getBillDAO();
    
    @Override
    /*month为需要可视化的月份 */
    public Void execute(String month) throws Exception {
        System.out.println("执行账单数据可视化业务");
        return null;
    }
}
