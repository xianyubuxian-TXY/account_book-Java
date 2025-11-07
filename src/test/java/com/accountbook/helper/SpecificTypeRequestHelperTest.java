package com.accountbook.helper;

import com.accountbook.proxy.common.ProxyHandler;
import com.accountbook.proxy.helper.impl.CategoryRequestHelper;
import com.accountbook.proxy.helper.impl.SpecificTypeRequestHelper;
import com.accountbook.proxy.response.category.CategorySingleResponse;
import com.accountbook.proxy.response.specific_type.SpecificTypeDeleteResponse;
import com.accountbook.proxy.response.specific_type.SpecificTypeListResponse;
import com.accountbook.proxy.response.specific_type.SpecificTypeSingleResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SpecificTypeRequestHelperTest {

    // 生成带短时间戳的唯一具体类型名称（确保更新后总长度≤20）
    private static String getUniqueSpecificTypeName() {
        // 前缀：“测类_”（2个汉字+下划线，3字符）
        // 时间戳：“MMddHHmm”（8字符，如“10222224”），总长度=3+8=11字符
        return "测类_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMddHHmm"));
    }

    public static void main(String[] args) {
        // 初始化代理和助手类
        ProxyHandler.init();
        SpecificTypeRequestHelper specificHelper = ProxyHandler.getSpecificTypeHelper();
        CategoryRequestHelper categoryHelper = ProxyHandler.getCategoryHelper();
        System.out.println("=== 开始测试SpecificTypeRequestHelper ===");

        // 测试用参数（依赖一个有效大类，先创建临时大类）
        String testCategoryName = "测试大类_" + System.currentTimeMillis(); // 临时大类名称
        CategorySingleResponse testCategory = null; // 存储临时大类信息
        String testSpecificName = getUniqueSpecificTypeName(); // 具体类型名称
        String updatedSpecificName = "更新_" + testSpecificName; // 更新后的名称
        Integer testSpecificId = null; // 存储新增的具体类型ID

        try {
            // 前置准备：创建一个临时大类（具体类型需关联大类）
            testCategory = categoryHelper.addCategory(testCategoryName);
            Integer testCategoryId = testCategory.getCategoryId();
            System.out.println("\n【前置准备】创建临时大类：ID=" + testCategoryId + "，名称=" + testCategoryName);


            // 1. 新增具体类型测试
            System.out.println("\n=== 测试1：新增具体类型 ===");
            SpecificTypeSingleResponse newSpecific = specificHelper.addSpecificType(testSpecificName, testCategoryId);
            testSpecificId = newSpecific.getSpecificTypeId();
            System.out.println("新增具体类型成功！ID：" + testSpecificId 
                    + "，名称：" + newSpecific.getName() 
                    + "，关联大类ID：" + newSpecific.getCategoryId());

            // 1.1 测试重复添加（同一大类下同名称，预期失败）
            System.out.println("\n=== 测试1.1：重复添加（同一大类下同名称，预期失败） ===");
            try {
                specificHelper.addSpecificType(testSpecificName, testCategoryId); // 相同名称+同一大类
                System.out.println("错误：未拦截重复添加的具体类型！");
            } catch (RuntimeException e) {
                System.out.println("验证成功：" + e.getMessage()); // 预期输出“已存在”
            }

            // 1.2 测试添加空白名称（预期失败）
            System.out.println("\n=== 测试1.2：添加空白名称（预期失败） ===");
            try {
                specificHelper.addSpecificType("   ", testCategoryId); // 空白字符
                System.out.println("错误：未拦截空白名称的具体类型！");
            } catch (RuntimeException e) {
                System.out.println("验证成功：" + e.getMessage()); // 预期输出“不能为空白”
            }

            // 1.3 测试添加超长名称（预期失败）
            System.out.println("\n=== 测试1.3：添加超长名称（预期失败） ===");
            String longName = "超长具体类型名称超长具体类型名称超长具体类型名称"; // 超过20字符
            try {
                specificHelper.addSpecificType(longName, testCategoryId);
                System.out.println("错误：未拦截超长名称的具体类型！");
            } catch (RuntimeException e) {
                System.out.println("验证成功：" + e.getMessage()); // 预期输出“过长”
            }

            // 1.4 测试关联无效大类ID（预期失败）
            System.out.println("\n=== 测试1.4：关联无效大类ID（预期失败） ===");
            try {
                specificHelper.addSpecificType(testSpecificName, -1); // 无效ID（负数）
                System.out.println("错误：未拦截无效大类ID！");
            } catch (RuntimeException e) {
                System.out.println("验证成功：" + e.getMessage()); // 预期输出“必须为正整数”
            }


            // 2. 按ID查询具体类型
            System.out.println("\n=== 测试2：按ID查询具体类型 ===");
            SpecificTypeSingleResponse queriedSpecific = specificHelper.searchSpecificTypeById(testSpecificId);
            System.out.println("查询到ID=" + testSpecificId + "的具体类型：" 
                    + "名称=" + queriedSpecific.getName() 
                    + "，关联大类ID=" + queriedSpecific.getCategoryId());


            // 3. 按名称+大类ID查询具体类型
            System.out.println("\n=== 测试3：按名称+大类ID查询具体类型 ===");
            String nameKey = testSpecificName.substring(0, 5); // 截取前5个字符作为关键字
            SpecificTypeListResponse searchResult = specificHelper.searchSpecificType(nameKey, testCategoryId);
            System.out.println("名称包含'" + nameKey + "'且大类ID=" + testCategoryId + "的具体类型共" 
                    + searchResult.getItems().size() + "条：");
            searchResult.getItems().forEach(type -> 
                System.out.println("- ID=" + type.getSpecificTypeId() 
                        + " | 名称=" + type.getName() 
                        + " | 大类ID=" + type.getCategoryId())
            );


            // 4. 更新具体类型测试（更新名称）
            System.out.println("\n=== 测试4：更新具体类型名称 ===");
            SpecificTypeSingleResponse updatedByName = specificHelper.updateSpecificType(
                    testSpecificId, updatedSpecificName, null); // 只更新名称
            System.out.println("更新后具体类型：ID=" + updatedByName.getSpecificTypeId() 
                    + " | 新名称=" + updatedByName.getName() 
                    + " | 大类ID（不变）=" + updatedByName.getCategoryId());

            // 4.1 更新具体类型测试（更新大类ID）
            System.out.println("\n=== 测试4.1：更新具体类型关联的大类ID ===");
            // 先新增一个目标大类
            String targetCategoryName = "目标大类_" + System.currentTimeMillis();
            CategorySingleResponse targetCategory = categoryHelper.addCategory(targetCategoryName);
            Integer targetCategoryId = targetCategory.getCategoryId();
            // 执行更新
            SpecificTypeSingleResponse updatedByCategory = specificHelper.updateSpecificType(
                    testSpecificId, null, targetCategoryId); // 只更新大类ID
            System.out.println("更新后具体类型：ID=" + updatedByCategory.getSpecificTypeId() 
                    + " | 名称（不变）=" + updatedByCategory.getName() 
                    + " | 新大类ID=" + updatedByCategory.getCategoryId());


            // 4.2 测试更新为同一大类下已存在的名称（预期失败）
            System.out.println("\n=== 测试4.2：更新为同一大类下已存在的名称（预期失败） ===");
            // 先在目标大类下新增一个冲突的具体类型
            String conflictName = "冲突_" + System.currentTimeMillis();
            SpecificTypeSingleResponse conflictSpecific = specificHelper.addSpecificType(conflictName, targetCategoryId);
            try {
                specificHelper.updateSpecificType(testSpecificId, conflictName, targetCategoryId); // 用冲突名称
                System.out.println("错误：未拦截更新为已有名称的操作！");
            } catch (RuntimeException e) {
                System.out.println("验证成功：" + e.getMessage()); // 预期输出“已存在”
            } finally {
                // 清理冲突的具体类型
                specificHelper.deleteSpecificType(conflictSpecific.getSpecificTypeId());
            }


            // 4.3 测试更新后与原数据一致（无意义更新，预期失败）
            System.out.println("\n=== 测试4.3：无意义更新（预期失败） ===");
            try {
                // 新名称和大类ID与当前一致
                specificHelper.updateSpecificType(testSpecificId, updatedSpecificName, targetCategoryId);
                System.out.println("错误：未拦截无意义更新！");
            } catch (RuntimeException e) {
                System.out.println("验证成功：" + e.getMessage()); // 预期输出“无需修改”
            }


            // 5. 查询所有具体类型
            System.out.println("\n=== 测试5：查询所有具体类型 ===");
            SpecificTypeListResponse allSpecifics = specificHelper.searchAllSpecificTypes();
            System.out.println("系统中所有具体类型共" + allSpecifics.getItems().size() + "条");


            // 6. 删除具体类型测试
            System.out.println("\n=== 测试6：删除具体类型 ===");
            SpecificTypeDeleteResponse deleteResponse = specificHelper.deleteSpecificType(testSpecificId);
            System.out.println("删除结果：" + deleteResponse);
            System.out.println("删除的具体类型ID：" + deleteResponse.getSpecificTypeId());


            // 7. 验证删除结果
            System.out.println("\n=== 测试7：验证删除结果 ===");
            try {
                specificHelper.searchSpecificTypeById(testSpecificId);
                System.out.println("错误：删除后仍能查询到具体类型！");
            } catch (RuntimeException e) {
                System.out.println("验证成功：" + e.getMessage()); // 预期输出“未找到”
            }


            // 8. 测试删除系统默认具体类型（ID=1，预期失败）
            System.out.println("\n=== 测试8：删除系统默认具体类型（预期失败） ===");
            try {
                specificHelper.deleteSpecificType(1); // 默认具体类型ID=1
                System.out.println("错误：未保护系统默认具体类型！");
            } catch (RuntimeException e) {
                System.out.println("验证成功：" + e.getMessage()); // 预期输出“不可删除”
            }


        } catch (Exception e) {
            System.err.println("\n测试失败：" + e.getMessage());
            e.printStackTrace();
        } finally {
            // 清理临时数据（无论测试成功与否，确保环境干净）
            try {
                if (testSpecificId != null) {
                    specificHelper.deleteSpecificType(testSpecificId); // 二次清理，防止残留
                }
                if (testCategory != null) {
                    categoryHelper.deleteCategory(testCategory.getCategoryId()); // 删除临时大类
                }
                System.out.println("\n【清理完成】临时测试数据已删除");
            } catch (Exception e) {
                System.err.println("清理临时数据失败：" + e.getMessage());
            }
        }

        System.out.println("\n=== 所有测试结束 ===");
    }
}