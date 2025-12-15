package com.accountbook.backend.service.impl.bill;

import com.accountbook.backend.storage.dao.BillDAO;
import com.accountbook.backend.storage.entity.Bill;
import com.accountbook.proxy.request.bill.BillAddParams;
import com.accountbook.proxy.response.bill.BillSingleResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AddBillServiceIntegrationTest {

    private void injectMockDao(AddBillService service, BillDAO mockDao) throws Exception {
        Field field = AddBillService.class.getDeclaredField("billBAO");
        field.setAccessible(true);
        field.set(service, mockDao);
    }

    @Test
    void integration_addBill_success() throws Exception {
        // 1. 构造 Mock DAO
        BillDAO mockDao = Mockito.mock(BillDAO.class);

        Mockito.when(mockDao.addBill(Mockito.any()))
                .thenReturn(1);

        Bill bill = new Bill(
                "2024-05-01 12:00:00",
                1,
                10,
                100,
                new BigDecimal("88.88"),
                "integration test"
        );
        bill.setId(1);

        Mockito.when(mockDao.queryBillById(1))
                .thenReturn(bill);

        // 2. 创建 Service 并注入 Mock DAO
        AddBillService service = new AddBillService();
        injectMockDao(service, mockDao);

        // 3. 调用 Service
        BillAddParams params = new BillAddParams(
                "2024-05-01 12:00:00",
                1,
                10,
                100,
                new BigDecimal("88.88"),
                "integration test"
        );

        BillSingleResponse response = service.execute(params);

        // 4. 断言结果
        assertNotNull(response);
        assertEquals(1, response.getBillId());
        assertEquals(new BigDecimal("88.88"), response.getAmount());
    }

    @Test
    void integration_addBill_failed() throws Exception {
        BillDAO mockDao = Mockito.mock(BillDAO.class);
    
        Mockito.when(mockDao.addBill(Mockito.any()))
                .thenReturn(-1);
    
        AddBillService service = new AddBillService();
        injectMockDao(service, mockDao);
    
        BillAddParams params = new BillAddParams(
                "2024-05-01 12:00:00",
                1,
                10,
                100,
                new BigDecimal("50"),
                "fail test"
        );
    
        assertThrows(Exception.class, () -> service.execute(params));
    }
    

}
