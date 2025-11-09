package com.accountbook.backend.service.impl.specific_type;

import com.accountbook.backend.common.exception.BusinessServiceException;
import com.accountbook.backend.service.BusinessService;
import com.accountbook.backend.storage.dao.BillDAO;
import com.accountbook.backend.storage.dao.SpecificTypeDAO;
import com.accountbook.backend.storage.dao.factory.DAOFactory;
import com.accountbook.backend.storage.entity.SpecificType;
import com.accountbook.proxy.request.specific_type.SpecificTypeDeleteParams;
import com.accountbook.proxy.response.specific_type.SpecificTypeDeleteResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 删除具体类型服务实现
 * 逻辑：查待删具体类型(拿到所属分类) → 确保该分类下存在“无”具体类型(没有则创建)
 *     → 先把该具体类型下所有账单的 specific_type_id 改为“无”
 *     → 再删除该具体类型
 * 注意：不修改 category_id，不影响 budget
 */
public class DeleteSpecificTypeService implements BusinessService<SpecificTypeDeleteParams, SpecificTypeDeleteResponse> {

    private final SpecificTypeDAO specificTypeDAO = DAOFactory.getSpecificTypeDAO();
    private final BillDAO billDAO = DAOFactory.getBillDAO();

    private static final String DEFAULT_SPEC_NAME = "无";

    @Override
    public SpecificTypeDeleteResponse execute(SpecificTypeDeleteParams params) throws Exception {
        System.out.println("执行删除具体类型服务（先把账单具体类型改为“无”）");

        // 1) 取参数并做基本校验
        Integer specificTypeId = params.getId();
        if (specificTypeId == null || specificTypeId <= 0) {
            throw new BusinessServiceException("删除具体类型失败：无效的ID=" + specificTypeId);
        }

        // 2) 查询该具体类型，拿到所属分类ID与名称
        SpecificType toDelete = specificTypeDAO.querySpecificTypeById(specificTypeId);
        if (toDelete == null) {
            throw new BusinessServiceException("删除具体类型失败：未找到ID=" + specificTypeId);
        }
        // 可选保护：避免把“无”本体删了
        if (DEFAULT_SPEC_NAME.equals(toDelete.getName())) {
            throw new BusinessServiceException("删除具体类型失败：默认类型“无”不可删除");
        }
        Integer categoryId = toDelete.getCategoryId();

        // 3) 在同一分类下查找名为“无”的具体类型；没有就创建一条
        Integer defaultSpecificTypeId = null;
        List<SpecificType> list = specificTypeDAO.querySpecificTypesByCategoryId(categoryId);
        if (list != null) {
            for (SpecificType st : list) {
                if (DEFAULT_SPEC_NAME.equals(st.getName())) {
                    defaultSpecificTypeId = st.getId();
                    break;
                }
            }
        }
        if (defaultSpecificTypeId == null) {
            Map<String, Object> insert = new HashMap<>();
            // 这里使用数据库字段名
            insert.put("name", DEFAULT_SPEC_NAME);
            insert.put("category_id", categoryId);
            int newId = specificTypeDAO.addSpecificType(insert);
            if (newId <= 0) {
                throw new BusinessServiceException("删除具体类型失败：创建默认具体类型“无”失败");
            }
            defaultSpecificTypeId = newId;
        }

        // 4) 先迁移账单的具体类型到“无”
        Map<String, Object> queryMap = new HashMap<>();
        // BillDAO 里使用 Java 属性名（会自动映射到 specific_type_id）
        queryMap.put("specificTypeId", specificTypeId);

        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("specificTypeId", defaultSpecificTypeId);

        int affectedBills = billDAO.updateBillsByCondition(queryMap, updateMap);
        if (affectedBills < 0) {
            throw new BusinessServiceException("删除具体类型失败：迁移账单具体类型时发生异常");
        }
        // affectedBills==0 说明本就没有账单指向该具体类型，属正常情况

        // 5) 再删除该具体类型
        int affectRows = specificTypeDAO.deleteSpecificTypeById(specificTypeId);
        if (affectRows <= 0) {
            throw new BusinessServiceException("删除具体类型失败：ID=" + specificTypeId + " 不存在或已被删除");
        }

        // 6) 返回响应：含被删具体类型ID与其所属分类ID（不影响分类与预算）
        return new SpecificTypeDeleteResponse(specificTypeId, categoryId);
    }
}
