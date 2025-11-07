package com.accountbook.frontend.component.dialog;

import com.accountbook.frontend.MainPage;
import com.accountbook.proxy.response.bill.BillSingleResponse;
import com.accountbook.proxy.response.category.CategorySingleResponse;
import com.accountbook.proxy.response.specific_type.SpecificTypeSingleResponse;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BillEditDialog extends JDialog {
    private final MainPage mainPage;
    private final BillSingleResponse originalBill;
    private JTextField timeField;
    private JComboBox<String> typeCombo;
    private JComboBox<CategorySingleResponse> categoryCombo;
    private JComboBox<SpecificTypeSingleResponse> specificTypeCombo;
    private JTextField amountField;
    private JTextArea remarkArea;

    public BillEditDialog(MainPage mainPage) {
        this(mainPage, null);
    }

    public BillEditDialog(MainPage mainPage, BillSingleResponse originalBill) {
        super(mainPage, true);
        this.mainPage = mainPage;
        this.originalBill = originalBill;
        initUI();
        setLocationRelativeTo(mainPage);
    }

    private void initUI() {
        setTitle(originalBill == null ? "新增账单" : "修改账单");
        setSize(400, 400);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 1. 时间字段
        formPanel.add(new JLabel("账单时间："));
        timeField = new JTextField();
        String defaultTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        timeField.setText(originalBill != null ? originalBill.getBillTime() : defaultTime);
        formPanel.add(timeField);

        // 2. 收支类型
        formPanel.add(new JLabel("收支类型："));
        typeCombo = new JComboBox<>(new String[]{"收入", "支出"});
        if (originalBill != null) {
            typeCombo.setSelectedItem(originalBill.getType() == 1 ? "收入" : "支出");
        }
        formPanel.add(typeCombo);

        // 3. 大类选择（添加自定义渲染器，显示名称）
        formPanel.add(new JLabel("大类："));
        categoryCombo = new JComboBox<>();
        // 自定义渲染器：显示CategorySingleResponse的name字段
        categoryCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof CategorySingleResponse) {
                    setText(((CategorySingleResponse) value).getName());
                }
                return this;
            }
        });
        mainPage.getCategoryMap().values().forEach(categoryCombo::addItem);
        if (originalBill != null) {
            categoryCombo.setSelectedItem(mainPage.getCategoryMap().get(originalBill.getCategoryId()));
        }
        categoryCombo.addActionListener(e -> refreshSpecificTypes());
        formPanel.add(categoryCombo);

        // 4. 具体类型选择（添加自定义渲染器，显示名称）
        formPanel.add(new JLabel("具体类型："));
        specificTypeCombo = new JComboBox<>();
        // 自定义渲染器：显示SpecificTypeSingleResponse的name字段
        specificTypeCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof SpecificTypeSingleResponse) {
                    setText(((SpecificTypeSingleResponse) value).getName());
                }
                return this;
            }
        });
        refreshSpecificTypes();
        if (originalBill != null) {
            specificTypeCombo.setSelectedItem(mainPage.getSpecificTypeMap().get(originalBill.getSpecificTypeId()));
        }
        formPanel.add(specificTypeCombo);

        // 5. 金额
        formPanel.add(new JLabel("金额（元）："));
        amountField = new JTextField();
        if (originalBill != null) {
            amountField.setText(originalBill.getAmount().toString());
        }
        formPanel.add(amountField);

        // 6. 备注
        formPanel.add(new JLabel("备注："));
        remarkArea = new JTextArea(3, 20);
        if (originalBill != null) {
            remarkArea.setText(originalBill.getRemark());
        }
        formPanel.add(new JScrollPane(remarkArea));

        add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton submitBtn = new JButton(originalBill == null ? "添加" : "修改");
        JButton cancelBtn = new JButton("取消");

        submitBtn.addActionListener(e -> submit());
        cancelBtn.addActionListener(e -> dispose());

        btnPanel.add(submitBtn);
        btnPanel.add(cancelBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void refreshSpecificTypes() {
        specificTypeCombo.removeAllItems();
        CategorySingleResponse selectedCategory = (CategorySingleResponse) categoryCombo.getSelectedItem();
        if (selectedCategory == null) return;

        mainPage.getSpecificTypeMap().values().stream()
                .filter(type -> type.getCategoryId().equals(selectedCategory.getCategoryId()))
                .forEach(specificTypeCombo::addItem);
    }

    private void submit() {
        try {
            String time = timeField.getText().trim();
            if (time.isEmpty()) {
                showError("请输入账单时间");
                return;
            }

            Integer type = typeCombo.getSelectedItem().equals("收入") ? 1 : -1;

            CategorySingleResponse category = (CategorySingleResponse) categoryCombo.getSelectedItem();
            if (category == null) {
                showError("请选择大类");
                return;
            }
            Integer categoryId = category.getCategoryId();

            SpecificTypeSingleResponse specificType = (SpecificTypeSingleResponse) specificTypeCombo.getSelectedItem();
            if (specificType == null) {
                showError("请选择具体类型");
                return;
            }
            Integer specificTypeId = specificType.getSpecificTypeId();

            BigDecimal amount;
            try {
                amount = new BigDecimal(amountField.getText().trim());
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    showError("金额必须大于0");
                    return;
                }
            } catch (Exception e) {
                showError("金额格式错误");
                return;
            }

            String remark = remarkArea.getText().trim();

            BillSingleResponse result;
            if (originalBill == null) {
                result = mainPage.getBillHelper().addBill(time, type, categoryId, specificTypeId, amount, remark);
                mainPage.addBill(result);
            } else {
                result = mainPage.getBillHelper().updateBill(
                        originalBill.getBillId(), time, type, categoryId, specificTypeId, amount, remark
                );
                mainPage.updateBill(result);
            }

            dispose();
            JOptionPane.showMessageDialog(this, originalBill == null ? "新增成功" : "修改成功");

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "错误", JOptionPane.ERROR_MESSAGE);
    }
}