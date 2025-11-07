package com.accountbook.proxy.helper.impl;

import com.accountbook.proxy.FrontendRequest;
import com.accountbook.proxy.FrontendRequest.RequestType;
import com.accountbook.proxy.helper.BaseRequestHelper;
import com.accountbook.proxy.request.bill.BillAddParams;
import com.accountbook.proxy.request.bill.BillChangeParams;
import com.accountbook.proxy.request.bill.BillDeleteParams;
import com.accountbook.proxy.request.bill.BillSearchParams;
import com.accountbook.proxy.response.bill.BillDeleteResponse;
import com.accountbook.proxy.response.bill.BillListResponse;
import com.accountbook.proxy.response.bill.BillSingleResponse;

import java.math.BigDecimal;
import java.util.List;

/**
 * 账单相关请求封装（优化参数一致性与业务适配）
 */
public class BillRequestHelper extends BaseRequestHelper {

    /**
     * 新增账单：返回新增的账单详情
     * 修正参数：将accountId改为specificTypeId（符合大类+具体类型的业务设计）
     */
    public BillSingleResponse addBill(
            String billTime, int type, int categoryId, int specificTypeId, 
            BigDecimal amount, String remark) {
        validateParamNotNull(billTime, "账单时间不能为空");
        validateParamNotNull(amount, "账单金额不能为空");
        validateParamTrue(amount.compareTo(BigDecimal.ZERO) > 0, "账单金额必须大于0");
        validateParamTrue(categoryId > 0, "大类ID必须为正整数");
        validateParamTrue(specificTypeId > 0, "具体类型ID必须为正整数");

        BillAddParams params = new BillAddParams(billTime, type, categoryId, specificTypeId, amount, remark);
        FrontendRequest<BillAddParams> request = new FrontendRequest<>(RequestType.ADD_BILL, params);
        return parseResponse(sendRequest(request));
    }

    // 在BillRequestHelper中补充updateBill方法（完整实现）
    public BillSingleResponse updateBill(
            Integer billId, String time, Integer type,
            Integer categoryId, Integer specificTypeId,
            BigDecimal amount, String remark) {
        // 1. 参数校验
        validateParamNotNull(billId, "账单ID不能为空");
        validateParamNotNull(time, "账单时间不能为空");
        validateParamNotNull(type, "收支类型不能为空");
        validateParamTrue(type == 1 || type == -1, "收支类型必须为1（收入）或-1（支出）");
        validateParamNotNull(categoryId, "大类ID不能为空");
        validateParamNotNull(specificTypeId, "具体类型ID不能为空");
        validateParamNotNull(amount, "金额不能为空");
        validateParamTrue(amount.compareTo(BigDecimal.ZERO) > 0, "金额必须大于0");

        // 2. 构建修改参数
        BillChangeParams params = new BillChangeParams(billId);
        params.setBillTime(time);
        params.setType(type);
        params.setCategoryId(categoryId);
        params.setSpecificTypeId(specificTypeId);
        params.setAmount(amount);
        params.setRemark(remark);

        // 3. 调用后端更新接口（确保同步到数据库）
        FrontendRequest<BillChangeParams> request = new FrontendRequest<>(RequestType.CHANGE_BILL, params);
        return parseResponse(sendRequest(request));
    }

    /**
     * 批量更新账单的大类ID（用于删除大类时，将关联账单迁移到"无"类型）
     */
    public BillListResponse updateBillsCategory(Integer oldCategoryId, Integer newCategoryId) {
        validateParamNotNull(oldCategoryId, "原大类ID不能为空");
        validateParamNotNull(newCategoryId, "新大类ID不能为空");
        validateParamTrue(oldCategoryId > 0 && newCategoryId > 0, "大类ID必须为正整数");

        // 构建仅更新大类ID的参数（其他字段为null表示不修改）
        BillChangeParams batchParams = new BillChangeParams(
                null, null, null, newCategoryId, null, null, null
        );
        // 结合搜索条件：筛选出所有属于oldCategoryId的账单
        BillSearchParams searchParams = new BillSearchParams(null, null, oldCategoryId, null, null, null);
        
        // 发送批量更新请求（假设后端支持按条件批量更新）
        FrontendRequest<BillChangeParams> request = new FrontendRequest<>(
                RequestType.BATCH_UPDATE_BILL, batchParams, searchParams
        );
        return parseResponse(sendRequest(request));
    }

