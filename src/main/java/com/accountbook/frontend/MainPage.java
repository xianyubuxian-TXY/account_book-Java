package com.accountbook.frontend;

import com.accountbook.frontend.component.PageDrawer;
import com.accountbook.frontend.component.Refreshable;
import com.accountbook.frontend.component.panel.LeftNavigationPanel;
import com.accountbook.frontend.component.page.*;
import com.accountbook.proxy.common.ProxyHandler;
import com.accountbook.proxy.helper.impl.BillRequestHelper;
import com.accountbook.proxy.helper.impl.CategoryRequestHelper;
import com.accountbook.proxy.helper.impl.SpecificTypeRequestHelper;
import com.accountbook.proxy.response.bill.BillSingleResponse;
import com.accountbook.proxy.response.category.CategorySingleResponse;
import com.accountbook.proxy.response.specific_type.SpecificTypeSingleResponse;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainPage extends JFrame {
    private JPanel contentPanel; // 内容面板（优先初始化）

    // 数据缓存
    private final Map<Integer, CategorySingleResponse> categoryMap = new HashMap<>();
    private final Map<Integer, SpecificTypeSingleResponse> specificTypeMap = new HashMap<>();
    private final Map<Integer, BillSingleResponse> billMap = new HashMap<>();

    // 页面映射
    private final Map<String, PageDrawer> pageMap = new HashMap<>();

    // 刷新通知列表
    private final List<Refreshable> refreshablePages = new ArrayList<>();

    // 后端交互Helper
    private final CategoryRequestHelper categoryHelper;
    private final SpecificTypeRequestHelper specificTypeHelper;
    private final BillRequestHelper billHelper;

    public MainPage() {
        // 1. 初始化ProxyHandler（确保Helper可用）
        ProxyHandler.init();

        // 2. 获取已初始化的Helper
        this.categoryHelper = ProxyHandler.getCategoryHelper();
        this.specificTypeHelper = ProxyHandler.getSpecificTypeHelper();
        this.billHelper = ProxyHandler.getBillHelper();

        // 3. 先初始化UI（确保contentPanel存在）
        initUI();

        // 4. 先加载数据，再初始化页面（关键：保证页面绘制时有数据）
        loadInitialData();
        initPages();
    }

    /**
     * 初始化UI（优先创建contentPanel）
     */
    private void initUI() {
        setTitle("个人记账系统");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());

        // 关键：先创建内容面板（确保非空）
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        // 后创建导航面板（依赖contentPanel）
        LeftNavigationPanel navPanel = new LeftNavigationPanel(this);
        getContentPane().add(navPanel, BorderLayout.WEST);
    }

    /**
     * 初始化页面（数据加载后执行，确保绘制时有数据）
     */
    private void initPages() {
        PageDrawer homePage = HomePage.getInstance(this);
        PageDrawer recordPage = new RecordPage(this);
        PageDrawer searchPage = new SearchPage();
        PageDrawer statisticsPage = new StatisticsPage();
        PageDrawer visualPage = new VisualPage();
        PageDrawer budgetPage = new BudgetPage();
        PageDrawer settingPage = new SettingPage();

        // 注册页面
        pageMap.put("系统首页", homePage);
        pageMap.put("记账录入", recordPage);
        pageMap.put("账单搜索", searchPage);
        pageMap.put("账单统计", statisticsPage);
        pageMap.put("数据可视化", visualPage);
        pageMap.put("预算管理", budgetPage);
        pageMap.put("系统设置", settingPage);

        // 关键：数据加载完成后，主动绘制首页
        switchPage("系统首页");
    }

    /**
     * 切换页面（确保contentPanel非空）
     */
    public void switchPage(String pageName) {
        if (contentPanel == null) {
            JOptionPane.showMessageDialog(this, "内容面板未初始化，无法切换页面", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        PageDrawer page = pageMap.get(pageName);
        if (page != null) {
            page.draw(contentPanel); // 绘制页面
        } else {
            JOptionPane.showMessageDialog(this, "页面不存在：" + pageName);
        }
    }

    /**
     * 加载初始数据（数据加载后通知页面刷新）
     */
    private void loadInitialData() {
        try {
            // 加载大类
            List<CategorySingleResponse> categories = categoryHelper.searchAllCategories().getItems();
            categoryMap.clear();
            categories.forEach(cat -> categoryMap.put(cat.getCategoryId(), cat));
            System.out.println("加载大类：" + categoryMap.size() + "个");

            // 加载具体类型
            List<SpecificTypeSingleResponse> specificTypes = specificTypeHelper.searchAllSpecificTypes().getItems();
            specificTypeMap.clear();
            specificTypes.forEach(type -> specificTypeMap.put(type.getSpecificTypeId(), type));
            System.out.println("加载具体类型：" + specificTypeMap.size() + "个");

            // 加载账单（核心数据）
            List<BillSingleResponse> bills = billHelper.searchAllBills().getItems();
            billMap.clear();
            bills.forEach(bill -> billMap.put(bill.getBillId(), bill));
            System.out.println("加载账单：" + billMap.size() + "条");

            // 关键：数据加载完成后，通知所有页面刷新（包括首页）
            notifyRefresh();

        } catch (Exception e) {
            System.err.println("数据加载失败：" + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "数据加载失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ------------------------------ 缓存操作与刷新通知 ------------------------------

    public void addCategory(CategorySingleResponse category) {
        categoryMap.put(category.getCategoryId(), category);
        notifyRefresh();
    }

    public void updateCategory(CategorySingleResponse category) {
        categoryMap.put(category.getCategoryId(), category);
        notifyRefresh();
    }

    public void deleteCategory(Integer categoryId) {
        categoryMap.remove(categoryId);
        notifyRefresh();
    }

    public void addSpecificType(SpecificTypeSingleResponse type) {
        specificTypeMap.put(type.getSpecificTypeId(), type);
        notifyRefresh();
    }

    public void updateSpecificType(SpecificTypeSingleResponse type) {
        specificTypeMap.put(type.getSpecificTypeId(), type);
        notifyRefresh();
    }

    public void deleteSpecificType(Integer typeId) {
        specificTypeMap.remove(typeId);
        notifyRefresh();
    }

    public void addBill(BillSingleResponse bill) {
        billMap.put(bill.getBillId(), bill);
        System.out.println("新增账单，当前总数：" + billMap.size());
        notifyRefresh();
    }

    public void updateBill(BillSingleResponse bill) {
        billMap.put(bill.getBillId(), bill);
        notifyRefresh();
    }

    public void deleteBill(Integer billId) {
        billMap.remove(billId);
        System.out.println("删除账单，当前总数：" + billMap.size());
        notifyRefresh();
    }

    public void registerRefreshable(Refreshable page) {
        if (page != null && !refreshablePages.contains(page)) {
            refreshablePages.add(page);
        }
    }

    private void notifyRefresh() {
        for (Refreshable page : new ArrayList<>(refreshablePages)) {
            page.refresh();
        }
    }

    // ------------------------------ Getter方法（带非空校验） ------------------------------

    public JPanel getContentPanel() {
        if (contentPanel == null) {
            throw new IllegalStateException("contentPanel未初始化！请检查MainPage的initUI执行顺序");
        }
        return contentPanel;
    }

    public Map<Integer, CategorySingleResponse> getCategoryMap() {
        return new HashMap<>(categoryMap);
    }

    public Map<Integer, SpecificTypeSingleResponse> getSpecificTypeMap() {
        return new HashMap<>(specificTypeMap);
    }

    public Map<Integer, BillSingleResponse> getBillMap() {
        return new HashMap<>(billMap);
    }

    public CategoryRequestHelper getCategoryHelper() {
        return categoryHelper;
    }

    public SpecificTypeRequestHelper getSpecificTypeHelper() {
        return specificTypeHelper;
    }

    public BillRequestHelper getBillHelper() {
        return billHelper;
    }

    /**
     * 程序入口
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                MainPage mainPage = new MainPage();
                mainPage.setVisible(true);
            } catch (Exception e) {
                System.err.println("程序启动失败：" + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "启动失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}