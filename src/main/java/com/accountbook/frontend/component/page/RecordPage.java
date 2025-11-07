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

public class RecordPage implements PageDrawer {
    private final MainPage mainPage;
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

    // 占位符常量
    private static final String HINT_DATE   = "例如 2025-10-22";
    private static final String HINT_TIME   = "例如 22:12";
    private static final String HINT_AMOUNT = "例如 58.50";
    private static final String HINT_REMARK = "例如 晚餐聚餐";

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
        contentPanel.removeAll();
        contentPanel.setLayout(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // 时间（日期）
        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(new JLabel("时间（年月日）："), gbc);
        gbc.gridx = 1;
        dateField = new JTextField(15);
        dateField.setText(HINT_DATE);
        dateField.setForeground(Color.GRAY);
        addFocusListener(dateField, HINT_DATE);
        contentPanel.add(dateField, gbc);

        // 时间（时分）
        gbc.gridx = 0; gbc.gridy = 1;
        contentPanel.add(new JLabel("时间（时分）："), gbc);
        gbc.gridx = 1;
        timeField = new JTextField(15);
        timeField.setText(HINT_TIME);
        timeField.setForeground(Color.GRAY);
        addFocusListener(timeField, HINT_TIME);
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
        addFocusListener(amountField, HINT_AMOUNT);
        contentPanel.add(amountField, gbc);

        // 备注
        gbc.gridx = 0; gbc.gridy = 6;
        contentPanel.add(new JLabel("备注："), gbc);
        gbc.gridx = 1;
        remarkField = new JTextField(15);
        remarkField.setText(HINT_REMARK);
        remarkField.setForeground(Color.GRAY);
        addFocusListener(remarkField, HINT_REMARK);
        contentPanel.add(remarkField, gbc);

        // 提交按钮
        gbc.gridx = 0; gbc.gridy = 7;
        gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        JButton confirmBtn = new JButton(editingBillId == null ? "添加" : "修改");
        confirmBtn.addActionListener(e -> handleConfirm());
        contentPanel.add(confirmBtn, gbc);

        updateSpecificOptionsByCategory();
        contentPanel.revalidate();
    }

