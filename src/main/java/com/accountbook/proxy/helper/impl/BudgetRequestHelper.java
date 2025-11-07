package com.accountbook.proxy.helper.impl;

import com.accountbook.proxy.FrontendRequest;
import com.accountbook.proxy.FrontendRequest.RequestType;
import com.accountbook.proxy.helper.BaseRequestHelper;
import com.accountbook.proxy.request.budget.BudgetAddParams;
import com.accountbook.proxy.request.budget.BudgetChangeParams;
import com.accountbook.proxy.request.budget.BudgetDeleteParams;
import com.accountbook.proxy.request.budget.BudgetSearchParams;
import com.accountbook.proxy.response.budget.BudgetDeleteResponse;
import com.accountbook.proxy.response.budget.BudgetListResponse;
import com.accountbook.proxy.response.budget.BudgetSingleResponse;

import java.math.BigDecimal;
import java.util.List;

/**
 * 预算请求助手类：封装预算相关的请求处理，与其他业务助手保持一致的设计风格
 */
public class BudgetRequestHelper extends BaseRequestHelper {

    /**
     * 新增预算：返回新增的预算详情
     * 校验规则：月份唯一（同一分类同一月份不允许重复预算）
     */
    public BudgetSingleResponse addBudget(String month, Integer categoryId, BigDecimal totalBudget) {
        // 核心参数非空校验
        validateParamNotNull(month, "预算月份不能为空");
        month = month.trim(); // 去除前后空格
        validateParamNotNull(categoryId, "分类ID不能为空");
        validateParamNotNull(totalBudget, "总预算金额不能为空");
        
        // 格式与合法性校验（强化月份格式）
        validateParamTrue(categoryId > 0, "分类ID必须为正整数（当前值：" + categoryId + "）");
        validateParamTrue(totalBudget.compareTo(BigDecimal.ZERO) > 0, "总预算金额必须大于0（当前值：" + totalBudget + "）");
        validateParamTrue(month.matches("^\\d{4}-\\d{2}$"), "月份格式错误（需为YYYY-MM，当前值：" + month + "）");
        
        // 强化重复校验：明确打印查询条件和结果，便于调试
        System.out.println("校验重复预算：month=" + month + ", categoryId=" + categoryId);
        BudgetListResponse existing = searchBudget(month, categoryId);
        if (existing == null) {
            throw new RuntimeException("查询预算是否存在时返回空结果，校验失败");
        }
        List<BudgetSingleResponse> existingItems = existing.getItems();
        if (existingItems != null && !existingItems.isEmpty()) {
            // 打印已存在的预算ID，明确重复记录
            StringBuilder ids = new StringBuilder();
            for (BudgetSingleResponse item : existingItems) {
                ids.append(item.getBudgetId()).append(",");
            }
            throw new RuntimeException("分类ID=" + categoryId + "在" + month + "已存在预算（ID：" + ids + "），不可重复添加");
        }
    
        // 发送新增请求
        BudgetAddParams params = new BudgetAddParams(month, categoryId, totalBudget);
        FrontendRequest<BudgetAddParams> request = new FrontendRequest<>(RequestType.ADD_BUDGET, params);
        return parseResponse(sendRequest(request));
    }

    /**
     * 修改预算：返回修改后的预算详情（统一命名为update，与其他业务助手保持一致）
     */
    public BudgetSingleResponse updateBudget(
            Integer budgetId, String month, Integer categoryId, BigDecimal totalBudget) {
        // 基础标识校验
        validateParamNotNull(budgetId, "预算ID不能为空");
        validateParamTrue(budgetId > 0, "预算ID必须为正整数（当前值：" + budgetId + "）");
        
        // 校验：至少修改一个字段（避免无效请求）
        boolean hasUpdate = (month != null) || (categoryId != null) || (totalBudget != null);
        validateParamTrue(hasUpdate, "至少需要修改一个预算字段");
        
        // 字段格式校验（仅校验非空的修改字段）
        if (month != null) {
            month = month.trim(); // 新增：去除前后空格
            validateParamTrue(month.matches("^\\d{4}-\\d{2}$"), "月份格式错误（需为YYYY-MM，当前值：" + month + "）");
        }
        if (categoryId != null) {
            validateParamTrue(categoryId > 0, "分类ID必须为正整数（当前值：" + categoryId + "）");
        }
        if (totalBudget != null) {
            validateParamTrue(totalBudget.compareTo(BigDecimal.ZERO) > 0, "总预算金额必须大于0（当前值：" + totalBudget + "）");
        }

        // 构建修改参数（仅传递需要修改的字段）
        BudgetChangeParams params = new BudgetChangeParams(budgetId);
        params.setMonth(month);
        params.setCategoryId(categoryId);
        params.setTotalBudget(totalBudget);
        
        // 发送修改请求
        FrontendRequest<BudgetChangeParams> request = new FrontendRequest<>(RequestType.CHANGE_BUDGET, params);
        return parseResponse(sendRequest(request));
    }

