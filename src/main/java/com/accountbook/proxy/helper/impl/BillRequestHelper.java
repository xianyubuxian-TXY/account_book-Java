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

/**
 * 优化后：直接返回业务数据，内部封装响应判断
 */
public class BillRequestHelper extends BaseRequestHelper {

    /**
     * 新增账单：返回新增的账单详情
     */
    public BillSingleResponse addBill(
            String date, int type, int categoryId, int accountId, BigDecimal amount, String remark) {
        validateParamNotNull(date, "账单日期不能为空");
        validateParamNotNull(amount, "账单金额不能为空");
        validateParamTrue(amount.compareTo(BigDecimal.ZERO) != 0, "账单金额不能为0");

        BillAddParams params = new BillAddParams(date, type, categoryId, accountId, amount, remark);
        FrontendRequest<BillAddParams> request = new FrontendRequest<>(RequestType.ADD_BILL, params);
        // 解析响应，直接返回业务数据
        return parseResponse(sendRequest(request));
    }

    /**
     * 修改账单：返回修改后的账单详情
     */
    public BillSingleResponse changeBill(
            Integer billId, String date, Integer type,
            Integer categoryId, Integer accountId,
            BigDecimal amount, String remark) {
        validateParamNotNull(billId, "账单ID不能为空");
        validateParamTrue(billId > 0, "账单ID必须为正整数");

        BillChangeParams params = new BillChangeParams(billId, date, type, categoryId, accountId, amount, remark);
        FrontendRequest<BillChangeParams> request = new FrontendRequest<>(RequestType.CHANGE_BILL, params);
        return parseResponse(sendRequest(request));
    }

    /**
     * 删除账单：返回被删除的账单ID信息
     */
    public BillDeleteResponse deleteBill(Integer billId) {
        validateParamNotNull(billId, "账单ID不能为空");
        validateParamTrue(billId > 0, "账单ID必须为正整数");

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
        BillSearchParams params = new BillSearchParams(timeStart, timeEnd, type, categoryId,
                specificTypeId, amountMin, amountMax, key);
        FrontendRequest<BillSearchParams> request = new FrontendRequest<>(RequestType.SEARCH_BILL, params);
        return parseResponse(sendRequest(request));
    }
}