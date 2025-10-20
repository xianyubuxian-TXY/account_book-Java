package com.accountbook.backend.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 时间工具类（单例模式）
 * 确保全局只有一个实例，提供统一的时间格式化功能
 */
public class TimeUtils {

    // 1. 私有静态实例（饿汉式：类加载时直接初始化，线程安全）
    private static final TimeUtils INSTANCE = new TimeUtils();

    // 2. 私有构造方法（禁止外部实例化）
    private TimeUtils() {}

    // 3. 公共静态方法（提供唯一实例访问入口）
    public static TimeUtils getInstance() {
        return INSTANCE;
    }

    /**
     * 获取当前时间，返回 YYYY-MM-DD HH:MM 格式的字符串
     * @return 格式化后的当前时间字符串
     */
    public String getCurrentTime() {
        // 获取当前时间（默认时区）
        LocalDateTime now = LocalDateTime.now();
        
        // 定义目标格式：年-月-日 时:分（24小时制）
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        // 格式化时间并返回
        return now.format(formatter);
    }

    // 测试示例：验证单例唯一性及功能
    public static void main(String[] args) {
        // 获取两个实例，判断是否为同一对象
        TimeUtils instance1 = TimeUtils.getInstance();
        TimeUtils instance2 = TimeUtils.getInstance();
        System.out.println("两个实例是否相同：" + (instance1 == instance2)); // 输出 true

        // 测试时间格式化功能
        System.out.println("当前时间：" + instance1.getCurrentTime()); // 输出示例：2025-10-19 16:45
    }
}