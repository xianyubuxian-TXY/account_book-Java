package com.accountbook.backend.service.impl.specific_type;

import com.accountbook.backend.common.util.SpecificTypeConvertUtils; // 假设存在具体类型转换工具类
import com.accountbook.backend.service.BusinessService;
import com.accountbook.backend.storage.dao.SpecificTypeDAO;
import com.accountbook.backend.storage.dao.factory.DAOFactory;
import com.accountbook.backend.storage.entity.SpecificType;
import com.accountbook.proxy.request.specific_type.SpecificTypeSearchParams;
import com.accountbook.proxy.response.specific_type.SpecificTypeListResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 具体类型查询业务实现类（参考SearchBillService、SearchCategoryService设计风格）
 */
public class SearchSpecificTypeService implements BusinessService<SpecificTypeSearchParams, SpecificTypeListResponse> {

    // 注入具体类型DAO（通过工厂获取，与其他查询服务保持一致）
    private final SpecificTypeDAO specificTypeDAO = DAOFactory.getSpecificTypeDAO();

    @Override
    public SpecificTypeListResponse execute(SpecificTypeSearchParams params) throws Exception {
        System.out.println("执行具体类型查询业务");

        // 处理参数为空的情况（直接查询所有并按ID正序）
        if (params == null || isAllParamsNull(params)) {
            System.out.println("无有效查询条件，默认返回所有具体类型（按ID由小到大）");
            List<SpecificType> allSpecificTypes = specificTypeDAO.queryAllSpecificTypesOrderByIdAsc();
            return SpecificTypeListResponse.fromSpecificTypeList(allSpecificTypes);
        }

        // 1. 将查询参数转为 Map
        Map<String, Object> paramMap = params.toMap();
        if (paramMap.isEmpty()) {
            // 所有参数均为null（但params不为null），默认返回所有具体类型
            System.out.println("所有查询条件均为null，默认返回所有具体类型（按ID由小到大）");
            List<SpecificType> allSpecificTypes = specificTypeDAO.queryAllSpecificTypesOrderByIdAsc();
            return SpecificTypeListResponse.fromSpecificTypeList(allSpecificTypes);
        }

        // 2. 动态拼接 SQL 条件和参数列表
        StringBuilder condition = new StringBuilder();
        List<Object> sqlParams = new ArrayList<>();

        // 遍历 Map 生成 AND 连接的条件（对应SpecificTypeSearchParams的toMap字段）
        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value == null) {
                continue; // 跳过 null 值条件
            }

            // 根据字段类型拼接条件
            switch (key) {
                case "id":
                    // 具体类型ID：精确匹配
                    condition.append("id = ? AND ");
                    sqlParams.add(value);
                    break;
                case "name_like":
                    // 名称关键字：模糊查询（前后加%）
                    condition.append("name LIKE CONCAT('%', ?, '%') AND ");
                    sqlParams.add(value);
                    break;
                case "category_id":
                    // 关联大类ID：精确匹配（查询某大类下的具体类型）
                    condition.append("category_id = ? AND ");
                    sqlParams.add(value);
                    break;
                default:
                    // 忽略未知字段
                    break;
            }
        }

        // 3. 处理条件字符串（移除末尾多余的 "AND "）
        String finalCondition = "";
        if (condition.length() > 0) {
            finalCondition = condition.substring(0, condition.length() - 4);
        } else {
            // 理论上不会走到这里，因paramMap非空且已过滤null
            System.out.println("条件处理后为空，默认返回所有具体类型（按ID由小到大）");
            List<SpecificType> allSpecificTypes = specificTypeDAO.queryAllSpecificTypesOrderByIdAsc();
            return SpecificTypeListResponse.fromSpecificTypeList(allSpecificTypes);
        }

        // 4. 调用 DAO 查询完整字段（字段参数传 "*"）
        List<Map<String, Object>> mapList = specificTypeDAO.querySpecificTypes("*", finalCondition, sqlParams.toArray());

        // 5. 将 Map 列表转为 SpecificType 实体列表（复用转换工具类）
        List<SpecificType> specificTypeList = SpecificTypeConvertUtils.mapListToSpecificTypeList(mapList);

        // 6. 将实体列表转为 SpecificTypeListResponse
        return SpecificTypeListResponse.fromSpecificTypeList(specificTypeList);
    }

    /**
     * 判断查询参数是否全为null（辅助方法）
     * @param params 具体类型查询参数对象
     * @return true=所有参数都为null；false=存在非null参数
     */
    private boolean isAllParamsNull(SpecificTypeSearchParams params) {
        // 检查具体类型查询的核心字段是否全为null
        return params.getSpecificTypeId() == null &&
               (params.getNameKey() == null || params.getNameKey().trim().isEmpty()) &&
               params.getCategoryId() == null;
    }
}