    /**
     * 批量更新账单的具体类型ID（用于删除具体类型时，将关联账单迁移到"无"类型）
     */
    public BillListResponse updateBillsSpecificType(Integer oldSpecificTypeId, Integer newSpecificTypeId) {
        validateParamNotNull(oldSpecificTypeId, "原具体类型ID不能为空");
        validateParamNotNull(newSpecificTypeId, "新具体类型ID不能为空");
        validateParamTrue(oldSpecificTypeId > 0 && newSpecificTypeId > 0, "具体类型ID必须为正整数");

        // 构建仅更新具体类型ID的参数
        BillChangeParams batchParams = new BillChangeParams(
                null, null, null, null, newSpecificTypeId, null, null
        );
        // 筛选出所有属于oldSpecificTypeId的账单
        BillSearchParams searchParams = new BillSearchParams(null, null, null, oldSpecificTypeId, null, null);
        
        FrontendRequest<BillChangeParams> request = new FrontendRequest<>(
                RequestType.BATCH_UPDATE_BILL, batchParams, searchParams
        );
        return parseResponse(sendRequest(request));
    }

    /**
     * 删除账单：返回被删除的账单ID信息
     */
    public BillDeleteResponse deleteBill(Integer billId) {
        validateParamNotNull(billId, "账单ID不能为空");
        validateParamTrue(billId > 0, "账单ID必须为正整数（当前值：" + billId + "）");

        BillDeleteParams params = new BillDeleteParams(billId);
        FrontendRequest<BillDeleteParams> request = new FrontendRequest<>(RequestType.DELETE_BILL, params);
        return parseResponse(sendRequest(request));
    }

    /**
     * 搜索账单（单值查询）：返回符合条件的账单列表
     */
    public BillListResponse searchBill(
            String time, Integer type, Integer categoryId,
            Integer specificTypeId, BigDecimal amount, String key) {
        BillSearchParams params = new BillSearchParams(time, type, categoryId, specificTypeId, amount, key);
        FrontendRequest<BillSearchParams> request = new FrontendRequest<>(RequestType.SEARCH_BILL, params);
        return parseResponse(sendRequest(request));
    }

    /**
     * 搜索账单（范围查询）：返回符合条件的账单列表
     */
    public BillListResponse searchBillByRange(
            String timeStart, String timeEnd, Integer type,
            Integer categoryId, Integer specificTypeId,
            BigDecimal amountMin, BigDecimal amountMax, String key) {
        BillSearchParams params = new BillSearchParams(
                timeStart, timeEnd, type, categoryId, specificTypeId, 
                amountMin, amountMax, key
        );
        FrontendRequest<BillSearchParams> request = new FrontendRequest<>(RequestType.SEARCH_BILL, params);
        return parseResponse(sendRequest(request));
    }

    /**
     * 按ID查询账单：返回唯一账单详情
     */
    public BillSingleResponse searchBillById(Integer billId) {
        validateParamNotNull(billId, "账单ID不能为空");
        validateParamTrue(billId > 0, "账单ID必须为正整数（当前值：" + billId + "）");
    
        // 关键修复：通过setter方法显式设置billId（确保参数被正确存储）
        BillSearchParams params = new BillSearchParams();
        params.setId(billId); // 假设BillSearchParams有setBillId()方法
    
        // 补充：打印参数日志，验证billId是否被正确设置
        System.out.println("查询参数：billId=" + params.getId()); // 确认输出为43
    
        // 发送查询请求（确保RequestType与后端匹配）
        FrontendRequest<BillSearchParams> request = new FrontendRequest<>(RequestType.SEARCH_BILL, params);
        BillListResponse listResponse = parseResponse(sendRequest(request));
    
        List<BillSingleResponse> bills = listResponse.getItems();
        if (bills == null || bills.isEmpty()) {
            throw new RuntimeException("未找到ID为" + billId + "的账单");
        }
        if (bills.size() > 1) {
            // 打印所有返回的账单ID，辅助确认后端是否真的按ID过滤
            StringBuilder ids = new StringBuilder();
            for (BillSingleResponse bill : bills) {
                ids.append(bill.getBillId()).append(",");
            }
            throw new RuntimeException("查询异常：按ID=" + billId + "查询到" + bills.size() + "条记录，实际ID为：" + ids);
        }
    
        return bills.get(0);
    }

    /**
     * 查询所有账单（按时间倒序）
     */
    public BillListResponse searchAllBills() {
        BillSearchParams emptyParams = new BillSearchParams();
        FrontendRequest<BillSearchParams> request = new FrontendRequest<>(RequestType.SEARCH_BILL, emptyParams);
        return parseResponse(sendRequest(request));
    }
    
}