package com.accountbook.proxy.request.category;

import java.util.HashMap;
import java.util.Map;

/*category操作参数：增加/删除 */
public class CategoryAddParams {
    private String name;

    public CategoryAddParams(){};
    public CategoryAddParams(String name)
    {
        this.name=name;
    }

    String getString(){return name;}
    void setName(String name){this.name=name;}

    /**
     * 将参数转换为 Map（键为字段名，值为字段值）
     * 用于 SQL 参数绑定、JSON 序列化等场景
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        return map;
    }
}
