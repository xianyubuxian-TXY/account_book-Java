package com.accountbook.proxy.helper;

import com.accountbook.proxy.BackendResponse;
import com.accountbook.proxy.FrontendRequest;
import com.accountbook.proxy.ServiceProxy;

/**
 * 请求助手基类：新增响应解析方法
 */
public abstract class BaseRequestHelper {
    protected ServiceProxy serviceProxy;

    public void init(ServiceProxy proxy) {
        if (proxy == null) {
            throw new IllegalArgumentException("ServiceProxy 不能为空，初始化失败");
        }
        this.serviceProxy = proxy;
    }

    @SuppressWarnings("unchecked")
    protected <T, R> BackendResponse<R> sendRequest(FrontendRequest<T> request) {
        if (serviceProxy == null) {
            throw new IllegalStateException("Helper 未初始化！请先调用 init 方法");
        }
        return serviceProxy.handleRequest(request);
    }

    // 通用参数校验方法（不变）
    protected void validateParamNotNull(Object param, String errorMsg) {
        if (param == null) {
            throw new IllegalArgumentException(errorMsg);
        }
    }

    protected void validateParamTrue(boolean condition, String errorMsg) {
        if (!condition) {
            throw new IllegalArgumentException(errorMsg);
        }
    }

    /**
     * 响应解析工具方法：解析BackendResponse，返回业务数据
     * 失败时抛出运行时异常（携带失败消息），成功时返回data
     * @param response 后端响应对象
     * @param <T> 业务数据类型
     * @return 成功时的业务数据
     */
    protected <T> T parseResponse(BackendResponse<T> response) {
        if (response.isSuccess()) {
            // 成功：返回业务数据
            return response.getData();
        } else {
            // 失败：抛出异常（前端可捕获处理，或直接展示异常消息）
            throw new RuntimeException("操作失败：" + response.getMessage());
        }
    }
}