package com.accountbook.frontend.component.panel;

import com.accountbook.frontend.MainPage;
import com.accountbook.frontend.component.PageDrawer;
import com.accountbook.frontend.component.page.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class LeftNavigationPanel extends JPanel {
    private static final int NAV_WIDTH = 180;
    private static final Color NAV_BG_COLOR = new Color(230, 240, 250);
    private static final Color BTN_BORDER_COLOR = new Color(200, 210, 220);
    private static final int BTN_FONT_SIZE = 16;
    private static final int BTN_HEIGHT = 48;
    private static final int BTN_GAP = 8;
    private static final Color SELECTED_BG_COLOR = new Color(200, 220, 255);

    private JButton selectedButton;
    private final JPanel contentPanel;
    private final MainPage mainPage;

    public LeftNavigationPanel(MainPage mainPage) {
        this.mainPage = mainPage;
        this.contentPanel = mainPage.getContentPanel();
        initUI();
    }

    public LeftNavigationPanel() {
        this(new MainPage());
    }

    private void initUI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(NAV_WIDTH, 0));
        setBackground(NAV_BG_COLOR);

        // 1. 标题区
        add(createTitlePanel());
        // 2. 伸缩空白
        add(Box.createVerticalGlue());
        // 3. 按钮组
        createMenuButtons();
    }

    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new GridBagLayout());
        titlePanel.setBackground(NAV_BG_COLOR);
        titlePanel.setMaximumSize(new Dimension(NAV_WIDTH, Short.MAX_VALUE));

        JLabel titleLabel = new JLabel("记账本");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 28));
        titleLabel.setForeground(new Color(30, 80, 150));
        titlePanel.add(titleLabel);

        return titlePanel;
    }

    private void createMenuButtons() {
        MenuData[] menus = {
            new MenuData("系统首页",    "home",     HomePage.getInstance(mainPage)),
            new MenuData("记账录入",    "edit",     new RecordPage(mainPage)),
            new MenuData("账单搜索",    "search",   new SearchPage()),
            new MenuData("账单统计",    "chart",    new StatisticsPage()),
            new MenuData("数据可视化",  "pieChart", new VisualPage()),
            new MenuData("预算管理",    "money",    new BudgetPage()),
            new MenuData("系统设置",    "gear",     new SettingPage())
        };

        for (MenuData data : menus) {
            JButton btn = createMenuButton(data);
            add(btn);
            add(Box.createVerticalStrut(BTN_GAP));
        }
    }

    private JButton createMenuButton(MenuData data) {
        JButton button = new JButton(data.name);
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setVerticalAlignment(SwingConstants.CENTER);
        button.setIcon(getIcon(data.iconName));
        button.setIconTextGap(6);

        button.setFont(new Font("微软雅黑", Font.PLAIN, BTN_FONT_SIZE));
        button.setForeground(Color.DARK_GRAY);
        button.setBackground(NAV_BG_COLOR);

        button.setMaximumSize(new Dimension(NAV_WIDTH - 16, BTN_HEIGHT));
        button.setPreferredSize(new Dimension(NAV_WIDTH - 16, BTN_HEIGHT));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        button.setBorder(BorderFactory.createLineBorder(BTN_BORDER_COLOR));
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setFocusPainted(false);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button != selectedButton) button.setBackground(new Color(220, 230, 250));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (button != selectedButton) button.setBackground(NAV_BG_COLOR);
            }
        });
        button.addActionListener(e -> {
            selectButton(button);
            data.pageDrawer.draw(contentPanel);
        });

        return button;
    }

    private void selectButton(JButton btn) {
        if (selectedButton != null) selectedButton.setBackground(NAV_BG_COLOR);
        selectedButton = btn;
        selectedButton.setBackground(SELECTED_BG_COLOR);
    }

    private ImageIcon getIcon(String name) {
        URL url = getClass().getResource("/icons/" + name + ".png");
        if (url == null) return null;
        Image img = new ImageIcon(url).getImage()
                      .getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private static class MenuData {
        String name, iconName;
        PageDrawer pageDrawer;
        MenuData(String n, String i, PageDrawer p) {
            name = n; iconName = i; pageDrawer = p;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("导航栏测试");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 600);
            frame.setLocationRelativeTo(null);

            JPanel content = new JPanel();
            content.setBackground(Color.WHITE);

            LeftNavigationPanel nav = new LeftNavigationPanel(new MainPage());
            frame.setLayout(new BorderLayout());
            frame.add(nav, BorderLayout.WEST);
            frame.add(content, BorderLayout.CENTER);
            frame.setVisible(true);
        });
    }
}