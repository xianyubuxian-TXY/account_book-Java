package com.accountbook.backend.storage.dao.factory;

import com.accountbook.backend.storage.dao.BillDAO;
import com.accountbook.backend.storage.dao.BudgetDAO;
import com.accountbook.backend.storage.dao.impl.BillDAOImpl;
import com.accountbook.backend.storage.dao.impl.BudgetDAOImpl;;

/*工厂模式统一创建 DAO*/
public class DAOFactory {
    private static BillDAO billDAO = new BillDAOImpl();
    private static BudgetDAO budgetDAO = new BudgetDAOImpl();

    public static BillDAO getBillDAO() {
        return billDAO;
    }

    public static BudgetDAO getBudgetDAO() {
        return budgetDAO;
    }
}
