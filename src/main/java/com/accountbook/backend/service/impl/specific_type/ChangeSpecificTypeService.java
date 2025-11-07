package com.accountbook.backend.service.impl.specific_type;

import com.accountbook.backend.common.exception.BusinessServiceException;
import com.accountbook.backend.service.BusinessService;
import com.accountbook.backend.storage.dao.SpecificTypeDAO;
import com.accountbook.backend.storage.dao.factory.DAOFactory;
import com.accountbook.backend.storage.entity.SpecificType;
import com.accountbook.proxy.request.specific_type.SpecificTypeChangeParams;
import com.accountbook.proxy.response.specific_type.SpecificTypeSingleResponse;

import java.util.Map;

/**
 * 具体类型修改业务实现类（参考ChangeBillService、ChangeCategoryService设计风格）
 */
public class ChangeSpecificTypeService implements BusinessService<SpecificTypeChangeParams, SpecificTypeSingleResponse> {

    // 注入具体类型DAO（通过工厂获取，与其他修改服务保持一致）
    private final SpecificTypeDAO specificTypeDAO = DAOFactory.getSpecificTypeDAO();

    @Override
    public SpecificTypeSingleResponse execute(SpecificTypeChangeParams params) throws Exception {
        System.out.println("执行具体类型修改业务");

        // 1. 获取要修改的具体类型ID和更新字段Map
        Integer specificTypeId = params.getSpecificTypeId();
        Map<String, Object> updateMap = params.toMap(); // 仅包含非空的修改字段（如name、categoryId）

        // 2. 调用DAO更新具体类型信息，获取影响行数
        int affectRows = specificTypeDAO.updateSpecificTypeById(specificTypeId, updateMap);

        // 3. 校验更新结果（-1=更新异常，0=未找到对应具体类型）
        if (affectRows == -1 || affectRows == 0) {
            throw new BusinessServiceException("修改具体类型失败：ID=" + specificTypeId + "，可能不存在或更新异常");
        }

        // 4. 查询更新后的具体类型信息（确保返回最新数据）
        SpecificType updatedSpecificType = specificTypeDAO.querySpecificTypeById(specificTypeId);

        // 5. 转换为响应对象并返回
        return SpecificTypeSingleResponse.fromSpecificType(updatedSpecificType);
    }
}