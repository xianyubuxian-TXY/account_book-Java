package com.accountbook.frontend.component;

import com.accountbook.frontend.PageComponent;
import com.accountbook.proxy.ServiceProxy;
import com.accountbook.proxy.helper.factory.RequestHelperFactory;
import com.accountbook.proxy.helper.impl.BillRequestHelper;

/**
 * 前端页面基类：自动注入共享的Helper实例，所有页面继承此类
 */
public abstract class BasePage implements PageComponent {
    // 共享的账单请求助手（子类可直接使用）
    protected BillRequestHelper billHelper;

    /**
     * 初始化页面时自动注入Helper（依赖工厂的共享实例）
     * @param serviceProxy 业务代理（全局唯一，从应用入口传入）
     */
    @Override
    public void initLayout(ServiceProxy serviceProxy) {
        // 从工厂获取共享的账单助手（首次创建，后续直接复用）
        this.billHelper = RequestHelperFactory.getInstance()
                .getBillHelper(serviceProxy);
        // 调用子类的具体初始化逻辑
        initPage();
    }

    /**
     * 子类实现具体页面初始化（无需关心Helper注入）
     */
    protected abstract void initPage();

    
    // 其他通用页面方法（如渲染、销毁等）
    @Override
    public void render() {
        renderPage();
    }

    /**
     * 子类实现具体页面渲染
     */
    protected abstract void renderPage();
}