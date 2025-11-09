package com.accountbook.frontend.component.page;

import java.awt.Window;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.accountbook.frontend.component.PageDrawer;

public class BudgetPage implements PageDrawer{
    
    @Override
    public void draw(JPanel contentPanel)
    {
        // 获取contentPanel所在的顶级窗口（作为对话框的父组件）
        Window parentWindow = SwingUtilities.getWindowAncestor(contentPanel);
        // 显示提示对话框
        JOptionPane.showMessageDialog(
            parentWindow,  // 父窗口（确保对话框模态显示在该窗口上）
            "该功能还未实现，敬请期待",  // 提示内容
            "提示",  // 对话框标题
            JOptionPane.INFORMATION_MESSAGE  // 信息类型图标
        );
    }
}
