package com.accountbook.backend.storage.dao.factory;

import com.accountbook.backend.storage.dao.BillDAO;
import com.accountbook.backend.storage.dao.BudgetDAO;
import com.accountbook.backend.storage.dao.CategoryDAO;
import com.accountbook.backend.storage.dao.SpecificTypeDAO;
import com.accountbook.backend.storage.dao.impl.BillDAOImpl;
import com.accountbook.backend.storage.dao.impl.BudgetDAOImpl;
import com.accountbook.backend.storage.dao.impl.CategoryDAOImpl;
import com.accountbook.backend.storage.dao.impl.SpecificTypeDAOImpl;;

/*工厂模式统一创建 DAO*/
public class DAOFactory {
    private static BillDAO billDAO = new BillDAOImpl();
    private static BudgetDAO budgetDAO = new BudgetDAOImpl();
    private static CategoryDAO categoryDAO=new CategoryDAOImpl();
    private static SpecificTypeDAO specificTypeDAO=new SpecificTypeDAOImpl();

    public static BillDAO getBillDAO() {
        return billDAO;
    }

    public static CategoryDAO getCategoryDAO(){
        return categoryDAO;
    }

    public static SpecificTypeDAO getSpecificTypeDAO(){
        return specificTypeDAO;
    }

    public static BudgetDAO getBudgetDAO() {
        return budgetDAO;
    }
}
