package com.accountbook.proxy.common;

import com.accountbook.backend.factory.AccountBookServiceFactory;
import com.accountbook.backend.factory.BusinessFactory;
import com.accountbook.proxy.ServiceProxy;
import com.accountbook.proxy.helper.factory.RequestHelperFactory;
import com.accountbook.proxy.helper.impl.BillRequestHelper;
import com.accountbook.proxy.helper.impl.CategoryRequestHelper;
import com.accountbook.proxy.helper.impl.SpecificTypeRequestHelper;
import com.accountbook.proxy.helper.impl.BudgetRequestHelper;

// ProxyHandler.java（中间代理模块）
public class ProxyHandler {
    private static ServiceProxy accountBookServiceProxy; // AccountBookServiceFactory的代理
    private static BillRequestHelper billRequestHelper;
    // 新增：分类请求助手
    private static CategoryRequestHelper categoryRequestHelper;
    // 新增：具体类型请求助手
    private static SpecificTypeRequestHelper specificTypeRequestHelper;
    // 新增：预算请求助手
    private static BudgetRequestHelper budgetHelper;

    private ProxyHandler() {}

    public static void init() {
        // 创建AccountBookServiceFactory的代理
        BusinessFactory businessFactory = new AccountBookServiceFactory();
        ServiceProxy proxy = new ServiceProxy(businessFactory);
        accountBookServiceProxy = proxy;


        // 初始化账单请求助手
        billRequestHelper = RequestHelperFactory.getInstance().getBillHelper(proxy);
        // 新增：初始化分类请求助手
        categoryRequestHelper = RequestHelperFactory.getInstance().getCategoryHelper(proxy);
        // 新增：初始化具体类型请求助手
        specificTypeRequestHelper = RequestHelperFactory.getInstance().getSpecificTypeHelper(proxy);
        // 新增：初始化预算请求助手
        budgetHelper = RequestHelperFactory.getInstance().getBudgetHelper(proxy);

    }

    public static ServiceProxy getAccountBookServiceProxy() {
        if (accountBookServiceProxy == null) {
            throw new IllegalStateException("ProxyHandler 未初始化，请先调用 init 方法");
        }
        return accountBookServiceProxy;
    }

    public static BillRequestHelper getBillHelper() {
        return billRequestHelper;
    }

    // 新增：分类请求助手的getter方法
    public static CategoryRequestHelper getCategoryHelper() {
        return categoryRequestHelper;
    }

    // 新增：具体类型请求助手的getter方法
    public static SpecificTypeRequestHelper getSpecificTypeHelper() {
        return specificTypeRequestHelper;
    }

    // 新增：预算请求助手的getter方法
    public static BudgetRequestHelper getBudgetHelper() {
        return budgetHelper;
    }
}