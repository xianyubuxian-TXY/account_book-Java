package com.accountbook;

import java.math.BigDecimal;
import java.net.Authenticator.RequestorType;

import com.accountbook.backend.factory.AccountBookServiceFactory;
import com.accountbook.backend.factory.BusinessFactory;
import com.accountbook.backend.storage.db.DBInitializer;
import com.accountbook.backend.storage.entity.Bill;
import com.accountbook.frontend.PageComponent;
import com.accountbook.frontend.UIFactory;
import com.accountbook.frontend.factory.AccountBookBusinessUIFactory;
import com.accountbook.proxy.BackendResponse;
import com.accountbook.proxy.FrontendRequest;
import com.accountbook.proxy.ServiceProxy;
import com.accountbook.proxy.FrontendRequest.RequestType;
import com.accountbook.proxy.request.bill.BillAddParams;
import com.accountbook.proxy.request.bill.BillChangeParams;
import com.accountbook.proxy.request.bill.BillDeleteParams;
import com.accountbook.proxy.request.bill.BillSearchParams;
import com.accountbook.proxy.response.bill.BillListResponse;
import com.accountbook.proxy.response.bill.BillDeleteResponse;
import com.accountbook.proxy.response.bill.BillSingleResponse;

/*应用入口类：初始化个各模块，启动系统 */
public class Application {
    public static void main(String[] args)
    {
        /*1.初始化数据库模块 */
        DBInitializer.init();

        /*2.初始化后端工厂与代理*/
        BusinessFactory businessFactory=new AccountBookServiceFactory();
        ServiceProxy serviceProxy=new ServiceProxy(businessFactory);

        /*3.初始化前端UI*/
        UIFactory uiFactory=new AccountBookBusinessUIFactory();
        PageComponent homePage=uiFactory.createHomePage();
        homePage.initLayout(serviceProxy);
        homePage.render();

        /*4.模拟前端请求 */
        //(1)增加账单
        // AddBillParams params=new AddBillParams("2025-10-19",-1,1,1,BigDecimal.valueOf(300.0),"买了件羽绒服朋友");
        // FrontendRequest<AddBillParams> addBillRequest=new FrontendRequest<AddBillParams>(RequestType.ADD_BILL,params);
        // BackendResponse<BillSingleResponse> backendResponse=serviceProxy.handleRequest(addBillRequest);
        // backendResponse.getData().printSelf();
        // if(backendResponse.isSuccess())
        // {
        //     backendResponse.getData().printSelf();
        // }
        // else
        // {
        //     System.err.println(backendResponse.getMessage());
        // }

        //(2)搜索账单
        // SearchBillParams params=new SearchBillParams("2025-10-19", null, null, null, null, null);
        // FrontendRequest<SearchBillParams> searchBillRequset=new FrontendRequest<SearchBillParams>(RequestType.SEARCH_BILL, params);
        // BackendResponse<BillListResponse> backendResponse=serviceProxy.handleRequest(searchBillRequset);
        // if(backendResponse.isSuccess())
        // {
        //     backendResponse.getData().printSelf();
        // }
        // else
        // {
        //     System.err.println(backendResponse.getMessage());
        // }

        // //(3)删除账单
        // BillDeleteParams params=new BillDeleteParams(1);
        // FrontendRequest<BillDeleteParams> delRequest=new FrontendRequest<BillDeleteParams>(RequestType.DELETE_BILL,params);
        // BackendResponse<BillDeleteResponse> backendResponse=serviceProxy.handleRequest(delRequest);
        // if(backendResponse.isSuccess())
        // {
        //     System.err.printf("bill be deleted which id=%d",backendResponse.getData().getBillId());
        // }
        // else
        // {
        //     System.err.println(backendResponse.getMessage());
        // }

        //(4)修改账单
        BillChangeParams params=new BillChangeParams(3,"2025-10-20",1,1,1,BigDecimal.valueOf(500.0),"搬砖");
        FrontendRequest<BillChangeParams> changeRequest=new FrontendRequest<BillChangeParams>(RequestType.CHANGE_BILL,params);
        BackendResponse<BillSingleResponse> backendResponse=serviceProxy.handleRequest(changeRequest);
        if(backendResponse.isSuccess())
        {
            System.out.println(backendResponse.getMessage());
            backendResponse.getData().printSelf();
        }
        else
        {
            System.out.println(backendResponse.getMessage());
        }
    }
}
