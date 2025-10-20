package com.accountbook.backend.service;

// 业务服务接口：T为参数类型，R为返回类型
public interface BusinessService<T, R> {
    R execute(T params) throws Exception; // 接收参数并返回处理结果
}


