package com.accountbook.frontend.component;

public class RecordPage implements PageComponent {
    @Override
    public void render() {
        System.out.println("渲染记账录入页：展示时间、金额、分类等录入组件");
    }

    @Override
    public void initLayout() {
        System.out.println("初始化录入页布局：左侧功能栏，右侧录入表单区");
    }

    @Override
    public boolean sendRequest(String data)
    {
        System.out.println("用户进行操作，将操作发送给中间代理");
        return true; 
    }
}
