package com.accountbook.frontend.factory;

import com.accountbook.frontend.component.PageComponent;

// 抽象工厂接口（定义创建各类页面的方法）
//便于扩展和维护：如果之后要支持其它UI风格，只需要新建具体UI工厂类，而不需要修改原有UI工厂类，符合“开闭原则”
public interface UIFactory {
    PageComponent createHomePage(); // 创建系统首页
    PageComponent createRecordPage(); // 创建记账录入页
    PageComponent createSearchPage(); // 创建账单搜索页
    PageComponent createStatisticPage(); // 创建账单统计页
    PageComponent createVisualPage(); // 创建数据可视化页
    PageComponent createBudgetPage(); // 创建预算管理页
    PageComponent createSettingPage(); // 创建系统设置页
}
