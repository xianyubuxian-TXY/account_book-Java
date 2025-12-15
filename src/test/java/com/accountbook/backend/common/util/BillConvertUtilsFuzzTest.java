package com.accountbook.backend.common.util;

import com.accountbook.backend.common.exception.BusinessServiceException;
import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import org.junit.jupiter.api.Test;
import com.code_intelligence.jazzer.junit.FuzzTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

class BillConvertUtilsFuzzTest {

    @FuzzTest(maxDuration = "18000s") // 5小时 = 5*60*60
    void fuzz_mapToBill(FuzzedDataProvider data) {
        Map<String, Object> map = new HashMap<>();

        // 随机决定是否放 key，以及 value 用什么类型
        if (data.consumeBoolean()) map.put("id", randomNumberOrString(data));
        if (data.consumeBoolean()) map.put("bill_time", randomTimeValue(data));
        if (data.consumeBoolean()) map.put("type", randomNumberOrString(data));
        if (data.consumeBoolean()) map.put("category_id", randomNumberOrString(data));
        if (data.consumeBoolean()) map.put("specific_type_id", randomNumberOrString(data));
        if (data.consumeBoolean()) map.put("amount", randomAmountValue(data));
        if (data.consumeBoolean()) map.put("remark", data.consumeString(50));

        try {
            BillConvertUtils.mapToBill(map);
        } catch (BusinessServiceException expected) {
            // 业务可控异常：正常，忽略
        }
        // 其他异常不捕获，让 Jazzer 认为是崩溃并保存 testcase
    }

    private Object randomNumberOrString(FuzzedDataProvider data) {
        int choice = data.consumeInt(0, 4);
        switch (choice) {
            case 0: return data.consumeInt();
            case 1: return data.consumeLong();
            case 2: return data.consumeShort();
            case 3: return data.consumeString(20); // 可能是空串/非数字
            default: return new Object();          // 故意塞不支持类型，看看会不会崩
        }
    }

    private Object randomTimeValue(FuzzedDataProvider data) {
        int choice = data.consumeInt(0, 3);
        switch (choice) {
            case 0: return data.consumeString(30);    // 可能是 "2024/05/01" 等非法
            case 1: return LocalDateTime.now().plusDays(data.consumeInt(-1000, 1000));
            case 2: return "";                         // 空字符串
            default: return new Object();              // 不支持类型
        }
    }

    private Object randomAmountValue(FuzzedDataProvider data) {
        return switch (data.consumeInt(0, 4)) {
            case 0 -> data.consumeString(40); // 直接给字符串，让 BillConvertUtils 自己处理
            case 1 -> data.consumeInt(-100000, 100000);
            case 2 -> BigDecimal.valueOf(data.consumeLong(-100000, 100000), 2);
            case 3 -> null;
            default -> new Object();
        };
    }
}
