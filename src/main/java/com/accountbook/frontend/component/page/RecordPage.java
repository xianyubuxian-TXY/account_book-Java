package com.accountbook.frontend.component.page;

import com.accountbook.frontend.MainPage;
import com.accountbook.frontend.component.PageDrawer;
import com.accountbook.proxy.response.bill.BillSingleResponse;
import com.accountbook.proxy.response.category.CategorySingleResponse;
import com.accountbook.proxy.response.specific_type.SpecificTypeSingleResponse;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date; // 导入 Date 类

public class RecordPage implements PageDrawer {

    private final MainPage mainPage;
    private JPanel rootPanel;
    private JTextField dateField;
    private JTextField timeField;
    private JComboBox<ComboItem> typeCombo;
    private JComboBox<ComboItem> categoryCombo;
    private JComboBox<ComboItem> specificCombo;
    private JTextField amountField;
    private JTextField remarkField;
    private Integer editingBillId = null;

    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");

    private static final String DEFAULT_NAME = "无";
    private static final int SYSTEM_DEFAULT_ID = 1;

    private static final String HINT_DATE   = "例如 2025-10-22";
    private static final String HINT_AMOUNT = "例如 58.50";
    private static final String HINT_REMARK = "例如 晚餐聚餐";

    /** 与 HomePage 约定的面板标记 key */
    private static final String PAGE_KEY = "currentPage";

    private static class ComboItem {
        final int id;
        final String name;
        ComboItem(int id, String name) { this.id = id; this.name = name; }
        @Override public String toString() { return name; }
    }

    public RecordPage(MainPage mainPage) {
        this.mainPage = mainPage;
    }

