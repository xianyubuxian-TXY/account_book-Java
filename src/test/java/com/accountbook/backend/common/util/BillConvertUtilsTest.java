package com.accountbook.backend.common.util;

import com.accountbook.backend.common.exception.BusinessServiceException;
import com.accountbook.backend.storage.entity.Bill;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class BillConvertUtilsTest {

    // ===================== 你原来的用例（保留） =====================

    @Test
    void testMapToBill_normal() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 1);
        map.put("bill_time", "2024-05-01 12:00:00");
        map.put("type", 1);
        map.put("category_id", 10);
        map.put("specific_type_id", 100);
        map.put("amount", new BigDecimal("99.99"));
        map.put("remark", "test");

        Bill bill = BillConvertUtils.mapToBill(map);

        assertEquals(1, bill.getId());
        assertEquals("2024-05-01 12:00:00", bill.getTime());
        assertEquals(1, bill.getType());
        assertEquals(new BigDecimal("99.99"), bill.getAmount());
    }

    @Test
    void testMapToBill_amountZero() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 1);
        map.put("bill_time", "2024-05-01 12:00:00");
        map.put("type", 1);
        map.put("category_id", 10);
        map.put("specific_type_id", 100);
        map.put("amount", BigDecimal.ZERO);

        assertThrows(BusinessServiceException.class,
                () -> BillConvertUtils.mapToBill(map));
    }

    @Test
    void testMapToBill_invalidTime() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 1);
        map.put("bill_time", "2024/05/01");
        map.put("type", 1);
        map.put("category_id", 10);
        map.put("specific_type_id", 100);
        map.put("amount", new BigDecimal("10"));

        assertThrows(BusinessServiceException.class,
                () -> BillConvertUtils.mapToBill(map));
    }

    @Test
    void testMapToBill_invalidType() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 1);
        map.put("bill_time", "2024-05-01 12:00:00");
        map.put("type", 0);
        map.put("category_id", 10);
        map.put("specific_type_id", 100);
        map.put("amount", new BigDecimal("10"));

        assertThrows(BusinessServiceException.class,
                () -> BillConvertUtils.mapToBill(map));
    }

    @Test
    void testMapToBill_integerAsString() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "1");                 // String -> Integer
        map.put("bill_time", "2024-05-01 12:00:00");
        map.put("type", "1");
        map.put("category_id", "10");
        map.put("specific_type_id", "100");
        map.put("amount", "12.50");

        Bill bill = BillConvertUtils.mapToBill(map);

        assertEquals(1, bill.getId());
        assertEquals(new BigDecimal("12.50"), bill.getAmount());
    }

    @Test
    void testMapToBill_integerInvalidString() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "abc");               // 非法整数
        map.put("bill_time", "2024-05-01 12:00:00");
        map.put("type", 1);
        map.put("category_id", 10);
        map.put("specific_type_id", 100);
        map.put("amount", new BigDecimal("10"));

        assertThrows(BusinessServiceException.class,
                () -> BillConvertUtils.mapToBill(map));
    }

    @Test
    void testMapToBill_amountInvalidString() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 1);
        map.put("bill_time", "2024-05-01 12:00:00");
        map.put("type", 1);
        map.put("category_id", 10);
        map.put("specific_type_id", 100);
        map.put("amount", "abc"); // 非法金额

        assertThrows(BusinessServiceException.class,
                () -> BillConvertUtils.mapToBill(map));
    }

    @Test
    void testMapToBill_timeAsLocalDateTime() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 1);
        map.put("bill_time", java.time.LocalDateTime.of(2024, 5, 1, 12, 0));
        map.put("type", 1);
        map.put("category_id", 10);
        map.put("specific_type_id", 100);
        map.put("amount", new BigDecimal("10"));

        Bill bill = BillConvertUtils.mapToBill(map);

        assertNotNull(bill.getTime());
    }

    @Test
    void testMapToBill_emptyRemark() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 1);
        map.put("bill_time", "2024-05-01 12:00:00");
        map.put("type", 1);
        map.put("category_id", 10);
        map.put("specific_type_id", 100);
        map.put("amount", new BigDecimal("10"));
        map.put("remark", "   ");

        Bill bill = BillConvertUtils.mapToBill(map);

        assertNull(bill.getRemark());
    }

    @Test
    void testMapListToBillList() {
        List<Map<String, Object>> list = new ArrayList<>();

        Map<String, Object> valid = new HashMap<>();
        valid.put("id", 1);
        valid.put("bill_time", "2024-05-01 12:00:00");
        valid.put("type", 1);
        valid.put("category_id", 10);
        valid.put("specific_type_id", 100);
        valid.put("amount", new BigDecimal("20"));

        Map<String, Object> invalid = new HashMap<>(); // 空 map

        list.add(valid);
        list.add(invalid);

        List<Bill> bills = BillConvertUtils.mapListToBillList(list);

        assertEquals(1, bills.size());
    }

    // ===================== 为了把覆盖率拉到 80%+ 的补充分支用例 =====================

    private Map<String, Object> validMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 1);
        map.put("bill_time", "2024-05-01 12:00:00");
        map.put("type", 1);
        map.put("category_id", 10);
        map.put("specific_type_id", 100);
        map.put("amount", new BigDecimal("10.00"));
        map.put("remark", "ok");
        return map;
    }

    // ---- mapToBill 入口校验：null / empty ----
    @Test
    void testMapToBill_nullMap() {
        assertThrows(BusinessServiceException.class, () -> BillConvertUtils.mapToBill(null));
    }

    @Test
    void testMapToBill_emptyMap() {
        assertThrows(BusinessServiceException.class, () -> BillConvertUtils.mapToBill(new HashMap<>()));
    }

    // ---- type = -1 合法分支 ----
    @Test
    void testMapToBill_typeMinusOne_valid() {
        Map<String, Object> map = validMap();
        map.put("type", -1);

        Bill bill = BillConvertUtils.mapToBill(map);
        assertEquals(-1, bill.getType());
    }

    // ---- getIntegerValue：Number 分支（Long 等）----
    @Test
    void testMapToBill_integerAsNumberLong() {
        Map<String, Object> map = validMap();
        map.put("id", 1L);
        map.put("category_id", 10L);
        map.put("specific_type_id", 100L);

        Bill bill = BillConvertUtils.mapToBill(map);
        assertEquals(1, bill.getId());
        assertEquals(10, bill.getCategoryId());
        assertEquals(100, bill.getSpecificTypeId());
    }

    // ---- getIntegerValue：空字符串 -> null -> 必填字段报错 ----
    @Test
    void testMapToBill_integerEmptyString_shouldThrow() {
        Map<String, Object> map = validMap();
        map.put("category_id", "   "); // trim后空 -> null -> mapToBill 必填字段异常

        assertThrows(BusinessServiceException.class, () -> BillConvertUtils.mapToBill(map));
    }

    // ---- getIntegerValue：不支持类型分支 ----
    @Test
    void testMapToBill_integerUnsupportedType_shouldThrow() {
        Map<String, Object> map = validMap();
        map.put("id", new Object()); // 非 Number / 非 String

        assertThrows(BusinessServiceException.class, () -> BillConvertUtils.mapToBill(map));
    }

    // ---- getBigDecimalValue：Number 分支（Integer/Long/Double 等）----
    @Test
    void testMapToBill_amountAsNumber() {
        Map<String, Object> map = validMap();
        map.put("amount", 20); // Number -> BigDecimal("20")

        Bill bill = BillConvertUtils.mapToBill(map);
        assertEquals(new BigDecimal("20.00"), bill.getAmount());
    }

    // ---- getBigDecimalValue：空字符串 -> null -> 必填字段报错 ----
    @Test
    void testMapToBill_amountEmptyString_shouldThrow() {
        Map<String, Object> map = validMap();
        map.put("amount", "   ");

        assertThrows(BusinessServiceException.class, () -> BillConvertUtils.mapToBill(map));
    }

    // ---- getBigDecimalValue：不支持类型分支 ----
    @Test
    void testMapToBill_amountUnsupportedType_shouldThrow() {
        Map<String, Object> map = validMap();
        map.put("amount", new Object());

        assertThrows(BusinessServiceException.class, () -> BillConvertUtils.mapToBill(map));
    }

    // ---- getTimeStringValue：空字符串 -> null -> 必填字段报错 ----
    @Test
    void testMapToBill_timeEmptyString_shouldThrow() {
        Map<String, Object> map = validMap();
        map.put("bill_time", "   ");

        assertThrows(BusinessServiceException.class, () -> BillConvertUtils.mapToBill(map));
    }

    // ---- getTimeStringValue：不支持类型分支 ----
    @Test
    void testMapToBill_timeUnsupportedType_shouldThrow() {
        Map<String, Object> map = validMap();
        map.put("bill_time", new Object());

        assertThrows(BusinessServiceException.class, () -> BillConvertUtils.mapToBill(map));
    }

    @Test
    void testMapToBill_missingSpecificTypeId_shouldThrow() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 1);
        map.put("bill_time", "2024-05-01 12:00:00");
        map.put("type", 1);
        map.put("category_id", 10);
        // 故意不放 specific_type_id
        map.put("amount", new BigDecimal("10"));

        assertThrows(BusinessServiceException.class,
                () -> BillConvertUtils.mapToBill(map));
    }

    @Test
    void testMapToBill_missingCategoryId_shouldThrow() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 1);
        map.put("bill_time", "2024-05-01 12:00:00");
        map.put("type", 1);
        // 故意不放 category_id
        map.put("specific_type_id", 100);
        map.put("amount", new BigDecimal("10"));

        assertThrows(BusinessServiceException.class,
                () -> BillConvertUtils.mapToBill(map));
    }

    // ---- mapListToBillList：null/empty 输入分支 ----
    @Test
    void testMapListToBillList_nullInput_shouldReturnEmpty() {
        List<Bill> bills = BillConvertUtils.mapListToBillList(null);
        assertNotNull(bills);
        assertTrue(bills.isEmpty());
    }

    @Test
    void testMapListToBillList_emptyInput_shouldReturnEmpty() {
        List<Bill> bills = BillConvertUtils.mapListToBillList(Collections.emptyList());
        assertNotNull(bills);
        assertTrue(bills.isEmpty());
    }
}
