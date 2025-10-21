package com.accountbook.frontend;

import com.accountbook.proxy.ServiceProxy;

// 抽象页面组件（所有UI页面的顶层接口）
public interface PageComponent {
    void render(); //渲染页面
    void initLayout(ServiceProxy serviceProxy); //初始化布局（左侧功能栏+右侧主显示区）    
} 
