package com.accountbook.frontend.component.panel;

import javax.swing.*;

import com.accountbook.proxy.response.bill.BillSingleResponse;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.FontMetrics;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 账单表格组件：表头固定在顶部，与数据列严格对齐
 */
public class BillTableComponent extends JPanel {
    private final Map<Integer, BillSingleResponse> bills;
    private final Map<Integer, ActionListener> detailCallbacks = new HashMap<>();
    private final Map<Integer, ActionListener> editCallbacks = new HashMap<>();
    private final Map<Integer, ActionListener> deleteCallbacks = new HashMap<>();
    private int baseFontSize = 13;
    private JPanel contentPanel;

    public BillTableComponent(Map<Integer, BillSingleResponse> bills) {
        this.bills = bills;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder());
        initTable();
        addResizeListener();
    }

    private void addResizeListener() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                baseFontSize = Math.max(10, Math.min(18, getWidth() / 90));
                refreshTable();
            }
        });
    }

    private void refreshTable() {
        removeAll();
        initTable();
        revalidate();
        repaint();
    }

    public void setCallback(int billId, ActionListener detail, ActionListener edit, ActionListener delete) {
        detailCallbacks.put(billId, detail);
        editCallbacks.put(billId, edit);
        deleteCallbacks.put(billId, delete);
    }

    private void initTable() {
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setAlignmentX(LEFT_ALIGNMENT);
        int padding = Math.max(5, getWidth() / 100);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));

        // 1. 先添加表头（固定在顶部）
        JPanel headerRow = createHeaderRow();
        contentPanel.add(headerRow);
        contentPanel.add(Box.createVerticalStrut(padding)); // 表头与数据行之间的间距

        // 2. 再添加数据行（表头下方）
        for (Map.Entry<Integer, BillSingleResponse> entry : bills.entrySet()) {
            int rowIndex = entry.getKey();
            BillSingleResponse bill = entry.getValue();
            JPanel dataRow = createDataRow(rowIndex, bill);
            contentPanel.add(dataRow);
            contentPanel.add(Box.createVerticalStrut(padding)); // 数据行之间的间距
        }

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
    }

    // 创建表头行（单独提取方法，便于维护）
    private JPanel createHeaderRow() {
        int headerHeight = baseFontSize * 4; // 表头行高（略小于数据行，突出层级）
        JPanel headerRow = new JPanel(new GridLayout(1, 7, 1, 1));
        headerRow.setBackground(new Color(240, 240, 240)); // 表头灰色背景，与数据行区分
        headerRow.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        headerRow.setPreferredSize(new Dimension(0, headerHeight));

        // 表头文本（明确列含义）
        String[] headers = {
            "索引", 
            "时间", 
            "支出/收入", 
            "类型", 
            "金额/元", 
            "备注", 
            "操作"
        };

        for (String header : headers) {
            JLabel label = new JLabel(header, SwingConstants.CENTER);
            label.setFont(new Font("微软雅黑", Font.BOLD, baseFontSize)); // 表头加粗
            label.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            label.setVerticalAlignment(SwingConstants.CENTER); // 垂直居中
            headerRow.add(label);
        }
        return headerRow;
    }

    // 创建数据行（单独提取方法，分离表头与数据逻辑）
    private JPanel createDataRow(int rowIndex, BillSingleResponse bill) {
        int rowHeight = baseFontSize * 5; // 数据行高（略大于表头）
        JPanel row = new JPanel(new GridLayout(1, 7, 1, 1));
        row.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        row.setPreferredSize(new Dimension(0, rowHeight));
        row.setBackground(Color.WHITE);

        // 1. 索引列
        JLabel indexLabel = new JLabel(String.format("%03d", rowIndex + 1), SwingConstants.CENTER);
        indexLabel.setFont(new Font("微软雅黑", Font.PLAIN, baseFontSize));
        indexLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        indexLabel.setVerticalAlignment(SwingConstants.CENTER);
        row.add(indexLabel);

        // 2. 时间列
        JLabel timeLabel = new JLabel(
            "<html><center>" + bill.getBillTime().replace(" ", "<br/>") + "</center></html>",
            SwingConstants.CENTER
        );
        timeLabel.setFont(new Font("微软雅黑", Font.PLAIN, baseFontSize));
        timeLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        timeLabel.setVerticalAlignment(SwingConstants.CENTER);
        row.add(timeLabel);

        // 3. 收支类型列
        boolean isExpense = bill.getType() == -1;
        JLabel typeLabel = new JLabel(isExpense ? "支出" : "收入", SwingConstants.CENTER);
        typeLabel.setForeground(isExpense ? Color.RED : new Color(0, 160, 0));
        typeLabel.setFont(new Font("微软雅黑", Font.PLAIN, baseFontSize));
        typeLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        typeLabel.setVerticalAlignment(SwingConstants.CENTER);
        row.add(typeLabel);

        // 4. 分类列
        String cat = getCategoryName(bill.getCategoryId());
        JLabel catLabel = new JLabel(
            "<html><center>" + cat + "</center></html>",
            SwingConstants.CENTER
        );
        catLabel.setFont(new Font("微软雅黑", Font.PLAIN, baseFontSize));
        catLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        catLabel.setVerticalAlignment(SwingConstants.CENTER);
        row.add(catLabel);

        // 5. 金额列
        JLabel amountLabel = new JLabel(bill.getAmount().toPlainString() + " 元", SwingConstants.CENTER);
        amountLabel.setFont(new Font("微软雅黑", Font.PLAIN, baseFontSize));
        amountLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        amountLabel.setVerticalAlignment(SwingConstants.CENTER);
        row.add(amountLabel);

        // 6. 备注列
        String remark = bill.getRemark() != null ? bill.getRemark() : "无";
        EllipsisLabel remarkLabel = new EllipsisLabel(remark);
        remarkLabel.setHorizontalAlignment(SwingConstants.CENTER);
        remarkLabel.setFont(new Font("微软雅黑", Font.PLAIN, baseFontSize));
        remarkLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        remarkLabel.setVerticalAlignment(SwingConstants.CENTER);
        row.add(remarkLabel);

        // 7. 操作列
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
        actionPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        actionPanel.setBackground(Color.WHITE);
        actionPanel.setPreferredSize(new Dimension(0, rowHeight));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, baseFontSize/2, 0));
        btnPanel.setAlignmentX(CENTER_ALIGNMENT);
        btnPanel.setBackground(Color.WHITE);

        // 详情按钮
        JButton detailBtn = new JButton("详情");
        detailBtn.setIcon(loadIcon("/icons/search.png"));
        styleActionButton(detailBtn);
        detailBtn.addActionListener(e -> {
            System.out.println("点击了账单[" + bill.getBillId() + "]的详情按钮");
            if (detailCallbacks.containsKey(bill.getBillId())) {
                detailCallbacks.get(bill.getBillId()).actionPerformed(e);
            }
        });
        btnPanel.add(detailBtn);

        // 修改按钮
        JButton editBtn = new JButton("修改");
        editBtn.setIcon(loadIcon("/icons/edit.png"));
        styleActionButton(editBtn);
        editBtn.addActionListener(e -> {
            System.out.println("点击了账单[" + bill.getBillId() + "]的修改按钮");
            if (editCallbacks.containsKey(bill.getBillId())) {
                editCallbacks.get(bill.getBillId()).actionPerformed(e);
            }
        });
        btnPanel.add(editBtn);

        // 删除按钮
        JButton deleteBtn = new JButton("删除");
        deleteBtn.setIcon(loadIcon("/icons/delete.png"));
        deleteBtn.setForeground(Color.RED);
        styleActionButton(deleteBtn);
        deleteBtn.addActionListener(e -> {
            System.out.println("点击了账单[" + bill.getBillId() + "]的删除按钮");
            if (deleteCallbacks.containsKey(bill.getBillId())) {
                deleteCallbacks.get(bill.getBillId()).actionPerformed(e);
            }
        });
        btnPanel.add(deleteBtn);

        // 微调垂直位置，与其他列文本对齐
        actionPanel.add(Box.createVerticalGlue());
        actionPanel.add(btnPanel);
        actionPanel.add(Box.createVerticalStrut(baseFontSize / 2));

        row.add(actionPanel);
        return row;
    }

    private ImageIcon loadIcon(String path) {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(path));
            int iconSize = baseFontSize + 2;
            Image scaledImage = icon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            return null;
        }
    }

    private void styleActionButton(JButton btn) {
        btn.setFont(new Font("微软雅黑", Font.PLAIN, baseFontSize));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setForeground(new Color(0, 100, 255));
        int btnVPadding = baseFontSize / 2;
        int btnHPadding = baseFontSize / 3;
        btn.setBorder(BorderFactory.createEmptyBorder(btnVPadding, btnHPadding, btnVPadding, btnHPadding));
        btn.setVerticalAlignment(SwingConstants.CENTER);
        btn.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private String getCategoryName(int categoryId) {
        switch (categoryId) {
            case 1: return "购物";
            case 2: return "饮食外卖";
            case 3: return "交通";
            case 4: return "娱乐";
            case 5: return "工资收入";
            case 6: return "其他收入";
            default: return "未知分类";
        }
    }

    public static class EllipsisLabel extends JLabel {
        public EllipsisLabel(String text) {
            super(text);
            setToolTipText(text);
        }

        @Override
        protected void paintComponent(Graphics g) {
            String fullText = getText();
            if (fullText == null || fullText.isEmpty()) {
                super.paintComponent(g);
                return;
            }

            FontMetrics fm = g.getFontMetrics(getFont());
            int availableWidth = getWidth() - getInsets().left - getInsets().right;

            if (fm.stringWidth(fullText) > availableWidth) {
                String ellipsis = "…";
                int maxLen = fullText.length();
                while (maxLen > 0 && fm.stringWidth(fullText.substring(0, maxLen) + ellipsis) > availableWidth) {
                    maxLen--;
                }
                super.setText(fullText.substring(0, maxLen) + ellipsis);
            } else {
                super.setText(fullText);
            }

            super.paintComponent(g);
            super.setText(fullText);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Map<Integer, BillSingleResponse> mockBills = new HashMap<>();
            mockBills.put(0, new BillSingleResponse() {
                @Override
                public Integer getBillId() { return 1001; }
                @Override
                public String getBillTime() { return "2025-10-17 21:23"; }
                @Override
                public Integer getType() { return -1; }
                @Override
                public Integer getCategoryId() { return 2; }
                @Override
                public BigDecimal getAmount() { return new BigDecimal("35.5"); }
                @Override
                public String getRemark() { return "夜宵吃了顿火锅"; }
            });
            mockBills.put(1, new BillSingleResponse() {
                @Override
                public Integer getBillId() { return 1002; }
                @Override
                public String getBillTime() { return "2025-10-16 12:36"; }
                @Override
                public Integer getType() { return -1; }
                @Override
                public Integer getCategoryId() { return 2; }
                @Override
                public BigDecimal getAmount() { return new BigDecimal("17.3"); }
                @Override
                public String getRemark() { return "买汉堡"; }
            });

            JFrame frame = new JFrame("账单表格（表头固定顶部版）");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 400);
            frame.setLocationRelativeTo(null);

            BillTableComponent table = new BillTableComponent(mockBills);
            frame.setContentPane(table);
            frame.setVisible(true);
        });
    }
}
