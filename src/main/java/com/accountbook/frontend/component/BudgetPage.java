package com.accountbook.frontend.component;

public class BudgetPage implements PageComponent{
    @Override
    public void render() {
        System.out.println("渲染预算管理页面：添加、管理、查看预算");
    }

    @Override
    public void initLayout() {
        System.out.println("初始化可视化页布局：左侧功能栏，右侧各类预算");
    }

    @Override
    public boolean sendRequest(String data)
    {
        System.out.println("用户进行操作，将操作发送给中间代理");
        return true; 
    }
}
