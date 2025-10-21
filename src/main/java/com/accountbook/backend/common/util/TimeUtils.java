package com.accountbook.backend.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// 假设该方法所在类为 TimeUtils
public class TimeUtils {
    // 单例模式相关代码（如果已有可保留）
    private static TimeUtils instance;

    private TimeUtils() {}

    public static TimeUtils getInstance() {
        if (instance == null) {
            instance = new TimeUtils();
        }
        return instance;
    }

    /**
     * 获取当前时间，返回 YYYY-MM-DD HH:MM 格式的字符串
     * @return 格式化后的当前时间字符串
     */
    public String getCurrentTime() {
        // 调用重载方法，传入完整格式模板
        return getCurrentTime("yyyy-MM-dd HH:mm");
    }

    /**
     * 重载方法：返回 HH:mm 格式的当前时间字符串（时分）
     * @return 格式化后的时分字符串
     */
    public String getCurrentTimeHHmm() {
        // 调用重载方法，传入时分格式模板
        return getCurrentTime("HH:mm");
    }

    /**
     * 核心工具方法：根据传入的格式模板返回当前时间
     * @param pattern 时间格式模板（如 "yyyy-MM-dd HH:mm"、"HH:mm"）
     * @return 格式化后的时间字符串
     */
    private String getCurrentTime(String pattern) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return now.format(formatter);
    }
}