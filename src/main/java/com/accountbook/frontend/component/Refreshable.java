// 新增：页面刷新接口，用于缓存更新后通知页面重绘
package com.accountbook.frontend.component;

public interface Refreshable {
    void refresh();
}