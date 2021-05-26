package com.chargerlink.iot.common.codec.exception;

import com.chargerlink.common.exception.IErrorType;

/**
 * 解码失败时，抛出的错误类型枚举
 *
 * @author GISirFive
 * @date Create on 2019/6/20 17:17
 */
public enum DecodeErrorType implements IErrorType {

    /**
     * 待解码的数据为空
     */
    NONE_TARGET_DATA(1000, "未找到待解码的数据"),
    /**
     * 根据协议数据类型转换失败
     */
    FAIL_CONVERT_PROTOCOL_DATA_TYPE(1001, "根据协议数据类型转换失败"),
    /**
     * 根据业务数据类型转换失败
     */
    FAIL_CONVERT_BUSINESS_DATA_TYPE(1002, "根据业务数据类型转换失败"),
    /**
     * 校验位计算的起始位置越界
     */
    CHECK_START_INDEX_OUT_OF_INDEX(1003, "校验位计算的起始位置越界"),
    /**
     * 校验位计算的截止位置越界
     */
    CHECK_END_INDEX_OUT_OF_INDEX(1004, "校验位计算的截止位置越界"),
    /**
     * 校验位计算的起始位置超出截止位置
     */
    CHECK_START_INDEX_OUT_OF_CHECK_END_INDEX(1005, "校验位计算的起始位置超出截止位置"),
    /**
     * 未找到代表规则链循环周期的参考节点
     */
    NOT_FOUND_CYCLE_SOURCE_NODE(1006, "未找到代表规则链循环周期的参考节点"),
    /**
     * 未获取到循环周期
     */
    NONE_CYCLE_VALUE(1007, "未获取到循环周期"),
    /**
     * 游标越界
     */
    CURSOR_OUT_OF_INDEX(1008, "游标越界"),
    /**
     * 起始解析位置越界
     */
    START_INDEX_OUT_OF_INDEX(1009, "起始解析位置越界"),
    /**
     * 未获取到参考节点的解析值
     */
    NONE_SOURCE_KEY_VALUE(1010, "未获取到参考节点的解析值"),
    /**
     * 根据参考节点未找到规则链
     */
    NOT_FOUND_RULE_LINK_WITH_KEY_NODE(1011, "根据参考节点未找到规则链"),
    /**
     * 请求参数无效
     */
    INVALID_PARAM(1800, "请求参数无效"),
    /**
     * 不支持的节点类型
     */
    INVALID_NODE_TYPE(1801, "不支持的节点类型"),
    /**
     * 解码结果无效
     */
    INVALID_RESULT(1802, "解码结果无效"),
    /**
     * 未识别的业务数据类型
     */
    INVALID_BUSINESS_DATA_TYPE(1803, "未识别的业务数据类型"),
    /**
     * 未识别的协议数据类型
     */
    INVALID_PROTOCOL_DATA_TYPE(1804, "未识别的协议数据类型"),
    /**
     * 不支持的校验类型
     */
    INVALID_CHECK_TYPE(1805, "不支持的校验类型"),
    /**
     * 未知错误
     */
    UNKNOWN_ERROR(1900, "未知错误"),
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

    DecodeErrorType(int errorCode, String errorMessage) {
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
    public static DecodeErrorType getEnumByErrorCode(Integer errorCode) {
        if (errorCode == null) {
            return null;
        }
        for (DecodeErrorType typeEnum : DecodeErrorType.values()) {
            if (errorCode == typeEnum.errorCode) {
                return typeEnum;
            }
        }
        return null;
    }
}
