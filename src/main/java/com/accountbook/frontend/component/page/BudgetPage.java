package com.accountbook.frontend.component.page;

import javax.swing.JPanel;

import com.accountbook.frontend.component.PageDrawer;

public class BudgetPage implements PageDrawer{
    
    @Override
    public void draw(JPanel contentPanel)
    {
        System.err.println("求换到：预算页面");
    }
}
