package com.chargerlink.iot.common.codec.exception;

import com.chargerlink.common.exception.IErrorType;

/**
 * 编码失败时，抛出的错误类型枚举
 *
 * @author GISirFive
 * @date Create on 2019/6/20 17:17
 */
public enum EncodeErrorType implements IErrorType {
    /**
     * 未找到待编码的数据
     */
    NONE_TARGET_DATA(2000, "未找到待编码的数据"),
    /**
     * 根据协议数据类型转换失败
     */
    FAIL_CONVERT_PROTOCOL_DATA_TYPE(2001, "根据协议数据类型转换失败"),
    /**
     * 根据业务数据类型转换失败
     */
    FAIL_CONVERT_BUSINESS_DATA_TYPE(2002, "根据业务数据类型转换失败"),
    /**
     * 校验位计算的起始位置越界
     */
    CHECK_START_INDEX_OUT_OF_INDEX(2003, "校验位计算的起始位置越界"),
    /**
     * 校验位计算的截止位置越界
     */
    CHECK_END_INDEX_OUT_OF_INDEX(2004, "校验位计算的截止位置越界"),
    /**
     * 校验位计算的起始位置超出截止位置
     */
    CHECK_START_INDEX_OUT_OF_CHECK_END_INDEX(2005, "校验位计算的起始位置超出截止位置"),
    /**
     * 未找到代表规则链循环周期的参考节点
     */
    NOT_FOUND_CYCLE_SOURCE_NODE(2006, "未找到代表规则链循环周期的参考节点"),
    /**
     * 循环周期和列表长度不匹配
     */
    CYCLE_VALUE_NOT_MATCHED_ARRAY_SIZE(2007, "循环周期和列表长度不匹配"),
    /**
     * 未获取到循环周期
     */
    NONE_CYCLE_VALUE(2008, "未获取到循环周期"),
    /**
     * 节点原始数据超长
     */
    DATA_OUT_OF_LENGTH(2009, "节点原始数据超长"),
    /**
     * 节点编码结果为空
     */
    NONE_ENCODING_RESULT(2010, "节点编码结果为空"),
    /**
     * 未获取到参考节点的解析值
     */
    NONE_SOURCE_KEY_VALUE(2011, "未获取到参考节点的解析值"),
    /**
     * 根据参考节点未找到规则链
     */
    NOT_FOUND_RULE_LINK_WITH_KEY_NODE(1011, "根据参考节点未找到规则链"),
    /**
     * 请求参数无效
     */
    INVALID_PARAM(2800, "请求参数无效"),
    /**
     * 不支持的节点类型
     */
    INVALID_NODE_TYPE(2801, "不支持的节点类型"),
    /**
     * 编码结果无效
     */
    INVALID_RESULT(2802, "编码结果无效"),
    /**
     * 不支持的协议数据类型
     */
    INVALID_PROTOCOL_DATA_TYPE(2803, "不支持的协议数据类型"),
    /**
     * 不支持的业务数据类型
     */
    INVALID_BUSINESS_DATA_TYPE(2804, "不支持的业务数据类型"),
    /**
     * 不支持的校验类型
     */
    INVALID_CHECK_TYPE(2805, "不支持的校验类型"),
    /**
     * 未知错误
     */
    UNKNOWN_ERROR(2900, "未知错误"),
    //
    ;

    /**
     * 错误码
     */
    private int errorCode;
    /**
     * 错误描述
     */
    private String errorMessage;

    EncodeErrorType(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @Override
    public Integer getErrorCode() {
        return errorCode;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * 根据错误码获取对应的枚举
     *
     * @param errorCode
     * @return
     */
    public static EncodeErrorType getEnumByErrorCode(Integer errorCode) {
        if (errorCode == null) {
            return null;
        }
        for (EncodeErrorType typeEnum : EncodeErrorType.values()) {
            if (errorCode == typeEnum.errorCode) {
                return typeEnum;
            }
        }
        return null;
    }
}
