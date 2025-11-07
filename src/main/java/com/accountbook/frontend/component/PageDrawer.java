package com.accountbook.frontend.component;

import javax.swing.JPanel;

/**
 * 页面绘制接口：所有系统页面需实现此接口，提供绘制自身的方法
 */
public interface PageDrawer {
    /**
     * 绘制页面内容到目标面板
     * @param contentPanel 右侧内容面板（用于承载页面内容）
     */
    void draw(JPanel contentPanel);
}