package com.accountbook.frontend.component;

public class VisualPage implements PageComponent {
    @Override
    public void render() {
        System.out.println("渲染数据可视化页：生成支出大类饼图及占比百分比");
    }

    @Override
    public void initLayout() {
        System.out.println("初始化可视化页布局：左侧功能栏，右侧图表展示区");
    }

    @Override
    public boolean sendRequest(String data)
    {
        System.out.println("用户进行操作，将操作发送给中间代理");
        return true; 
    }
}
