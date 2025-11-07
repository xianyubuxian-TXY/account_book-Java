package com.accountbook.proxy.helper.factory;

import com.accountbook.proxy.ServiceProxy;
import com.accountbook.proxy.helper.BaseRequestHelper;
import com.accountbook.proxy.helper.impl.BillRequestHelper;
import com.accountbook.proxy.helper.impl.CategoryRequestHelper;
import com.accountbook.proxy.helper.impl.SpecificTypeRequestHelper;
import com.accountbook.proxy.helper.impl.BudgetRequestHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * 共享模式请求助手工厂：单例+实例容器，确保Helper实例全局共享，支持前端页面注入
 * 扩展：新增预算请求助手的获取方法
 */
public class RequestHelperFactory {
    // 1. 单例工厂实例（全局唯一）
    private static final RequestHelperFactory INSTANCE = new RequestHelperFactory();

    // 2. 共享实例容器：存储已创建的Helper实例（key=Helper类型标识，value=实例）
    private final Map<String, BaseRequestHelper> helperContainer = new HashMap<>();

    // 3. 私有构造：禁止外部实例化
    private RequestHelperFactory() {}

    // 4. 全局获取工厂实例的入口（供前端页面注入使用）
    public static RequestHelperFactory getInstance() {
        return INSTANCE;
    }

    /**
     * 获取或创建账单请求助手（共享实例）
     * 首次调用创建实例并放入容器，后续调用直接从容器获取
     * @param serviceProxy 业务代理（仅首次创建时需要，后续可传null）
     * @return 共享的BillRequestHelper实例
     */
    public BillRequestHelper getBillHelper(ServiceProxy serviceProxy) {
        String key = BillRequestHelper.class.getName(); // 用类名作为唯一标识
        // 从容器获取已有实例
        BillRequestHelper helper = (BillRequestHelper) helperContainer.get(key);
        if (helper == null) {
            // 首次创建：初始化并放入容器
            helper = new BillRequestHelper();
            helper.init(serviceProxy); // 调用基类初始化方法
            helperContainer.put(key, helper);
        }
        return helper;
    }

    /**
     * 获取或创建分类请求助手（共享实例）
     * 首次调用创建实例并放入容器，后续调用直接从容器获取
     * @param serviceProxy 业务代理（仅首次创建时需要，后续可传null）
     * @return 共享的CategoryRequestHelper实例
     */
    public CategoryRequestHelper getCategoryHelper(ServiceProxy serviceProxy) {
        String key = CategoryRequestHelper.class.getName(); // 类名作为唯一标识，避免冲突
        CategoryRequestHelper helper = (CategoryRequestHelper) helperContainer.get(key);
        if (helper == null) {
            helper = new CategoryRequestHelper();
            helper.init(serviceProxy); // 注入ServiceProxy完成初始化
            helperContainer.put(key, helper);
        }
        return helper;
    }

    /**
     * 获取或创建具体类型请求助手（共享实例）
     * 首次调用创建实例并放入容器，后续调用直接从容器获取
     * @param serviceProxy 业务代理（仅首次创建时需要，后续可传null）
     * @return 共享的SpecificRequestHelper实例
     */
    public SpecificTypeRequestHelper getSpecificTypeHelper(ServiceProxy serviceProxy) {
        String key = SpecificTypeRequestHelper.class.getName(); // 类名作为唯一标识
        SpecificTypeRequestHelper helper = (SpecificTypeRequestHelper) helperContainer.get(key);
        if (helper == null) {
            helper = new SpecificTypeRequestHelper();
            helper.init(serviceProxy); // 调用基类初始化方法
            helperContainer.put(key, helper);
        }
        return helper;
    }

    /**
     * 获取或创建预算请求助手（共享实例）
     * 首次调用创建实例并放入容器，后续调用直接从容器获取
     * @param serviceProxy 业务代理（仅首次创建时需要，后续可传null）
     * @return 共享的BudgetHelper实例
     */
    public BudgetRequestHelper getBudgetHelper(ServiceProxy serviceProxy) {
        String key = BudgetRequestHelper.class.getName(); // 类名作为唯一标识
        BudgetRequestHelper helper = (BudgetRequestHelper) helperContainer.get(key);
        if (helper == null) {
            helper = new BudgetRequestHelper();
            helper.init(serviceProxy); // 调用基类初始化方法
            helperContainer.put(key, helper);
        }
        return helper;
    }

    /**
     * 清空容器（用于测试或特殊场景）
     */
    public void clear() {
        helperContainer.clear();
    }
}