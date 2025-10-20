package com.accountbook.frontend.component;

/*系统设置页面 */
public class SettingPage implements PageComponent {
    @Override
    public void render() {
        System.out.println("渲染系统设置页：设置账户信息");
    }

    @Override
    public void initLayout() {
        System.out.println("初始化可视化页布局：左侧功能栏，右侧账户设置功能");
    }

    @Override
    public boolean sendRequest(String data)
    {
        System.out.println("用户进行操作，将操作发送给中间代理");
        return true; 
    }
}
