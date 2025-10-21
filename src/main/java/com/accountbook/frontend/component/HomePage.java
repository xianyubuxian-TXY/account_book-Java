package com.accountbook.frontend.component;

import com.accountbook.proxy.response.bill.BillListResponse;
import java.math.BigDecimal;

/**
 * 首页示例：继承基类，直接使用注入的共享Helper
 */
public class HomePage extends BasePage {

    @Override
    protected void initPage() {
        // 页面初始化逻辑（如绑定事件、加载数据）
        System.out.println("首页初始化完成");
    }

    @Override
    protected void renderPage() {
        // 渲染页面时调用Helper查询数据（使用共享实例）

    }

    // 其他页面功能（如新增账单按钮点击事件）

}