    @Override
    public void draw(JPanel contentPanel) {
        this.rootPanel = contentPanel;

        // 标记当前显示的是录入页
        contentPanel.putClientProperty(PAGE_KEY, "RECORD");

        contentPanel.removeAll();
        contentPanel.setLayout(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // 时间（年月日）
        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(new JLabel("时间（年月日）："), gbc);
        gbc.gridx = 1;
        dateField = new JTextField(15);
        dateField.setText(HINT_DATE);
        dateField.setForeground(Color.GRAY);
        addHint(dateField, HINT_DATE);
        contentPanel.add(dateField, gbc);

        // 时间（时分）
        gbc.gridx = 0; gbc.gridy = 1;
        contentPanel.add(new JLabel("时间（时分）："), gbc);
        gbc.gridx = 1;
        timeField = new JTextField(15);
        // --- MODIFICATION START ---
        // 自动获取系统当前时间并填入
        timeField.setText(timeFormatter.format(new Date()));
        timeField.setForeground(Color.BLACK); // 自动填入后，颜色设为黑色
        // 移除 addHint 调用，因为不再需要提示
        // addHint(timeField, HINT_TIME);
        // --- MODIFICATION END ---
        contentPanel.add(timeField, gbc);

        // 收支类型
        gbc.gridx = 0; gbc.gridy = 2;
        contentPanel.add(new JLabel("收入/支出："), gbc);
        gbc.gridx = 1;
        typeCombo = new JComboBox<>();
        typeCombo.addItem(new ComboItem(1, "收入"));
        typeCombo.addItem(new ComboItem(-1, "支出"));
        contentPanel.add(typeCombo, gbc);

        // 大类
        gbc.gridx = 0; gbc.gridy = 3;
        contentPanel.add(new JLabel("类型（大类）："), gbc);
        gbc.gridx = 1;
        categoryCombo = new JComboBox<>();
        loadCategoryOptions();
        contentPanel.add(categoryCombo, gbc);

        // 具体类型
        gbc.gridx = 0; gbc.gridy = 4;
        contentPanel.add(new JLabel("类型（具体）："), gbc);
        gbc.gridx = 1;
        specificCombo = new JComboBox<>();
        categoryCombo.addActionListener(e -> updateSpecificOptionsByCategory());
        contentPanel.add(specificCombo, gbc);

        // 金额
        gbc.gridx = 0; gbc.gridy = 5;
        contentPanel.add(new JLabel("金额/元："), gbc);
        gbc.gridx = 1;
        amountField = new JTextField(15);
        amountField.setText(HINT_AMOUNT);
        amountField.setForeground(Color.GRAY);
        addHint(amountField, HINT_AMOUNT);
        contentPanel.add(amountField, gbc);

        // 备注
        gbc.gridx = 0; gbc.gridy = 6;
        contentPanel.add(new JLabel("备注："), gbc);
        gbc.gridx = 1;
        remarkField = new JTextField(15);
        remarkField.setText(HINT_REMARK);
        remarkField.setForeground(Color.GRAY);
        addHint(remarkField, HINT_REMARK);
        contentPanel.add(remarkField, gbc);

        // 提交按钮（仅操作本页，不跳转）
        gbc.gridx = 0; gbc.gridy = 7;
        gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        JButton confirmBtn = new JButton(editingBillId == null ? "添加" : "修改");
        confirmBtn.addActionListener(e -> handleConfirm());
        contentPanel.add(confirmBtn, gbc);

        updateSpecificOptionsByCategory();
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /* 占位符工具 */

    private void addHint(JTextField field, String hint) {
        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (field.getText().equals(hint)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
            @Override public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(hint);
                    field.setForeground(Color.GRAY);
                }
            }
        });
    }

    // --- MODIFICATION START ---
    // isHint 方法不再需要检查 timeField 的 HINT_TIME
    private boolean isHint(JTextField field, String hint) {
        // 对于 timeField，因为它自动填充，所以不应该被视为 HINT
        // 除非它的内容是空的，或者颜色是灰色（但我们设置了黑色）
        // 这里的逻辑可以简化为只检查是否是传入的 hint
        return hint.equals(field.getText()) && Color.GRAY.equals(field.getForeground());
    }
    // --- MODIFICATION END ---

    /* 下拉加载和右键菜单 */

    private void loadCategoryOptions() {
        DefaultComboBoxModel<ComboItem> model = new DefaultComboBoxModel<>();
        mainPage.getCategoryMap().values().stream()
                .sorted(Comparator.comparingInt(CategorySingleResponse::getCategoryId))
                .forEach(cat -> model.addElement(new ComboItem(cat.getCategoryId(), cat.getName())));
        categoryCombo.setModel(model);
        categoryCombo.setComponentPopupMenu(createCategoryPopupMenu());
        selectDefault(model, categoryCombo);
    }

    private void updateSpecificOptionsByCategory() {
        specificCombo.removeAllItems();
        ComboItem selected = (ComboItem) categoryCombo.getSelectedItem();
        if (selected == null) return;

        DefaultComboBoxModel<ComboItem> model = new DefaultComboBoxModel<>();
        mainPage.getSpecificTypeMap().values().stream()
                .filter(t -> t.getCategoryId() == selected.id)
                .sorted(Comparator.comparingInt(SpecificTypeSingleResponse::getSpecificTypeId))
                .forEach(t -> model.addElement(new ComboItem(t.getSpecificTypeId(), t.getName())));
        specificCombo.setModel(model);
        specificCombo.setComponentPopupMenu(createSpecificTypePopupMenu());
        selectDefault(model, specificCombo);
    }

    private void selectDefault(DefaultComboBoxModel<ComboItem> model, JComboBox<ComboItem> combo) {
        int index = -1;
        for (int i = 0; i < model.getSize(); i++) {
            if (DEFAULT_NAME.equals(model.getElementAt(i).name)) { index = i; break; }
        }
        if (index >= 0) combo.setSelectedIndex(index);
        else if (model.getSize() > 0) combo.setSelectedIndex(0);
    }

    private JPopupMenu createCategoryPopupMenu() {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem addItem = new JMenuItem("新增");
        addItem.addActionListener(e -> showAddCategoryDialog());
        menu.add(addItem);

        JMenuItem editItem = new JMenuItem("修改");
        editItem.addActionListener(e -> editCategory());
        menu.add(editItem);

        JMenuItem deleteItem = new JMenuItem("删除");
        deleteItem.addActionListener(e -> deleteCategory());
        menu.add(deleteItem);

        return menu;
    }

    private JPopupMenu createSpecificTypePopupMenu() {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem addItem = new JMenuItem("新增");
        addItem.addActionListener(e -> showAddSpecificTypeDialog());
        menu.add(addItem);

        JMenuItem editItem = new JMenuItem("修改");
        editItem.addActionListener(e -> editSpecificType());
        menu.add(editItem);

        JMenuItem deleteItem = new JMenuItem("删除");
        deleteItem.addActionListener(e -> deleteSpecificType());
        menu.add(deleteItem);

        return menu;
    }

    /* 大类/具体类型操作：仅更新缓存+本页控件 */

    private void showAddCategoryDialog() {
        String name = JOptionPane.showInputDialog("请输入新大类名称：");
        if (name == null || name.trim().isEmpty()) return;
        try {
            CategorySingleResponse newCategory = mainPage.getCategoryHelper().addCategory(name.trim());
            mainPage.addCategory(newCategory);

            SpecificTypeSingleResponse defaultSpecific = ensureDefaultSpecificType(newCategory.getCategoryId());

            loadCategoryOptions();
            selectComboById(categoryCombo, newCategory.getCategoryId());
            updateSpecificOptionsByCategory();
            selectComboById(specificCombo, defaultSpecific.getSpecificTypeId());

            JOptionPane.showMessageDialog(null, "新增大类成功，并已创建默认具体类型「无」！");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "新增失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private SpecificTypeSingleResponse ensureDefaultSpecificType(int categoryId) throws Exception {
        SpecificTypeSingleResponse existed = mainPage.getSpecificTypeMap().values().stream()
                .filter(t -> t.getCategoryId() == categoryId && DEFAULT_NAME.equals(t.getName()))
                .findFirst().orElse(null);
        if (existed != null) return existed;

        SpecificTypeSingleResponse created =
                mainPage.getSpecificTypeHelper().addSpecificType(DEFAULT_NAME, categoryId);
        mainPage.addSpecificType(created);
        return created;
    }

    private void showAddSpecificTypeDialog() {
        ComboItem categoryItem = (ComboItem) categoryCombo.getSelectedItem();
        if (categoryItem == null) {
            JOptionPane.showMessageDialog(null, "请先选择大类！");
            return;
        }
        String name = JOptionPane.showInputDialog("请输入新具体类型名称：");
        if (name == null || name.trim().isEmpty()) return;
        try {
            SpecificTypeSingleResponse newType =
                    mainPage.getSpecificTypeHelper().addSpecificType(name.trim(), categoryItem.id);
            mainPage.addSpecificType(newType);
            updateSpecificOptionsByCategory();
            JOptionPane.showMessageDialog(null, "新增具体类型成功！");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "新增失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editCategory() {
        ComboItem item = (ComboItem) categoryCombo.getSelectedItem();
        if (item == null) return;
        String newName = JOptionPane.showInputDialog("请输入新名称：", item.name);
        if (newName == null || newName.trim().isEmpty() || newName.equals(item.name)) return;
        try {
            CategorySingleResponse updated =
                    mainPage.getCategoryHelper().updateCategory(item.id, newName.trim());
            mainPage.updateCategory(updated);
            loadCategoryOptions();
            updateSpecificOptionsByCategory();
            JOptionPane.showMessageDialog(null, "修改成功！");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "修改失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editSpecificType() {
        ComboItem item = (ComboItem) specificCombo.getSelectedItem();
        ComboItem categoryItem = (ComboItem) categoryCombo.getSelectedItem();
        if (item == null || categoryItem == null) return;
        String newName = JOptionPane.showInputDialog("请输入新名称：", item.name);
        if (newName == null || newName.trim().isEmpty() || newName.equals(item.name)) return;
        try {
            SpecificTypeSingleResponse updated =
                    mainPage.getSpecificTypeHelper().updateSpecificType(item.id, newName.trim(), categoryItem.id);
            mainPage.updateSpecificType(updated);
            updateSpecificOptionsByCategory();
            JOptionPane.showMessageDialog(null, "修改成功！");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "修改失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 删除大类（Category）
     * 删除前提示：账单和预算都会迁移到默认分类及默认具体类型“无”
     */
    private void deleteCategory() {
        ComboItem item = (ComboItem) categoryCombo.getSelectedItem();
        if (item == null) return;

        // 系统默认分类禁止删除
        if (item.id == SYSTEM_DEFAULT_ID) {
            JOptionPane.showMessageDialog(null, "系统默认分类不可删除！");
            return;
        }

        // 确认对话框
        int confirm = JOptionPane.showConfirmDialog(
                null,
                "删除后：\n" +
                "① 属于该大类及其下具体类型的账单将迁移到默认分类的'无'类型；\n" +
                "② 属于该大类的预算会合并或迁移到默认分类。\n\n" +
                "是否继续？",
                "确认删除",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            // 调用后端删除接口
            mainPage.getCategoryHelper().deleteCategory(item.id);

            // 清理前端缓存
            mainPage.getSpecificTypeMap().values().removeIf(t -> t.getCategoryId() == item.id);
            mainPage.deleteCategory(item.id);

            // 刷新下拉选项
            loadCategoryOptions();
            updateSpecificOptionsByCategory();

            // ✅ 调整：删除后强制从后端刷新缓存，避免“未知类型”
            mainPage.reloadAllData();
            JOptionPane.showMessageDialog(null, "删除成功！相关账单与预算已迁移到默认分类。");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "删除失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 删除具体类型（SpecificType）
     * 删除前提示：账单具体类型会改为“无”，不会影响分类和预算
     */
    private void deleteSpecificType() {
        ComboItem item = (ComboItem) specificCombo.getSelectedItem();
        if (item == null) return;

        // ✅ 新增保护逻辑：禁止删除内置“无”类型（ID=1）
        if (item.id == SYSTEM_DEFAULT_ID) {
            JOptionPane.showMessageDialog(null, "内置类型“无”(ID=1)不可删除！");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                null,
                "删除后：\n" +
                "① 属于该具体类型的账单将自动改为默认类型“无”；\n" +
                "② 不影响所属分类及预算。\n\n" +
                "是否继续？",
                "确认删除",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            // 调用后端删除接口
            mainPage.getSpecificTypeHelper().deleteSpecificType(item.id);

            // 从前端内存中移除该具体类型
            mainPage.deleteSpecificType(item.id);

            // 刷新下拉框
            updateSpecificOptionsByCategory();

            // ✅ 调整：删除后强制从后端刷新缓存，避免“未知类型”
            mainPage.reloadAllData();

            JOptionPane.showMessageDialog(null, "删除成功！相关账单已迁移到默认类型“无”。");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "删除失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /* 添加/修改账单：只清表单，不跳转 */

    private void handleConfirm() {
        try {
            String billTime = validateAndGetBillTime();
            if (billTime == null) return;

            ComboItem typeItem = (ComboItem) typeCombo.getSelectedItem();
            ComboItem categoryItem = (ComboItem) categoryCombo.getSelectedItem();
            ComboItem specificItem = (ComboItem) specificCombo.getSelectedItem();
            if (typeItem == null || categoryItem == null || specificItem == null) {
                JOptionPane.showMessageDialog(null, "请选择收支类型/大类/具体类型", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (isHint(amountField, HINT_AMOUNT)) { // isHint 已经更新，这里只检查 HINT_AMOUNT
                JOptionPane.showMessageDialog(null, "请输入金额", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String amountStr = amountField.getText().trim();
            if (amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "请输入金额", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            BigDecimal amount;
            try {
                amount = new BigDecimal(amountStr);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "金额格式不正确", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(null, "金额必须大于0", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String remark = (isHint(remarkField, HINT_REMARK) || remarkField.getText().trim().isEmpty())
                    ? DEFAULT_NAME
                    : remarkField.getText().trim();

            if (editingBillId == null) {
                BillSingleResponse newBill = mainPage.getBillHelper().addBill(
                        billTime, typeItem.id, categoryItem.id, specificItem.id, amount, remark);
                mainPage.addBill(newBill);
                JOptionPane.showMessageDialog(null, "添加成功！");
            } else {
                BillSingleResponse updated = mainPage.getBillHelper().updateBill(
                        editingBillId, billTime, typeItem.id, categoryItem.id, specificItem.id, amount, remark);
                mainPage.updateBill(updated);
                JOptionPane.showMessageDialog(null, "修改成功！");
                editingBillId = null;
            }

            // --- MODIFICATION START ---
            clearForm(); // 清除表单并重新加载当前时间
            // --- MODIFICATION END ---
            loadCategoryOptions();
            updateSpecificOptionsByCategory();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "操作失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String validateAndGetBillTime() {
        if (isHint(dateField, HINT_DATE)) {
            JOptionPane.showMessageDialog(null, "请输入年月日", "错误", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        // --- MODIFICATION START ---
        // timeField 不再有 HINT_TIME，所以直接检查是否为空
        String timeStr = timeField.getText().trim();
        if (timeStr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "请输入时分", "错误", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        // --- MODIFICATION END ---
        String dateStr = dateField.getText().trim();
        try {
            dateFormatter.parse(dateStr);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null, "年月日格式错误（yyyy-MM-dd）", "错误", JOptionPane.ERROR_MESSAGE);
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
        dateField.setText(HINT_DATE); dateField.setForeground(Color.GRAY);
        // --- MODIFICATION START ---
        timeField.setText(timeFormatter.format(new Date())); // 清除时自动填充当前时间
        timeField.setForeground(Color.BLACK); // 颜色设为黑色
        // --- MODIFICATION END ---
        amountField.setText(HINT_AMOUNT); amountField.setForeground(Color.GRAY);
        remarkField.setText(HINT_REMARK); remarkField.setForeground(Color.GRAY);
        for (int i = 0; i < typeCombo.getItemCount(); i++) {
            if (typeCombo.getItemAt(i).id == 1) { typeCombo.setSelectedIndex(i); break; }
        }
    }

    private void selectComboById(JComboBox<ComboItem> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).id == id) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }
}
