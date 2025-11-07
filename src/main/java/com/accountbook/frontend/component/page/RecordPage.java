package com.accountbook.frontend.component.page;

import com.accountbook.frontend.MainPage;
import com.accountbook.frontend.component.PageDrawer;
import com.accountbook.proxy.response.bill.BillSingleResponse;
import com.accountbook.proxy.response.category.CategorySingleResponse;
import com.accountbook.proxy.response.specific_type.SpecificTypeSingleResponse;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.stream.Collectors;

public class RecordPage implements PageDrawer {
    private final MainPage mainPage;
    private JTextField dateField;
    private JTextField timeField;
    private JComboBox<String> typeCombo;
    private JComboBox<String> categoryCombo;
    private JComboBox<String> specificCombo;
    private JTextField amountField;
    private JTextField remarkField;
    private Integer editingBillId = null; // 编辑时的账单ID

    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");

    public RecordPage(MainPage mainPage) {
        this.mainPage = mainPage;
    }

    @Override
    public void draw(JPanel contentPanel) {
        contentPanel.removeAll();
        contentPanel.setLayout(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // 1. 时间 - 年月日
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(new JLabel("时间（年月日）："), gbc);
        gbc.gridx = 1;
        dateField = new JTextField(15);
        dateField.setText("例如 2025-10-22");
        dateField.setForeground(Color.GRAY);
        addFocusListener(dateField, "例如 2025-10-22");
        dateField.setToolTipText("请输入格式：yyyy-MM-dd");
        contentPanel.add(dateField, gbc);

        // 2. 时间 - 时分
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(new JLabel("时间（时分）："), gbc);
        gbc.gridx = 1;
        timeField = new JTextField(15);
        timeField.setText("例如 22:12");
        timeField.setForeground(Color.GRAY);
        addFocusListener(timeField, "例如 22:12");
        timeField.setToolTipText("请输入格式：HH:mm（24小时制）");
        contentPanel.add(timeField, gbc);

        // 3. 收支类型
        gbc.gridx = 0;
        gbc.gridy = 2;
        contentPanel.add(new JLabel("收入/支出："), gbc);
        gbc.gridx = 1;
        String[] typeOptions = {"收入", "支出"};
        typeCombo = new JComboBox<>(typeOptions);
        contentPanel.add(typeCombo, gbc);

        // 4. 消费/收入大类
        gbc.gridx = 0;
        gbc.gridy = 3;
        contentPanel.add(new JLabel("类型（大类）："), gbc);
        gbc.gridx = 1;
        categoryCombo = new JComboBox<>();
        loadCategoryOptions();
        contentPanel.add(categoryCombo, gbc);

        // 新增大类按钮
        gbc.gridx = 2;
        JButton addCategoryBtn = new JButton("+ 新增大类");
        addCategoryBtn.addActionListener(e -> showAddCategoryDialog());
        contentPanel.add(addCategoryBtn, gbc);

        // 5. 具体类型
        gbc.gridx = 0;
        gbc.gridy = 4;
        contentPanel.add(new JLabel("类型（具体）："), gbc);
        gbc.gridx = 1;
        specificCombo = new JComboBox<>();
        categoryCombo.addActionListener(e -> updateSpecificOptionsByCategory());
        contentPanel.add(specificCombo, gbc);

        // 新增具体类型按钮
        gbc.gridx = 2;
        JButton addSpecificBtn = new JButton("+ 新增具体类型");
        addSpecificBtn.addActionListener(e -> showAddSpecificTypeDialog());
        contentPanel.add(addSpecificBtn, gbc);

        // 6. 金额/元
        gbc.gridx = 0;
        gbc.gridy = 5;
        contentPanel.add(new JLabel("金额/元："), gbc);
        gbc.gridx = 1;
        amountField = new JTextField(15);
        amountField.setText("例如 58.50");
        amountField.setForeground(Color.GRAY);
        addFocusListener(amountField, "例如 58.50");
        contentPanel.add(amountField, gbc);

        // 7. 备注
        gbc.gridx = 0;
        gbc.gridy = 6;
        contentPanel.add(new JLabel("备注："), gbc);
        gbc.gridx = 1;
        remarkField = new JTextField(15);
        remarkField.setText("例如 晚餐聚餐");
        remarkField.setForeground(Color.GRAY);
        addFocusListener(remarkField, "例如 晚餐聚餐");
        contentPanel.add(remarkField, gbc);

        // 8. 确定按钮
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton confirmBtn = new JButton(editingBillId == null ? "添加" : "修改");
        confirmBtn.addActionListener(e -> handleConfirm());
        contentPanel.add(confirmBtn, gbc);

        contentPanel.revalidate();
    }

    private void addFocusListener(JTextField field, String hint) {
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(hint)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(hint);
                    field.setForeground(Color.GRAY);
                }
            }
        });
    }

    // 加载大类选项
    private void loadCategoryOptions() {
        categoryCombo.removeAllItems();
        mainPage.getCategoryMap().values().forEach(category -> 
            categoryCombo.addItem(category.getName())
        );
        categoryCombo.setComponentPopupMenu(createCategoryPopupMenu());
    }

    // 加载具体类型选项
    private void updateSpecificOptionsByCategory() {
        specificCombo.removeAllItems();
        String selectedCategory = (String) categoryCombo.getSelectedItem();
        if (selectedCategory == null) return;

        int categoryId = mainPage.getCategoryMap().values().stream()
                .filter(c -> c.getName().equals(selectedCategory))
                .findFirst()
                .map(CategorySingleResponse::getCategoryId)
                .orElse(-1);

        mainPage.getSpecificTypeMap().values().stream()
                .filter(type -> type.getCategoryId() == categoryId)
                .forEach(type -> specificCombo.addItem(type.getName()));

        specificCombo.setComponentPopupMenu(createSpecificTypePopupMenu());
    }

    // 新增大类对话框
    private void showAddCategoryDialog() {
        String name = JOptionPane.showInputDialog("请输入新大类名称：");
        if (name == null || name.trim().isEmpty()) return;

        try {
            CategorySingleResponse newCategory = mainPage.getCategoryHelper().addCategory(name);
            mainPage.addCategory(newCategory);
            loadCategoryOptions();
            JOptionPane.showMessageDialog(null, "新增大类成功！");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "新增失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 新增具体类型对话框
    private void showAddSpecificTypeDialog() {
        String categoryName = (String) categoryCombo.getSelectedItem();
        if (categoryName == null) {
            JOptionPane.showMessageDialog(null, "请先选择大类！");
            return;
        }

        String name = JOptionPane.showInputDialog("请输入新具体类型名称：");
        if (name == null || name.trim().isEmpty()) return;

        try {
            int categoryId = mainPage.getCategoryMap().values().stream()
                    .filter(c -> c.getName().equals(categoryName))
                    .findFirst()
                    .map(CategorySingleResponse::getCategoryId)
                    .orElse(-1);

            SpecificTypeSingleResponse newType = mainPage.getSpecificTypeHelper()
                    .addSpecificType(name,categoryId);
            mainPage.addSpecificType(newType);
            updateSpecificOptionsByCategory();
            JOptionPane.showMessageDialog(null, "新增具体类型成功！");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "新增失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 大类右键菜单
    private JPopupMenu createCategoryPopupMenu() {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem editItem = new JMenuItem("修改");
        editItem.addActionListener(e -> editCategory());
        JMenuItem deleteItem = new JMenuItem("删除");
        deleteItem.addActionListener(e -> deleteCategory());
        menu.add(editItem);
        menu.add(deleteItem);
        return menu;
    }

    // 修改大类
    private void editCategory() {
        String oldName = (String) categoryCombo.getSelectedItem();
        if (oldName == null) return;

        String newName = JOptionPane.showInputDialog("请输入新名称：", oldName);
        if (newName == null || newName.trim().isEmpty() || newName.equals(oldName)) return;

        try {
            CategorySingleResponse category = mainPage.getCategoryMap().values().stream()
                    .filter(c -> c.getName().equals(oldName))
                    .findFirst().orElse(null);
            if (category == null) return;

            CategorySingleResponse updated = mainPage.getCategoryHelper()
                    .updateCategory(category.getCategoryId(), newName);
            mainPage.updateCategory(updated);
            loadCategoryOptions();
            JOptionPane.showMessageDialog(null, "修改成功！");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "修改失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 删除大类
    private void deleteCategory() {
        String name = (String) categoryCombo.getSelectedItem();
        if (name == null) return;

        CategorySingleResponse category = mainPage.getCategoryMap().values().stream()
                .filter(c -> c.getName().equals(name))
                .findFirst().orElse(null);
        if (category == null || category.getCategoryId() == 1) { // 1号类型不可删除
            JOptionPane.showMessageDialog(null, "系统默认类型不可删除！");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                null,
                "删除后，属于该类型的账单将改为'无'，是否继续？",
                "确认删除",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            // 更新关联账单
            mainPage.getBillHelper().updateBillsCategory(category.getCategoryId(), 1);
            // 删除大类
            mainPage.getCategoryHelper().deleteCategory(category.getCategoryId());
            mainPage.deleteCategory(category.getCategoryId());
            // 刷新
            loadCategoryOptions();
            updateSpecificOptionsByCategory();
            JOptionPane.showMessageDialog(null, "删除成功！");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "删除失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 具体类型右键菜单
    private JPopupMenu createSpecificTypePopupMenu() {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem editItem = new JMenuItem("修改");
        editItem.addActionListener(e -> editSpecificType());
        JMenuItem deleteItem = new JMenuItem("删除");
        deleteItem.addActionListener(e -> deleteSpecificType());
        menu.add(editItem);
        menu.add(deleteItem);
        return menu;
    }

    // 修改具体类型
    private void editSpecificType() {
        String oldName = (String) specificCombo.getSelectedItem();
        if (oldName == null) return;

        String newName = JOptionPane.showInputDialog("请输入新名称：", oldName);
        if (newName == null || newName.trim().isEmpty() || newName.equals(oldName)) return;

        try {
            SpecificTypeSingleResponse type = mainPage.getSpecificTypeMap().values().stream()
                    .filter(t -> t.getName().equals(oldName))
                    .findFirst().orElse(null);
            if (type == null) return;

            SpecificTypeSingleResponse updated = mainPage.getSpecificTypeHelper()
                    .updateSpecificType(type.getSpecificTypeId(), newName,type.getCategoryId());
            mainPage.updateSpecificType(updated);
            updateSpecificOptionsByCategory();
            JOptionPane.showMessageDialog(null, "修改成功！");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "修改失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 删除具体类型
    private void deleteSpecificType() {
        String name = (String) specificCombo.getSelectedItem();
        if (name == null) return;

        SpecificTypeSingleResponse type = mainPage.getSpecificTypeMap().values().stream()
                .filter(t -> t.getName().equals(name))
                .findFirst().orElse(null);
        if (type == null) return;

        int confirm = JOptionPane.showConfirmDialog(
                null,
                "删除后，属于该类型的账单将改为'无'，是否继续？",
                "确认删除",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            // 更新关联账单
            mainPage.getBillHelper().updateBillsSpecificType(type.getSpecificTypeId(), 1);
            // 删除具体类型
            mainPage.getSpecificTypeHelper().deleteSpecificType(type.getSpecificTypeId());
            mainPage.deleteSpecificType(type.getSpecificTypeId());
            // 刷新
            updateSpecificOptionsByCategory();
            JOptionPane.showMessageDialog(null, "删除成功！");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "删除失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 预填账单数据（编辑时使用）
    public void fillBillData(BillSingleResponse bill) {
        this.editingBillId = bill.getBillId();
        
        // 填充日期时间
        String[] timeParts = bill.getBillTime().split(" ");
        dateField.setText(timeParts[0]);
        dateField.setForeground(Color.BLACK);
        timeField.setText(timeParts[1].substring(0, 5));
        timeField.setForeground(Color.BLACK);

        // 填充收支类型
        typeCombo.setSelectedItem(bill.getType() == 1 ? "收入" : "支出");

        // 填充大类和具体类型
        String categoryName = mainPage.getCategoryMap().get(bill.getCategoryId()).getName();
        categoryCombo.setSelectedItem(categoryName);
        updateSpecificOptionsByCategory();
        
        String specificName = mainPage.getSpecificTypeMap().get(bill.getSpecificTypeId()).getName();
        specificCombo.setSelectedItem(specificName);

        // 填充金额和备注
        amountField.setText(bill.getAmount().toString());
        amountField.setForeground(Color.BLACK);
        remarkField.setText(bill.getRemark() != null ? bill.getRemark() : "");
        remarkField.setForeground(Color.BLACK);
    }

    // 处理添加/修改确认
    private void handleConfirm() {
        try {
            String billTime = validateAndGetBillTime();
            if (billTime == null) return;

            int type = "收入".equals(typeCombo.getSelectedItem()) ? 1 : -1;

            String categoryName = (String) categoryCombo.getSelectedItem();
            int categoryId = mainPage.getCategoryMap().values().stream()
                    .filter(c -> c.getName().equals(categoryName))
                    .findFirst()
                    .map(CategorySingleResponse::getCategoryId)
                    .orElse(-1);

            String specificName = (String) specificCombo.getSelectedItem();
            int specificTypeId = mainPage.getSpecificTypeMap().values().stream()
                    .filter(t -> t.getName().equals(specificName))
                    .findFirst()
                    .map(SpecificTypeSingleResponse::getSpecificTypeId)
                    .orElse(-1);

            String amountStr = amountField.getText().trim().replace("例如 ", "");
            if (amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "请输入金额", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            BigDecimal amount = new BigDecimal(amountStr);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(null, "金额必须大于0", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String remark = remarkField.getText().trim().replace("例如 ", "");

            if (editingBillId == null) {
                // 添加新账单
                BillSingleResponse newBill = mainPage.getBillHelper().addBill(
                        billTime, type, categoryId, specificTypeId, amount, remark
                );
                mainPage.addBill(newBill);
                JOptionPane.showMessageDialog(null, "添加成功！ID：" + newBill.getBillId());
            } else {
                // 修改现有账单
                BillSingleResponse updated = mainPage.getBillHelper().updateBill(
                        editingBillId, billTime, type, categoryId, specificTypeId, amount, remark
                );
                mainPage.updateBill(updated);
                JOptionPane.showMessageDialog(null, "修改成功！");
                editingBillId = null; // 重置编辑状态
            }

            clearForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "操作失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String validateAndGetBillTime() {
        String dateStr = dateField.getText().trim().replace("例如 ", "").trim();
        if (dateStr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "请输入年月日", "错误", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        try {
            dateFormatter.parse(dateStr);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null, "年月日格式错误（yyyy-MM-dd）", "错误", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        String timeStr = timeField.getText().trim().replace("例如 ", "").trim();
        if (timeStr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "请输入时分", "错误", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        try {
            timeFormatter.parse(timeStr);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null, "时分格式错误（HH:mm）", "错误", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        return dateStr + " " + timeStr + ":00";
    }

    private void clearForm() {
        dateField.setText("例如 2025-10-22");
        dateField.setForeground(Color.GRAY);
        timeField.setText("例如 22:12");
        timeField.setForeground(Color.GRAY);
        typeCombo.setSelectedIndex(0);
        categoryCombo.setSelectedIndex(0);
        updateSpecificOptionsByCategory();
        amountField.setText("例如 58.50");
        amountField.setForeground(Color.GRAY);
        remarkField.setText("例如 晚餐聚餐");
        remarkField.setForeground(Color.GRAY);
    }
}