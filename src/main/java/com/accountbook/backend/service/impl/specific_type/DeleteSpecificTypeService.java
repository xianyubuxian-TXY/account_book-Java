package com.accountbook.backend.service.impl.specific_type;

import com.accountbook.backend.common.exception.BusinessServiceException;
import com.accountbook.backend.service.BusinessService;
import com.accountbook.backend.storage.dao.SpecificTypeDAO;
import com.accountbook.backend.storage.dao.factory.DAOFactory;
import com.accountbook.backend.storage.entity.SpecificType;
import com.accountbook.proxy.request.specific_type.SpecificTypeDeleteParams;
import com.accountbook.proxy.response.specific_type.SpecificTypeDeleteResponse;

/**
 * 删除具体类型服务实现
 * 逻辑：查询待删除数据（获取关联大类ID）→ 调用DAO删除 → 校验影响行数 → 转换为响应
 */
public class DeleteSpecificTypeService implements BusinessService<SpecificTypeDeleteParams, SpecificTypeDeleteResponse> {

    // 注入具体类型DAO（通过工厂获取，与其他服务保持一致）
    private final SpecificTypeDAO specificTypeDAO = DAOFactory.getSpecificTypeDAO();

    @Override
    public SpecificTypeDeleteResponse execute(SpecificTypeDeleteParams params) throws Exception {
        System.out.println("执行删除具体类型服务");

        // 1. 从参数中获取要删除的具体类型ID
        Integer specificTypeId = params.getId();

        // 2. 先查询该具体类型，获取关联的大类ID（用于响应返回）
        SpecificType specificType = specificTypeDAO.querySpecificTypeById(specificTypeId);
        Integer categoryId = specificType.getCategoryId(); // 关联的大类ID

        // 3. 调用DAO通过ID删除具体类型，获取影响行数
        int affectRows = specificTypeDAO.deleteSpecificTypeById(specificTypeId);

        // 4. 校验删除结果（-1=异常，0=未找到对应具体类型）
        if (affectRows == -1 || affectRows == 0) {
            throw new BusinessServiceException("删除具体类型失败：ID=" + specificTypeId + "，可能不存在或已被删除");
        }

        // 5. 返回删除成功的响应（包含被删除的具体类型ID和关联的大类ID）
        return new SpecificTypeDeleteResponse(specificTypeId, categoryId);
    }
}