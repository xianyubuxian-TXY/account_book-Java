package com.accountbook.proxy.helper.impl;

import com.accountbook.proxy.FrontendRequest;
import com.accountbook.proxy.FrontendRequest.RequestType;
import com.accountbook.proxy.helper.BaseRequestHelper;
import com.accountbook.proxy.request.specific_type.SpecificTypeAddParams;
import com.accountbook.proxy.request.specific_type.SpecificTypeChangeParams;
import com.accountbook.proxy.request.specific_type.SpecificTypeDeleteParams;
import com.accountbook.proxy.request.specific_type.SpecificTypeSearchParams;
import com.accountbook.proxy.response.specific_type.SpecificTypeDeleteResponse;
import com.accountbook.proxy.response.specific_type.SpecificTypeListResponse;
import com.accountbook.proxy.response.specific_type.SpecificTypeSingleResponse;

import java.util.List;

/**
 * 具体类型请求助手：封装具体类型（隶属于大类）的请求处理，与其他业务助手保持一致设计风格
 */
public class SpecificTypeRequestHelper extends BaseRequestHelper {

    /**
     * 新增具体类型：返回新增的具体类型详情
     * 业务规则：同一大类下的具体类型名称不可重复，名称长度≤20字符
     */
    public SpecificTypeSingleResponse addSpecificType(String name, Integer categoryId) {
        // 基础参数校验
        validateParamNotNull(name, "具体类型名称不能为空");
        String trimmedName = name.trim();
        validateParamNotNull(categoryId, "关联的大类ID不能为空");
        
        // 格式与合法性校验
        validateParamTrue(!trimmedName.isEmpty(), "具体类型名称不能为空白字符");
        validateParamTrue(trimmedName.length() <= 20, 
                "具体类型名称过长（最大20字符，当前：" + trimmedName.length() + "）");
        validateParamTrue(categoryId > 0, "关联的大类ID必须为正整数（当前值：" + categoryId + "）");
        
        // 唯一性校验：同一大类下不可有同名具体类型
        SpecificTypeListResponse existing = searchSpecificType(trimmedName, categoryId);
        if (existing.getItems() != null && !existing.getItems().isEmpty()) {
            throw new RuntimeException("大类ID=" + categoryId + "下已存在'" + trimmedName + "'，不可重复添加");
        }

        // 发送新增请求
        SpecificTypeAddParams params = new SpecificTypeAddParams(trimmedName, categoryId);
        FrontendRequest<SpecificTypeAddParams> request = new FrontendRequest<>(RequestType.ADD_SPECIFIC_TYPE, params);
        return parseResponse(sendRequest(request));
    }

    /**
     * 修改具体类型：返回修改后的具体类型详情（统一命名为update，与其他助手类保持一致）
     */
    public SpecificTypeSingleResponse updateSpecificType(
            Integer specificTypeId, String newName, Integer newCategoryId) {
        // 基础标识校验
        validateParamNotNull(specificTypeId, "具体类型ID不能为空");
        validateParamTrue(specificTypeId > 0, "具体类型ID必须为正整数（当前值：" + specificTypeId + "）");
        
        // 校验：至少修改一个有效字段
        boolean hasValidUpdate = (newName != null && !newName.trim().isEmpty()) 
                || (newCategoryId != null && newCategoryId > 0);
        validateParamTrue(hasValidUpdate, "至少需要修改一个有效字段（名称或大类ID）");
        
        // 获取原具体类型信息（用于后续校验）
        SpecificTypeSingleResponse original = searchSpecificTypeById(specificTypeId);
        Integer originalCategoryId = original.getCategoryId();
        String originalName = original.getName();
        
        // 处理新名称（若有）
        String trimmedNewName = null;
        if (newName != null) {
            trimmedNewName = newName.trim();
            validateParamTrue(!trimmedNewName.isEmpty(), "新具体类型名称不能为空白字符");
            validateParamTrue(trimmedNewName.length() <= 20, 
                    "新名称过长（最大20字符，当前：" + trimmedNewName.length() + "）");
            
            // 校验：新名称与原名称是否一致（避免无效修改）
            if (trimmedNewName.equals(originalName) && (newCategoryId == null || newCategoryId.equals(originalCategoryId))) {
                throw new RuntimeException("新名称和大类ID与原数据一致，无需修改");
            }
        }
        
        // 处理新大类ID（若有）
        Integer targetCategoryId = (newCategoryId != null) ? newCategoryId : originalCategoryId;
        validateParamTrue(targetCategoryId > 0, "新关联的大类ID必须为正整数（当前值：" + targetCategoryId + "）");
        
        // 唯一性校验：新名称在目标大类下是否已存在（排除自身）
        if (trimmedNewName != null) {
            SpecificTypeListResponse existing = searchSpecificType(trimmedNewName, targetCategoryId);
            if (existing.getItems() != null) {
                for (SpecificTypeSingleResponse type : existing.getItems()) {
                    if (!type.getSpecificTypeId().equals(specificTypeId)) { // 排除自身
                        throw new RuntimeException("大类ID=" + targetCategoryId + "下已存在'" + trimmedNewName + "'");
                    }
                }
            }
        }

        // 发送修改请求
        SpecificTypeChangeParams params = new SpecificTypeChangeParams(specificTypeId, trimmedNewName, newCategoryId);
        FrontendRequest<SpecificTypeChangeParams> request = new FrontendRequest<>(RequestType.CHANGE_SPECIFIC_TYPE, params);
        return parseResponse(sendRequest(request));
    }

