package com.accountbook.backend.common.util;

/**
 * 驼峰命名转下划线命名（用于Java属性与数据库字段映射）
 */
public class NameConvertUtils {
    public static String camelToUnderline(String camelName) {
        if (camelName == null || camelName.isEmpty()) {
            return camelName;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(Character.toLowerCase(camelName.charAt(0)));
        for (int i = 1; i < camelName.length(); i++) {
            char c = camelName.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append("_").append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
