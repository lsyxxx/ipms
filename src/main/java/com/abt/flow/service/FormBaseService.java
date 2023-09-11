package com.abt.flow.service;

import com.abt.common.validator.IValidator;
import com.abt.common.validator.ValidatorChain;

import java.util.List;
import java.util.Map;

public interface FormBaseService {


    /**
     * 添加一个申请表单验证器, 同时更新validationMap
     * @param validator 验证器
     * @return ValidatorChain
     */
    ValidatorChain addApplyFormValidator(IValidator validator);


    /**
     * 添加一组申请表单验证器
     * @param validators 多个验证器，在已有的ValidatorChain后添加
     * @return ValidatorChain
     */
    ValidatorChain addApplyFormValidator(List<IValidator> validators);
    ValidatorChain addApplyFormValidator(IValidator ...validators);


    /**
     * 创建全新的map用来存放构造器，默认添加申请
     * key: 自定义名称
     * value: ValidatorChain
     * @return Map<String, ValidatorChain>
     */
    Map<String, ValidatorChain> createValidationMap();

    Map<String, ValidatorChain> getValidationMap();

    /**
     * 向ValidationMap中追加一组构造器，如果key已存在，则向后追加
     * @param key key
     * @param validators 添加一组构造器
     * @return Map<String, ValidatorChain> 添加后的Map
     */
    Map<String, ValidatorChain> addValidators(String key, List<IValidator> validators);

    /**
     * 向已有的validationMap中添加map，如果存在key重复，则合并ValidatorChain中的validators
     * @param map 要添加的validationMap
     * @return Map<String, ValidatorChain> 追加后的map
     */
    Map<String, ValidatorChain> addValidators(Map<String, ValidatorChain> map);


    /**
     * key对应的value创建一个新的Chain
     * @param key 被清空的key
     */
    void clearMap(String key);




}
