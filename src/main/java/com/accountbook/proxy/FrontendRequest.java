package com.accountbook.proxy;

public class FrontendRequest<T>{
    //请求类型
    public enum RequestType
    {
        ADD_BILL, //增加账单
        DELETE_BILL, //删除账单
        CHANGE_BILL, //修改账单
        SEARCH_BILL, //查找账单
        STATISTIC_BILL, //统计账单
        VISUAL_BILL, //可视化账单
        ADD_BUDGET, //增加预算
        DELETE_BUDGET, //删除预算
        CHANGE_BUDGET, //修改预算
        SEARCH_BUDGET, //查找预算
    };

    private RequestType m_type; //请求的服务类型类型
    private T m_parms; //请求参数

    public FrontendRequest(RequestType type,T parms)
    {
        m_type=type;
        m_parms=parms;
    }

    public RequestType getType()
    {
        return m_type;
    }

    public T getParams()
    {
        return m_parms;
    }

    void setType(final RequestType type)
    {   
        m_type=type;
    }

    void setParms(final T parms)
    {
        m_parms=parms;
    }
}
