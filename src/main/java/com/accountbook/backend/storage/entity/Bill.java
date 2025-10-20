package com.accountbook.backend.storage.entity;

import com.accountbook.backend.util.TimeUtils;



// 账单实体（对应需求Req002的核心字段）
public class Bill {
    private String m_time; // 时间（格式：YYYY-MM-DD HH:MM）
    private String m_type; // 收支类型（收入/支出）
    private double m_amount; // 金额（支持小数）
    private String m_category; // 大类（如饮食）
    private String m_detail; // 具体说明（如外卖）
    private String m_remark; // 备注

    //time: YYYY-MM-DD
    public Bill(String time,String type,double amount,String category,String detail,String remark)
    {
        m_time=time+" "+TimeUtils.getInstance().getCurrentTime(); //拼接：YYYY_MM_DD HH:MM
        m_type=type;
        m_amount=amount;
        m_category=category;
        m_detail=detail;
        m_remark=remark;
    }

    //打印自己
    public void PrintSelf()
    {
        System.out.printf("m_time=%s,m_type=%s,m_amount=%.2f,m_category=%s,m_detail=%s,m_remark=%s\n",
                                    m_time,m_type,m_amount,m_category,m_detail,m_remark);
    }

    //简单测试
    public static void main(String[] args)
    {
        Bill b1=new Bill("2025-10-18","支出",23.567,"饮食","午餐","吃了一顿火锅");
        b1.PrintSelf();
        Bill b2=new Bill("2025-10-18","支出",24,"饮食","无","无");
        b2.PrintSelf();
    }
}