    private void addFocusListener(JTextField field, String hint) {
        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (field.getText().equals(hint)) { field.setText(""); field.setForeground(Color.BLACK); }
            }
            @Override public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) { field.setText(hint); field.setForeground(Color.GRAY); }
            }
        });
    }

    private boolean isHint(JTextField field, String hint) {
        return hint.equals(field.getText()) || Color.GRAY.equals(field.getForeground());
    }

    // ------------------------- 下拉加载 -------------------------

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
        int selectIndex = -1;
        for (int i = 0; i < model.getSize(); i++) {
            if (DEFAULT_NAME.equals(model.getElementAt(i).name)) { selectIndex = i; break; }
        }
        if (selectIndex >= 0) combo.setSelectedIndex(selectIndex);
        else if (model.getSize() > 0) combo.setSelectedIndex(0);
    }

    // ------------------------- 右键菜单 -------------------------

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

    // ------------------------- 新增/修改 -------------------------

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
        SpecificTypeSingleResponse created = mainPage.getSpecificTypeHelper()
                .addSpecificType(DEFAULT_NAME, categoryId);
        mainPage.addSpecificType(created);
        return created;
    }

    private void showAddSpecificTypeDialog() {
        ComboItem categoryItem = (ComboItem) categoryCombo.getSelectedItem();
        if (categoryItem == null) { JOptionPane.showMessageDialog(null, "请先选择大类！"); return; }
        String name = JOptionPane.showInputDialog("请输入新具体类型名称：");
        if (name == null || name.trim().isEmpty()) return;
        try {
            SpecificTypeSingleResponse newType = mainPage.getSpecificTypeHelper()
                    .addSpecificType(name.trim(), categoryItem.id);
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
            CategorySingleResponse updated = mainPage.getCategoryHelper().updateCategory(item.id, newName.trim());
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
            SpecificTypeSingleResponse updated = mainPage.getSpecificTypeHelper()
                    .updateSpecificType(item.id, newName.trim(), categoryItem.id);
            mainPage.updateSpecificType(updated);
            updateSpecificOptionsByCategory();
            JOptionPane.showMessageDialog(null, "修改成功！");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "修改失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ------------------------- 删除（含兜底迁移） -------------------------

    private void deleteCategory() {
        ComboItem item = (ComboItem) categoryCombo.getSelectedItem();
        if (item == null) return;
        if (item.id == SYSTEM_DEFAULT_ID) { JOptionPane.showMessageDialog(null, "系统默认类型不可删除！"); return; }

        int confirm = JOptionPane.showConfirmDialog(
                null, "删除后，该大类及其下具体类型的账单将改为'无'，是否继续？",
                "确认删除", JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            mainPage.getCategoryHelper().deleteCategory(item.id);
            mainPage.getSpecificTypeMap().values().removeIf(t -> t.getCategoryId() == item.id);
            mainPage.deleteCategory(item.id);
            loadCategoryOptions();
            updateSpecificOptionsByCategory();
            JOptionPane.showMessageDialog(null, "删除成功！");
        } catch (Exception backendEx) {
            System.out.println("[RecordPage] 后端删除分类失败，开始前端兜底迁移：" + backendEx.getMessage());
            try {
                mainPage.getSpecificTypeMap().values().stream()
                        .filter(t -> t.getCategoryId() == item.id)
                        .map(SpecificTypeSingleResponse::getSpecificTypeId)
                        .distinct()
                        .forEach(this::safeDeleteSpecificTypeById);

                try {
                    mainPage.getBillHelper().updateBillsCategory(item.id, SYSTEM_DEFAULT_ID);
                } catch (Exception bulkFail) {
                    reassignBillsCategoryFallback(item.id, SYSTEM_DEFAULT_ID);
                }

                mainPage.getCategoryHelper().deleteCategory(item.id);

                mainPage.deleteCategory(item.id);
                loadCategoryOptions();
                updateSpecificOptionsByCategory();
                JOptionPane.showMessageDialog(null, "删除成功！");
            } catch (Exception downgradeEx) {
                JOptionPane.showMessageDialog(null, "删除失败：" + downgradeEx.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSpecificType() {
        ComboItem item = (ComboItem) specificCombo.getSelectedItem();
        if (item == null) return;

        int confirm = JOptionPane.showConfirmDialog(
                null, "删除后，属于该具体类型的账单将改为'无'，是否继续？",
                "确认删除", JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            mainPage.getSpecificTypeHelper().deleteSpecificType(item.id);
            mainPage.deleteSpecificType(item.id);
            updateSpecificOptionsByCategory();
            JOptionPane.showMessageDialog(null, "删除成功！");
        } catch (Exception backendEx) {
            System.out.println("[RecordPage] 后端删除具体类型失败，前端兜底迁移：" + backendEx.getMessage());
            try {
                try {
                    mainPage.getBillHelper().updateBillsSpecificType(item.id, SYSTEM_DEFAULT_ID);
                } catch (Exception bulkFail) {
                    reassignBillsSpecificTypeFallback(item.id, SYSTEM_DEFAULT_ID);
                }
                mainPage.getSpecificTypeHelper().deleteSpecificType(item.id);
                mainPage.deleteSpecificType(item.id);
                updateSpecificOptionsByCategory();
                JOptionPane.showMessageDialog(null, "删除成功！");
            } catch (Exception downgradeEx) {
                JOptionPane.showMessageDialog(null, "删除失败：" + downgradeEx.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ------------------------- 兜底迁移（逐条更新账单） -------------------------

    private void reassignBillsCategoryFallback(int fromCategoryId, int toCategoryId) throws Exception {
        int count = 0;
        for (BillSingleResponse bill : mainPage.getBillMap().values()) {
            if (bill.getCategoryId() == fromCategoryId) {
                BillSingleResponse updated = mainPage.getBillHelper().updateBill(
                        bill.getBillId(),
                        bill.getBillTime(),
                        bill.getType(),
                        toCategoryId,
                        bill.getSpecificTypeId(),
                        bill.getAmount(),
                        bill.getRemark()
                );
                mainPage.updateBill(updated);
                count++;
            }
        }
        System.out.println("[RecordPage] 逐条迁移账单(按大类)完成，条数：" + count);
    }

    private void reassignBillsSpecificTypeFallback(int fromSpecificId, int toSpecificId) throws Exception {
        int count = 0;
        for (BillSingleResponse bill : mainPage.getBillMap().values()) {
            if (bill.getSpecificTypeId() == fromSpecificId) {
                BillSingleResponse updated = mainPage.getBillHelper().updateBill(
                        bill.getBillId(),
                        bill.getBillTime(),
                        bill.getType(),
                        bill.getCategoryId(),
                        toSpecificId,
                        bill.getAmount(),
                        bill.getRemark()
                );
                mainPage.updateBill(updated);
                count++;
            }
        }
        System.out.println("[RecordPage] 逐条迁移账单(按具体类型)完成，条数：" + count);
    }

    private void safeDeleteSpecificTypeById(int specificId) {
        try {
            mainPage.getSpecificTypeHelper().deleteSpecificType(specificId);
            mainPage.deleteSpecificType(specificId);
        } catch (Exception ex) {
            System.out.println("[RecordPage] 删除具体类型失败，迁移后重试 specificId=" + specificId + " ; " + ex.getMessage());
            try {
                try {
                    mainPage.getBillHelper().updateBillsSpecificType(specificId, SYSTEM_DEFAULT_ID);
                } catch (Exception bulkFail) {
                    reassignBillsSpecificTypeFallback(specificId, SYSTEM_DEFAULT_ID);
                }
                mainPage.getSpecificTypeHelper().deleteSpecificType(specificId);
                mainPage.deleteSpecificType(specificId);
            } catch (Exception inner) {
                throw new RuntimeException("具体类型删除失败（已尝试迁移），id=" + specificId + "；" + inner.getMessage(), inner);
            }
        }
    }

    // ------------------------- 提交/校验 -------------------------

    private void handleConfirm() {
        try {
            // 时间必须自己填
            String billTime = validateAndGetBillTime();
            if (billTime == null) return;

            ComboItem typeItem = (ComboItem) typeCombo.getSelectedItem();
            ComboItem categoryItem = (ComboItem) categoryCombo.getSelectedItem();
            ComboItem specificItem = (ComboItem) specificCombo.getSelectedItem();
            if (typeItem == null || categoryItem == null || specificItem == null) {
                JOptionPane.showMessageDialog(null, "请选择收支类型/大类/具体类型", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 金额必须自己填（不能是占位、不能为空、必须是数值且 > 0）
            if (isHint(amountField, HINT_AMOUNT)) {
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
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(null, "金额格式不正确", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(null, "金额必须大于0", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 备注可不填，未填则写入“无”
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
            clearForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "操作失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String validateAndGetBillTime() {
        // 不能使用占位
        if (isHint(dateField, HINT_DATE)) {
            JOptionPane.showMessageDialog(null, "请输入年月日", "错误", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        if (isHint(timeField, HINT_TIME)) {
            JOptionPane.showMessageDialog(null, "请输入时分", "错误", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        String dateStr = dateField.getText().trim();
        String timeStr = timeField.getText().trim();
        if (dateStr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "请输入年月日", "错误", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        if (timeStr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "请输入时分", "错误", JOptionPane.ERROR_MESSAGE);
            return null;
        }
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

    private void selectComboById(JComboBox<ComboItem> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).id == id) { combo.setSelectedIndex(i); return; }
        }
    }

    private void clearForm() {
        dateField.setText(HINT_DATE); dateField.setForeground(Color.GRAY);
        timeField.setText(HINT_TIME); timeField.setForeground(Color.GRAY);
        amountField.setText(HINT_AMOUNT); amountField.setForeground(Color.GRAY);
        remarkField.setText(HINT_REMARK); remarkField.setForeground(Color.GRAY);
        for (int i = 0; i < typeCombo.getItemCount(); i++) {
            if (typeCombo.getItemAt(i).id == 1) { typeCombo.setSelectedIndex(i); break; }
        }
        loadCategoryOptions();
        updateSpecificOptionsByCategory();
    }
}
