package com.accountbook.frontend.component;

/*统计页面 */
public class StatisticPage implements PageComponent {
    @Override
    public void render() {
        System.out.println("渲染统计页：分类统计各类账单");
    }

    @Override
    public void initLayout() {
        System.out.println("初始化可视化页布局：左侧功能栏，右侧分类统计后的账单列表");
    }

    @Override
    public boolean sendRequest(String data)
    {
        System.out.println("用户进行操作，将操作发送给中间代理");
        return true; 
    }
}
