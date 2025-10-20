package com.accountbook.frontend.component;

/*系统首页*/
public class HomePage implements PageComponent {
    @Override
    public void render()
    {
        System.out.println("渲染系统首页：展示月度财务数据与最近账单列表");
    }

    @Override
    public void initLayout()
    {
        System.out.println("初始化首页布局：左侧功能选项栏，右侧账单展示区");
    }

    @Override
    public boolean sendRequest(String data)
    {
        System.out.println("用户进行操作，将操作发送给中间代理");
        return true; 
    }
}
