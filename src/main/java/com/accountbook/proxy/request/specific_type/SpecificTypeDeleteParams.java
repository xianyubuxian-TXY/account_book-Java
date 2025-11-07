package com.accountbook.proxy.request.specific_type;

import java.util.HashMap;
import java.util.Map;

public class SpecificTypeDeleteParams {
    private int id;
    public SpecificTypeDeleteParams(){};
    public SpecificTypeDeleteParams(int id)
    {
        this.id=id;
    }

    public int getId(){return id;}
    public void setId(int id){this.id=id;}

    /**
     * 将参数转换为 Map（键为字段名，值为字段值）
     * 用于 SQL 参数绑定、JSON 序列化等场景
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        return map;
    }
}
