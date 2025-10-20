package com.accountbook.frontend.component;

/*账单搜索页 */
public class SearchPage implements PageComponent {
        @Override
    public void render() {
        System.out.println("渲染账单搜索也页：搜索功能");
    }

    @Override
    public void initLayout() {
        System.out.println("初始化可视化页布局：左侧功能栏，右侧搜索区");
    }

    @Override
    public boolean sendRequest(String data)
    {
        System.out.println("用户进行操作，将操作发送给中间代理");
        return true; 
    }
}
