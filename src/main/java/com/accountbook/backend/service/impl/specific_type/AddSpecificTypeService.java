package com.accountbook.backend.service.impl.specific_type;

import com.accountbook.backend.common.exception.BusinessServiceException;
import com.accountbook.backend.service.BusinessService;
import com.accountbook.backend.storage.dao.SpecificTypeDAO;
import com.accountbook.backend.storage.dao.factory.DAOFactory;
import com.accountbook.backend.storage.entity.SpecificType;
import com.accountbook.proxy.request.specific_type.SpecificTypeAddParams;
import com.accountbook.proxy.response.specific_type.SpecificTypeSingleResponse;

import java.util.Map;

/**
 * 新增具体类型服务实现
 * 逻辑：参数转换 → 调用DAO新增 → 校验结果 → 查询新增数据 → 转换为响应
 */
public class AddSpecificTypeService implements BusinessService<SpecificTypeAddParams, SpecificTypeSingleResponse> {

    // 注入具体类型DAO（通过工厂获取，与其他服务保持一致）
    private final SpecificTypeDAO specificTypeDAO = DAOFactory.getSpecificTypeDAO();

    @Override
    public SpecificTypeSingleResponse execute(SpecificTypeAddParams params) throws Exception {
        System.out.println("执行新增具体类型服务");

        // 1. 将请求参数转换为Map（适配DAO的addSpecificType方法）
        Map<String, Object> specificTypeMap = params.toMap();

        // 2. 调用DAO新增具体类型，获取生成的主键ID
        int specificTypeId = specificTypeDAO.addSpecificType(specificTypeMap);

        // 3. 校验新增结果（-1表示新增失败）
        if (specificTypeId == -1) {
            throw new BusinessServiceException("新增具体类型失败：DAO返回异常");
        }

        // 4. 通过主键查询新增的具体类型（确保数据一致性）
        SpecificType newSpecificType = specificTypeDAO.querySpecificTypeById(specificTypeId);

        // 5. 转换为前端响应对象（注意：响应类中字段billId应为specificTypeId，此处按用户定义字段适配）
        return new SpecificTypeSingleResponse(
                newSpecificType.getId(),
                newSpecificType.getName(),
                newSpecificType.getCategoryId()
        );
    }
}