    /**
     * 删除具体类型：返回被删除的具体类型信息
     * 业务规则：系统默认具体类型（ID=1）不可删除；存在关联账单时需先处理
     */
    public SpecificTypeDeleteResponse deleteSpecificType(Integer specificTypeId) {
        // 基础标识校验
        validateParamNotNull(specificTypeId, "具体类型ID不能为空");
        validateParamTrue(specificTypeId > 0, "具体类型ID必须为正整数（当前值：" + specificTypeId + "）");
        
        // 系统保护：默认具体类型不可删除
        if (specificTypeId == 1) {
            throw new RuntimeException("系统默认具体类型（ID=1）不可删除");
        }

        // TODO：实际业务中需添加关联校验（示例）
        // 1. 检查是否有关联的账单（通过BillRequestHelper查询）
        // 若存在关联数据，需提示："该具体类型存在XX条关联账单，请先迁移或删除关联数据"

        // 发送删除请求
        SpecificTypeDeleteParams params = new SpecificTypeDeleteParams(specificTypeId);
        FrontendRequest<SpecificTypeDeleteParams> request = new FrontendRequest<>(RequestType.DELETE_SPECIFIC_TYPE, params);
        return parseResponse(sendRequest(request));
    }

    /**
     * 搜索具体类型：支持按名称关键字+大类ID组合查询（同一大类下的模糊匹配）
     */
    public SpecificTypeListResponse searchSpecificType(String nameKey, Integer categoryId) {
        // 可选参数格式校验
        if (categoryId != null) {
            validateParamTrue(categoryId > 0, "大类ID必须为正整数（当前值：" + categoryId + "）");
        }

        SpecificTypeSearchParams params = new SpecificTypeSearchParams(null, nameKey, categoryId);
        FrontendRequest<SpecificTypeSearchParams> request = new FrontendRequest<>(RequestType.SEARCH_SPECIFIC_TYPE, params);
        return parseResponse(sendRequest(request));
    }

    /**
     * 按ID查询具体类型详情
     */
    public SpecificTypeSingleResponse searchSpecificTypeById(Integer specificTypeId) {
        validateParamNotNull(specificTypeId, "具体类型ID不能为空");
        validateParamTrue(specificTypeId > 0, "具体类型ID必须为正整数（当前值：" + specificTypeId + "）");

        SpecificTypeSearchParams params = new SpecificTypeSearchParams(specificTypeId);
        FrontendRequest<SpecificTypeSearchParams> request = new FrontendRequest<>(RequestType.SEARCH_SPECIFIC_TYPE, params);
        SpecificTypeListResponse listResponse = parseResponse(sendRequest(request));

        List<SpecificTypeSingleResponse> specificTypes = listResponse.getItems();
        if (specificTypes == null || specificTypes.isEmpty()) {
            throw new RuntimeException("未找到ID为" + specificTypeId + "的具体类型");
        }
        if (specificTypes.size() > 1) {
            throw new RuntimeException("查询异常：ID为" + specificTypeId + "的具体类型存在" + specificTypes.size() + "条记录");
        }

        return specificTypes.get(0);
    }

    /**
     * 查询所有具体类型（按大类ID+名称正序排列）
     */
    public SpecificTypeListResponse searchAllSpecificTypes() {
        SpecificTypeSearchParams emptyParams = new SpecificTypeSearchParams();
        FrontendRequest<SpecificTypeSearchParams> request = new FrontendRequest<>(RequestType.SEARCH_SPECIFIC_TYPE, emptyParams);
        return parseResponse(sendRequest(request));
    }
}