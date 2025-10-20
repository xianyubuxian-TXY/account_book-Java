package com.accountbook.frontend.component;

// 抽象页面组件（所有UI页面的顶层接口）
public interface PageComponent {
    void render(); //渲染页面
    void initLayout(); //初始化布局（左侧功能栏+右侧主显示区）    
    boolean sendRequest(String data); //发送操作请求（通过json封装请求）
} 
