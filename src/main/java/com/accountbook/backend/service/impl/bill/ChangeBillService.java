package com.accountbook.backend.service.impl.bill;

import java.util.Map;

import com.accountbook.backend.common.exception.BusinessServiceException;
import com.accountbook.backend.service.BusinessService;
import com.accountbook.backend.storage.dao.BillDAO;
import com.accountbook.backend.storage.dao.factory.DAOFactory;
import com.accountbook.backend.storage.entity.Bill;
import com.accountbook.proxy.request.bill.BillChangeParams;
import com.accountbook.proxy.response.bill.BillSingleResponse;

public class ChangeBillService implements BusinessService<BillChangeParams,BillSingleResponse>{
    private final BillDAO billBAO=DAOFactory.getBillDAO();
    
    @Override
    public BillSingleResponse execute(BillChangeParams params) throws Exception {
        System.out.println("执行账单修改业务");
        Map<String,Object> map=params.toMap(); //更新条件

        Integer id=params.getBillId();
        Integer ret=billBAO.updateBillById(id, map);
        if(ret==-1 || ret==0) //更新失败
        {
            throw new BusinessServiceException("change bill failed");
        }
        else
        {
            Bill bill=billBAO.queryBillById(id);
            return BillSingleResponse.fromBill(bill);
        }
    }
}
