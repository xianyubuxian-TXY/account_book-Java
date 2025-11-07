package com.accountbook.helper;

import com.accountbook.proxy.common.ProxyHandler;
import com.accountbook.proxy.helper.impl.CategoryRequestHelper;
import com.accountbook.proxy.response.category.CategoryDeleteResponse;
import com.accountbook.proxy.response.category.CategoryListResponse;
import com.accountbook.proxy.response.category.CategorySingleResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CategoryRequestHelperTest {

    // 生成带时间戳的唯一分类名称（限制长度，确保拼接后不超限）
    private static String getUniqueCategoryName() {
        // 原名称长度控制在15字符内（加上"改_"后总长度=15+2=17 ≤20）
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMddHHmmss")); // 10字符
        return "测试_" + timestamp; // 示例："测试_1022221707"（共12字符）
    }

    public static void main(String[] args) {
        // 初始化代理和分类助手
        ProxyHandler.init();
        CategoryRequestHelper categoryHelper = ProxyHandler.getCategoryHelper();
        System.out.println("=== 开始测试CategoryRequestHelper ===");

        // 测试用参数
        String testCategoryName = getUniqueCategoryName(); // 唯一测试名称
        String updatedName = "更新_" + testCategoryName;   // 更新后的名称
        Integer testCategoryId = null;                     // 存储新增的分类ID

        try {
            // 1. 新增分类测试
            System.out.println("\n=== 测试1：新增分类 ===");
            CategorySingleResponse newCategory = categoryHelper.addCategory(testCategoryName);
            testCategoryId = newCategory.getCategoryId();
            System.out.println("新增分类成功！ID：" + testCategoryId + "，名称：" + newCategory.getName());

            // 1.1 测试重复添加同名分类（预期失败）
            System.out.println("\n=== 测试1.1：重复添加同名分类（预期失败） ===");
            try {
                categoryHelper.addCategory(testCategoryName); // 使用相同名称
                System.out.println("错误：未拦截重复添加的分类！");
            } catch (RuntimeException e) {
                System.out.println("验证成功：" + e.getMessage()); // 预期输出“已存在”
            }

            // 1.2 测试添加空白名称（预期失败）
            System.out.println("\n=== 测试1.2：添加空白名称分类（预期失败） ===");
            try {
                categoryHelper.addCategory("   "); // 空白字符
                System.out.println("错误：未拦截空白名称分类！");
            } catch (RuntimeException e) {
                System.out.println("验证成功：" + e.getMessage()); // 预期输出“不能为空白”
            }

            // 1.3 测试添加超长名称（预期失败）
            System.out.println("\n=== 测试1.3：添加超长名称分类（预期失败） ===");
            String longName = "超长分类名称超长分类名称超长分类名称超长分类名称"; // 超过20字符
            try {
                categoryHelper.addCategory(longName);
                System.out.println("错误：未拦截超长名称分类！");
            } catch (RuntimeException e) {
                System.out.println("验证成功：" + e.getMessage()); // 预期输出“过长”
            }

            // 2. 按ID查询分类
            System.out.println("\n=== 测试2：按ID查询分类 ===");
            CategorySingleResponse queriedCategory = categoryHelper.searchCategoryById(testCategoryId);
            System.out.println("查询到ID=" + testCategoryId + "的分类：名称=" + queriedCategory.getName());

            // 3. 按名称搜索分类（模糊查询）
            System.out.println("\n=== 测试3：按名称搜索分类 ===");
            String nameKey = testCategoryName.substring(0, 4); // 截取前4个字符作为关键字
            CategoryListResponse searchResult = categoryHelper.searchCategory(nameKey);
            System.out.println("名称包含'" + nameKey + "'的分类共" + searchResult.getItems().size() + "条：");
            searchResult.getItems().forEach(cat -> 
                System.out.println("- ID=" + cat.getCategoryId() + " | 名称=" + cat.getName())
            );

            // 4. 更新分类测试
            System.out.println("\n=== 测试4：更新分类名称 ===");
            CategorySingleResponse updatedCategory = categoryHelper.updateCategory(testCategoryId, updatedName);
            System.out.println("更新后分类：ID=" + updatedCategory.getCategoryId() + " | 新名称=" + updatedCategory.getName());

            // 4.1 测试更新为已有名称（预期失败）
            System.out.println("\n=== 测试4.1：更新为已有名称（预期失败） ===");
            // 先新增一个用于冲突的分类
            String conflictName = getUniqueCategoryName() + "_冲突";
            CategorySingleResponse conflictCategory = categoryHelper.addCategory(conflictName);
            try {
                categoryHelper.updateCategory(testCategoryId, conflictName); // 使用冲突名称
                System.out.println("错误：未拦截更新为已有名称的操作！");
            } catch (RuntimeException e) {
                System.out.println("验证成功：" + e.getMessage()); // 预期输出“已被其他分类使用”
            } finally {
                // 清理冲突分类
                categoryHelper.deleteCategory(conflictCategory.getCategoryId());
            }

            // 4.2 测试更新为原名称（预期失败）
            System.out.println("\n=== 测试4.2：更新为原名称（预期失败） ===");
            try {
                categoryHelper.updateCategory(testCategoryId, updatedName); // 与当前名称一致
                System.out.println("错误：未拦截无意义更新！");
            } catch (RuntimeException e) {
                System.out.println("验证成功：" + e.getMessage()); // 预期输出“无需修改”
            }

            // 5. 查询所有分类
            System.out.println("\n=== 测试5：查询所有分类 ===");
            CategoryListResponse allCategories = categoryHelper.searchAllCategories();
            System.out.println("系统中所有分类共" + allCategories.getItems().size() + "条");

            // 6. 删除分类测试
            System.out.println("\n=== 测试6：删除分类 ===");
            CategoryDeleteResponse deleteResponse = categoryHelper.deleteCategory(testCategoryId);
            System.out.println("删除结果：" + deleteResponse);
            System.out.println("删除的分类ID：" + deleteResponse.getCategoryId());

            // 7. 验证删除结果
            System.out.println("\n=== 测试7：验证删除结果 ===");
            try {
                categoryHelper.searchCategoryById(testCategoryId);
                System.out.println("错误：删除后仍能查询到分类！");
            } catch (RuntimeException e) {
                System.out.println("验证成功：" + e.getMessage()); // 预期输出“未找到分类”
            }

            // 8. 测试删除系统默认分类（ID=1，预期失败）
            System.out.println("\n=== 测试8：删除系统默认分类（预期失败） ===");
            try {
                categoryHelper.deleteCategory(1); // 默认分类ID=1
                System.out.println("错误：未保护系统默认分类！");
            } catch (RuntimeException e) {
                System.out.println("验证成功：" + e.getMessage()); // 预期输出“不可删除”
            }

        } catch (Exception e) {
            System.err.println("\n测试失败：" + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=== 所有测试结束 ===");
    }
}