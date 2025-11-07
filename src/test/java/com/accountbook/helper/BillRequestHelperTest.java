package com.accountbook.helper;

import com.accountbook.proxy.common.ProxyHandler;
import com.accountbook.proxy.helper.impl.BillRequestHelper;
import com.accountbook.proxy.response.bill.BillDeleteResponse;
import com.accountbook.proxy.response.bill.BillListResponse;
import com.accountbook.proxy.response.bill.BillSingleResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BillRequestHelperTest {

    // 格式化当前时间为"yyyy-MM-dd HH:mm:ss"
    private static String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static void main(String[] args) {
        // 初始化账单请求助手
        ProxyHandler.init();
        BillRequestHelper billHelper =ProxyHandler.getBillHelper();
        System.out.println("=== 开始测试BillRequestHelper ===");

        try {
            // 1. 新增账单测试
            System.out.println("\n=== 测试1：新增账单 ===");
            // 测试数据（假设大类ID=1，具体类型ID=1，类型1=收入）
            BillSingleResponse newBill = billHelper.addBill(
                    getCurrentTime(),       // 账单时间
                    1,                      // 类型：1=收入，-1=支出
                    1,                      // 大类ID（需确保存在）
                    1,                      // 具体类型ID（需确保存在）
                    new BigDecimal("500.00"), // 金额
                    "测试新增账单：工资收入"   // 备注
            );
            System.out.println("新增账单成功！ID：" + newBill.getBillId());
            System.out.println("新增账单详情：" + newBill);

            // 2. 按ID查询账单测试
            System.out.println("\n=== 测试2：按ID查询账单 ===");
            Integer billId = newBill.getBillId();
            BillSingleResponse queriedBill = billHelper.searchBillById(billId);
            System.out.println("查询到ID=" + billId + "的账单：" + queriedBill);

            // 3. 更新账单测试（修改金额和备注）
            System.out.println("\n=== 测试3：更新账单 ===");
            BillSingleResponse updatedBill = billHelper.updateBill(
                    billId,                 // 要修改的账单ID
                    null,                   // 不修改时间（传null表示保持原样）
                    null,                   // 不修改类型
                    null,                   // 不修改大类
                    null,                   // 不修改具体类型
                    new BigDecimal("600.00"), // 新金额
                    "测试更新账单：工资收入（调整后）" // 新备注
            );
            System.out.println("更新后账单详情：" + updatedBill);

            // 4. 搜索账单（按条件查询）
            System.out.println("\n=== 测试4：按条件搜索账单 ===");
            BillListResponse searchResult = billHelper.searchBill(
                    null,                   // 不按时间筛选
                    1,                      // 只查收入类型
                    1,                      // 只查大类ID=1
                    null,                   // 不限制具体类型
                    null,                   // 不限制金额
                    "工资"                  // 备注包含"工资"
            );
            System.out.println("符合条件的账单共" + searchResult.getItems().size() + "条：");
            searchResult.getItems().forEach(bill -> System.out.println("- " + bill));

            // 5. 查询所有账单
            System.out.println("\n=== 测试5：查询所有账单 ===");
            BillListResponse allBills = billHelper.searchAllBills();
            System.out.println("系统中所有账单共" + allBills.getItems().size() + "条");

            // 6. 删除账单测试
            System.out.println("\n=== 测试6：删除账单 ===");
            BillDeleteResponse deleteResponse = billHelper.deleteBill(billId);
            System.out.println("删除结果：" + deleteResponse);
            System.out.println("删除的账单ID：" + deleteResponse.getBillId());

            // 7. 验证删除（预期抛出异常：未找到账单）
            System.out.println("\n=== 测试7：验证删除结果 ===");
            try {
                billHelper.searchBillById(billId);
                System.out.println("错误：删除后仍能查询到账单！");
            } catch (RuntimeException e) {
                System.out.println("验证成功：" + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("\n测试失败：" + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=== 所有测试结束 ===");
    }
}