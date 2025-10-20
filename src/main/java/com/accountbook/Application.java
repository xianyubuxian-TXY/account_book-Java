package com.accountbook;

import com.accountbook.backend.factory.AccountBookServiceFactory;
import com.accountbook.backend.factory.BusinessFactory;
import com.accountbook.backend.storage.db.DBInitializer;
import com.accountbook.backend.storage.entity.Bill;
import com.accountbook.frontend.component.PageComponent;
import com.accountbook.frontend.factory.AccountBookBusinessUIFactory;
import com.accountbook.frontend.factory.UIFactory;
import com.accountbook.proxy.BackendResponse;
import com.accountbook.proxy.FrontendRequest;
import com.accountbook.proxy.ServiceProxy;
import com.accountbook.proxy.FrontendRequest.RequestType;

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
        homePage.initLayout();
        homePage.render();

        /*4.模拟前端请求 */
        Bill bill=new Bill("2025-10-19","支出",300,"购物","买衣服","买了件羽绒服朋友");
        FrontendRequest<Bill> addBillRequest=new FrontendRequest<Bill>(RequestType.ADD_BILL,bill);
        BackendResponse<Bill> backendResponse=serviceProxy.handleRequest(addBillRequest);
    }
}