    /**
     * 删除预算：返回被删除的预算ID信息
     */
    public BudgetDeleteResponse deleteBudget(Integer budgetId) {
        validateParamNotNull(budgetId, "预算ID不能为空");
        validateParamTrue(budgetId > 0, "预算ID必须为正整数（当前值：" + budgetId + "）");

        BudgetDeleteParams params = new BudgetDeleteParams(budgetId);
        FrontendRequest<BudgetDeleteParams> request = new FrontendRequest<>(RequestType.DELETE_BUDGET, params);
        return parseResponse(sendRequest(request));
    }

    /**
     * 按分类+月份查询预算（单值查询）
     */
    public BudgetListResponse searchBudget(String month, Integer categoryId) {
        // 可选参数格式校验（仅校验非空参数）
        if (month != null) {
            validateParamTrue(month.matches("\\d{4}-\\d{2}"), "月份格式错误（需为YYYY-MM，当前值：" + month + "）");
        }
        if (categoryId != null) {
            validateParamTrue(categoryId > 0, "分类ID必须为正整数（当前值：" + categoryId + "）");
        }

        BudgetSearchParams params = new BudgetSearchParams(month, categoryId);
        FrontendRequest<BudgetSearchParams> request = new FrontendRequest<>(RequestType.SEARCH_BUDGET, params);
        return parseResponse(sendRequest(request));
    }

    /**
     * 按月份范围+分类查询预算（范围查询）
     */
    public BudgetListResponse searchBudgetByMonthRange(
            String monthStart, String monthEnd, Integer categoryId) {
        // 月份范围格式校验
        if (monthStart != null) {
            validateParamTrue(monthStart.matches("\\d{4}-\\d{2}"), "起始月份格式错误（需为YYYY-MM，当前值：" + monthStart + "）");
        }
        if (monthEnd != null) {
            validateParamTrue(monthEnd.matches("\\d{4}-\\d{2}"), "结束月份格式错误（需为YYYY-MM，当前值：" + monthEnd + "）");
        }
        // 分类ID校验（若传递）
        if (categoryId != null) {
            validateParamTrue(categoryId > 0, "分类ID必须为正整数（当前值：" + categoryId + "）");
        }

        BudgetSearchParams params = new BudgetSearchParams(monthStart, monthEnd, categoryId);
        FrontendRequest<BudgetSearchParams> request = new FrontendRequest<>(RequestType.SEARCH_BUDGET, params);
        return parseResponse(sendRequest(request));
    }

    /**
     * 按ID查询预算：返回唯一预算详情
     */
    public BudgetSingleResponse searchBudgetById(Integer budgetId) {
        validateParamNotNull(budgetId, "预算ID不能为空");
        validateParamTrue(budgetId > 0, "预算ID必须为正整数（当前值：" + budgetId + "）");

        BudgetSearchParams params = new BudgetSearchParams(budgetId);
        FrontendRequest<BudgetSearchParams> request = new FrontendRequest<>(RequestType.SEARCH_BUDGET, params);
        BudgetListResponse listResponse = parseResponse(sendRequest(request));

        List<BudgetSingleResponse> budgets = listResponse.getItems();
        if (budgets == null || budgets.isEmpty()) {
            throw new RuntimeException("未找到ID为" + budgetId + "的预算");
        }
        if (budgets.size() > 1) {
            throw new RuntimeException("查询异常：ID为" + budgetId + "的预算存在" + budgets.size() + "条记录");
        }

        return budgets.get(0);
    }

    /**
     * 查询所有预算：默认按月份倒序（最新月份在前）
     */
    public BudgetListResponse searchAllBudgets() {
        BudgetSearchParams emptyParams = new BudgetSearchParams();
        FrontendRequest<BudgetSearchParams> request = new FrontendRequest<>(RequestType.SEARCH_BUDGET, emptyParams);
        return parseResponse(sendRequest(request));
    }
}