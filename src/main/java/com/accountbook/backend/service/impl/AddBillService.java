package com.accountbook.backend.service.impl;

import java.util.Map;

import com.accountbook.backend.common.exception.BillBusinessException;
import com.accountbook.backend.service.BusinessService;
import com.accountbook.backend.storage.dao.BillDAO;
import com.accountbook.backend.storage.dao.factory.DAOFactory;
import com.accountbook.backend.storage.entity.Bill;
import com.accountbook.proxy.request.bill.BillAddParams;
import com.accountbook.proxy.response.bill.BillSingleResponse;;


public class AddBillService implements BusinessService<BillAddParams,BillSingleResponse>{
    private final BillDAO billBAO=DAOFactory.getBillDAO();

    @Override
    /*新增账单服务：为什么不直接返回bill？因为bill属于后端实体，前端应该使用代理提供的response*/
    public BillSingleResponse execute(BillAddParams params) throws Exception {
        System.out.println("执行增加bill服务");

        Map<String,Object> map=params.toMap();
        
        //获取主键id，用于通过id进行查询，然后返回给用户
        int id=billBAO.addBill(map);
        if(id==-1) throw new BillBusinessException("add bill failed");
        else
        {
            Bill bill=billBAO.queryBillById(id);
            return BillSingleResponse.fromBill(bill);
        }
    }
}
