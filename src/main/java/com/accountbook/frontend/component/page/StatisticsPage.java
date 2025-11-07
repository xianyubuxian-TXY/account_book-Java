package com.accountbook.frontend.component.page;

import javax.swing.JPanel;

import com.accountbook.frontend.component.PageDrawer;

public class StatisticsPage implements PageDrawer{
    @Override
    public void draw(JPanel contentPanel)
    {
        System.out.println("切换到：统计页面");
    }
}
