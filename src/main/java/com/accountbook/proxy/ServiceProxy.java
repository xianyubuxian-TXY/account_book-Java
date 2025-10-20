package com.accountbook.proxy;

import com.accountbook.backend.factory.BusinessFactory;
import com.accountbook.backend.service.BusinessService;

public class ServiceProxy {
    private final BusinessFactory businessFactory;

    public ServiceProxy(BusinessFactory factory) {
        businessFactory = factory;
    }

    /**
     * 处理泛型请求，传递参数并返回标准化响应
     * @param request 前端泛型请求
     * @param <T> 请求参数类型
     * @param <R> 响应数据类型
     * @return 标准化后端响应（泛型类型，与业务结果匹配）
     */
    public <T, R> BackendResponse<R> handleRequest(FrontendRequest<T> request) {
        // 1. 校验请求合法性
        if (request == null || request.getType() == null) {
            return BackendResponse.fail(BackendResponse.Code.INVALID_REQUEST, "请求对象或请求类型不能为空");
        }

        // 2. 获取对应业务服务
        BusinessService<T, R> service = getBusinessService(request.getType());
        if (service == null) {
            return BackendResponse.fail(BackendResponse.Code.INVALID_REQUEST, 
                    "无效的请求类型：" + request.getType());
        }

        try {
            // 3. 提取参数并执行业务逻辑
            T params = request.getParams();
            // 可选：参数非空校验（根据业务需求调整，部分服务可能允许空参数）
            validateParams(request.getType(), params);
            
            R result = service.execute(params);
            // 4. 返回成功响应（携带业务结果）
            return BackendResponse.success("处理成功", result);
        } catch (IllegalArgumentException e) {
            // 5. 处理参数非法异常（400错误）
            return BackendResponse.fail(BackendResponse.Code.INVALID_REQUEST, 
                    "参数错误：" + e.getMessage());
        } catch (NullPointerException e) {
            // 6. 处理空指针异常（细化错误类型）
            return BackendResponse.fail(BackendResponse.Code.INVALID_REQUEST, 
                    "关键参数为空：" + e.getMessage());
        } catch (Exception e) {
            // 7. 处理通用业务异常（500错误）
            // 生产环境建议记录日志：log.error("业务处理失败", e);
            return BackendResponse.fail(BackendResponse.Code.ERROR, 
                    "处理失败：" + e.getMessage());
        }
    }

    /**
     * 根据请求类型获取对应的泛型业务服务
     */
    @SuppressWarnings("unchecked")
    private <T, R> BusinessService<T, R> getBusinessService(FrontendRequest.RequestType requestType) {
        return switch (requestType) {
            case ADD_BILL -> (BusinessService<T, R>) businessFactory.createAddBillService();
            case DELETE_BILL -> (BusinessService<T, R>) businessFactory.createDeleteBillService();
            case CHANGE_BILL -> (BusinessService<T, R>) businessFactory.createChangeBillService();
            case SEARCH_BILL -> (BusinessService<T, R>) businessFactory.createSearchBillService();
            case STATISTIC_BILL -> (BusinessService<T, R>) businessFactory.createStatisticBillService();
            case ADD_BUDGET -> (BusinessService<T, R>) businessFactory.createAddBudgetService();
            case DELETE_BUDGET -> (BusinessService<T, R>) businessFactory.createDeleteBudgetService();
            case CHANGE_BUDGET -> (BusinessService<T, R>) businessFactory.createChangeBudgetService();
            case SEARCH_BUDGET -> (BusinessService<T, R>) businessFactory.createSearchBudgetService();
            default -> null;
        };
    }

    /**
     * 可选：参数合法性校验（根据具体业务需求扩展）
     * @param requestType 请求类型
     * @param params 待校验参数
     */
    private <T> void validateParams(FrontendRequest.RequestType requestType, T params) {
        switch (requestType) {
            case ADD_BILL:
            case CHANGE_BILL:
                if (params == null) {
                    throw new IllegalArgumentException(requestType.name() + "请求参数不能为空");
                }
                break;
            case DELETE_BILL:
                if (params == null || !(params instanceof Long) || (Long) params <= 0) {
                    throw new IllegalArgumentException("删除账单的ID必须为正整数");
                }
                break;
            // 其他请求类型的参数校验可按需添加
            default:
                break;
        }
    }
}