package com.accountbook.backend.service.impl.bill;

import com.accountbook.backend.common.exception.BusinessServiceException;
import com.accountbook.backend.service.BusinessService;
import com.accountbook.backend.storage.dao.BillDAO;
import com.accountbook.backend.storage.dao.factory.DAOFactory;
import com.accountbook.proxy.request.bill.BillDeleteParams;
import com.accountbook.proxy.response.bill.BillDeleteResponse;

public class DeleteBillService implements BusinessService<BillDeleteParams,BillDeleteResponse>{
    private final BillDAO billBAO=DAOFactory.getBillDAO();
    
    @Override
    public BillDeleteResponse execute(BillDeleteParams params) throws Exception{
        System.out.println("执行bill删除服务");
        Integer id=params.getBillId();
        Integer ret=billBAO.deleteBillById(id);
        if(ret==-1 || ret==0)
        {
            throw new BusinessServiceException("delete bill failed");
        }
        else
        {
            return new BillDeleteResponse(params.getBillId());
        }
    }
}
