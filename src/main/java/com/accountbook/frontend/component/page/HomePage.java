package com.accountbook.frontend.component.page;

import com.accountbook.frontend.MainPage;
import com.accountbook.frontend.component.PageDrawer;
import com.accountbook.frontend.component.Refreshable;
import com.accountbook.frontend.component.dialog.BillEditDialog;
import com.accountbook.proxy.response.bill.BillSingleResponse;
import com.accountbook.proxy.response.category.CategorySingleResponse;
import com.accountbook.proxy.response.specific_type.SpecificTypeSingleResponse;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class HomePage implements PageDrawer, Refreshable {
    private static volatile HomePage instance;
    private final MainPage mainPage;
    private final DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private int currentFontSize = 12;
    private JPanel currentContentPanel;

    private HomePage(MainPage mainPage) {
        this.mainPage = mainPage;
        this.mainPage.registerRefreshable(this);
    }

    public static HomePage getInstance(MainPage mainPage) {
        if (instance == null) {
            synchronized (HomePage.class) {
                if (instance == null) {
                    instance = new HomePage(mainPage);
                }
            }
        }
        return instance;
    }

    @Override
    public void draw(JPanel contentPanel) {
        this.currentContentPanel = contentPanel;
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout(0, 20));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);
        buildPageComponents(contentPanel);
    }

    private void buildPageComponents(JPanel contentPanel) {
        Map<Integer, BillSingleResponse> billMap = mainPage.getBillMap();

        BigDecimal totalExpense = BigDecimal.ZERO;
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal budget = new BigDecimal("1500");

        for (BillSingleResponse bill : billMap.values()) {
            if (bill.getType() == -1) totalExpense = totalExpense.add(bill.getAmount());
            else totalIncome = totalIncome.add(bill.getAmount());
        }

        BigDecimal remainingBudget = budget.subtract(totalExpense);
        BigDecimal netIncome = totalIncome.subtract(totalExpense);

        JPanel statsPanel = createStatsPanel(
                "2025-10", budget, totalExpense, remainingBudget, totalIncome, netIncome
        );
        contentPanel.add(statsPanel, BorderLayout.NORTH);

        JPanel billListPanel = createBillListPanel(billMap);
        contentPanel.add(billListPanel, BorderLayout.CENTER);

        contentPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustFontSize(contentPanel.getWidth());
                contentPanel.revalidate();
                contentPanel.repaint();
            }
        });

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    @Override
    public void refresh() {
        if (currentContentPanel != null) draw(currentContentPanel);
    }

    private void adjustFontSize(int width) {
        currentFontSize = width < 800 ? 10 : (width < 1200 ? 12 : (width < 1600 ? 14 : 16));
    }

    private JPanel createStatsPanel(String currentMonth, BigDecimal budget, BigDecimal totalExpense,
                                   BigDecimal remainingBudget, BigDecimal totalIncome, BigDecimal netIncome) {
        JPanel panel = new JPanel(new GridLayout(1, 6, 15, 20));
        panel.setPreferredSize(new Dimension(0, 120));
        panel.setBackground(new Color(135, 206, 250));
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        String expenseStr = decimalFormat.format(totalExpense) + " (" +
                (totalExpense.compareTo(BigDecimal.ZERO) == 0 ? 0 :
                        totalExpense.divide(budget, 4, RoundingMode.HALF_UP)
                                .multiply(new BigDecimal("100"))).intValue() + "%)";
        String remainingStr = decimalFormat.format(remainingBudget);
        String incomeStr = decimalFormat.format(totalIncome);
        String netStr = decimalFormat.format(netIncome);

        addStatItem(panel, "当前年月", currentMonth, Color.WHITE);
        addStatItem(panel, "预算/元", budget.toPlainString(), Color.WHITE);
        addStatItem(panel, "已支出/元", expenseStr, Color.WHITE);
        addStatItem(panel, "剩余预算/元", remainingStr,
                remainingBudget.compareTo(BigDecimal.ZERO) < 0 ? Color.RED : Color.WHITE);
        addStatItem(panel, "收入/元", incomeStr, Color.WHITE);
        addStatItem(panel, "净收入/元", netStr,
                netIncome.compareTo(BigDecimal.ZERO) < 0 ? Color.RED : Color.WHITE);
        return panel;
    }

    private void addStatItem(JPanel panel, String title, String value, Color valueColor) {
        JPanel itemPanel = new JPanel(new BorderLayout(0, 8));
        itemPanel.setBackground(new Color(135, 206, 250));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, currentFontSize + 3));

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setForeground(valueColor);
        valueLabel.setFont(new Font("微软雅黑", Font.PLAIN, currentFontSize + 3));

        itemPanel.add(titleLabel, BorderLayout.NORTH);
        itemPanel.add(valueLabel, BorderLayout.CENTER);
        panel.add(itemPanel);
    }

    private JPanel createBillListPanel(Map<Integer, BillSingleResponse> billMap) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("账单列表"));
        panel.setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new GridLayout(1, 6));
        headerPanel.setPreferredSize(new Dimension(0, 40));
        headerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        headerPanel.setBackground(Color.WHITE);
        String[] headers = {"时间", "支出/收入", "类型", "金额/元", "备注", "操作"};
        for (String header : headers) {
            JLabel label = new JLabel(header, SwingConstants.CENTER);
            label.setFont(new Font("微软雅黑", Font.BOLD, currentFontSize));
            label.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            label.setOpaque(true);
            label.setBackground(Color.WHITE);
            headerPanel.add(label);
        }
        panel.add(headerPanel, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);

        if (billMap.isEmpty()) {
            JLabel emptyLabel = new JLabel("暂无账单数据，请添加账单", SwingConstants.CENTER);
            emptyLabel.setPreferredSize(new Dimension(0, 200));
            emptyLabel.setFont(new Font("微软雅黑", Font.PLAIN, currentFontSize + 2));
            emptyLabel.setForeground(new Color(100, 100, 100));
            listPanel.add(emptyLabel);
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            billMap.values().stream()
                    .sorted((b1, b2) -> {
                        LocalDateTime t1 = LocalDateTime.parse(b1.getBillTime(), formatter);
                        LocalDateTime t2 = LocalDateTime.parse(b2.getBillTime(), formatter);
                        return t2.compareTo(t1);
                    })
                    .forEach(bill -> {
                        JPanel rowPanel = createBillRow(bill);
                        listPanel.add(rowPanel);
                        listPanel.add(Box.createVerticalStrut(2));
                    });
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createBillRow(BillSingleResponse bill) {
        JPanel rowPanel = new JPanel(new GridLayout(1, 6));
        rowPanel.setPreferredSize(new Dimension(0, 60));
        rowPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        rowPanel.setBackground(Color.WHITE);

        JLabel timeLabel = new JLabel(bill.getBillTime(), SwingConstants.CENTER);
        timeLabel.setFont(new Font("微软雅黑", Font.PLAIN, currentFontSize));
        rowPanel.add(timeLabel);

        String typeText = bill.getType() == -1 ? "支出" : "收入";
        Color typeColor = bill.getType() == -1 ? Color.RED : new Color(0, 150, 0);
        JLabel typeLabel = new JLabel(typeText, SwingConstants.CENTER);
        typeLabel.setForeground(typeColor);
        typeLabel.setFont(new Font("微软雅黑", Font.PLAIN, currentFontSize));
        rowPanel.add(typeLabel);

        // --- 改造：类型列并排显示 大类「具体类型」 ---
        String categoryName = "未知类型";
        CategorySingleResponse category = mainPage.getCategoryMap().get(bill.getCategoryId());
        if (category != null) categoryName = category.getName();

        String specificName = "无";
        SpecificTypeSingleResponse specific = mainPage.getSpecificTypeMap().get(bill.getSpecificTypeId());
        if (specific != null) specificName = specific.getName();

        String displayType = categoryName + " 「" + specificName + "」";
        JLabel categoryLabel = new JLabel(displayType, SwingConstants.CENTER);
        categoryLabel.setFont(new Font("微软雅黑", Font.PLAIN, currentFontSize));
        categoryLabel.setToolTipText(displayType);
        categoryLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        rowPanel.add(categoryLabel);

        JLabel amountLabel = new JLabel(decimalFormat.format(bill.getAmount()) + " 元", SwingConstants.CENTER);
        amountLabel.setFont(new Font("微软雅黑", Font.PLAIN, currentFontSize));
        rowPanel.add(amountLabel);

        String remark = bill.getRemark() != null ? bill.getRemark() : "无";
        JLabel remarkLabel = new JLabel(remark, SwingConstants.CENTER);
        remarkLabel.setFont(new Font("微软雅黑", Font.PLAIN, currentFontSize));
        remarkLabel.setToolTipText(remark);
        rowPanel.add(remarkLabel);

        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.X_AXIS));
        actionPanel.setBackground(Color.WHITE);

        JButton detailBtn = new JButton("详情");
        styleActionButton(detailBtn, Color.BLUE, currentFontSize + 1);
        detailBtn.addActionListener(e -> showBillDetail(bill));
        actionPanel.add(detailBtn);
        actionPanel.add(Box.createHorizontalStrut(10));

        JButton editBtn = new JButton("修改");
        styleActionButton(editBtn, Color.BLUE, currentFontSize + 1);
        editBtn.addActionListener(e -> showEditDialog(bill));
        actionPanel.add(editBtn);
        actionPanel.add(Box.createHorizontalStrut(10));

        JButton deleteBtn = new JButton("删除");
        styleActionButton(deleteBtn, Color.RED, currentFontSize + 1);
        deleteBtn.addActionListener(e -> deleteBill(bill, rowPanel));
        actionPanel.add(deleteBtn);

        rowPanel.add(actionPanel);
        return rowPanel;
    }

    private void styleActionButton(JButton btn, Color borderColor, int fontSize) {
        btn.setFont(new Font("微软雅黑", Font.PLAIN, fontSize));
        btn.setForeground(borderColor);
        btn.setBorderPainted(true);
        btn.setBorder(BorderFactory.createLineBorder(borderColor, 2));
        btn.setContentAreaFilled(true);
        btn.setBackground(new Color(245, 245, 245));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(90, 35));

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(230,230,230)); }
            @Override public void mouseExited(MouseEvent e) { btn.setBackground(new Color(245,245,245)); }
        });
    }

    private void showBillDetail(BillSingleResponse bill) {
        JTextArea textArea = new JTextArea(bill.getFormattedString());
        textArea.setEditable(false);
        textArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        JOptionPane.showMessageDialog(null, scrollPane,
                "账单详情（ID：" + bill.getBillId() + "）", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showEditDialog(BillSingleResponse bill) {
        BillEditDialog editDialog = new BillEditDialog(mainPage, bill);
        editDialog.setVisible(true);
    }

    private void deleteBill(BillSingleResponse bill, JPanel rowPanel) {
        int confirm = JOptionPane.showConfirmDialog(null,
                "确定要删除账单 ID：" + bill.getBillId() + " 吗？",
                "确认删除", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            mainPage.getBillHelper().deleteBill(bill.getBillId());
            mainPage.deleteBill(bill.getBillId());
            if (currentContentPanel != null) draw(currentContentPanel);
            JOptionPane.showMessageDialog(null, "删除成功！");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "删除失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}
