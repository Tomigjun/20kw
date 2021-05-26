package com.chargerlink.iot.common.constant;

import com.chargerlink.common.exception.IErrorType;

/**
 * 消费消息失败时，抛出的错误类型枚举
 *
 * @author GISirFive
 * @date Create on 2019/6/20 17:17
 */
public enum ConsumeErrorTypeEnum implements IErrorType {

    /**
     * MQ内部错误，重试消息
     */
    INTERNAL_ERROR_RETRY(100, "MQ内部错误，重试消息"),
    /**
     * MQ内部错误，丢弃消息
     */
    INTERNAL_ERROR_DROP(200, "MQ内部错误，丢弃消息"),
    /**
     * 业务逻辑错误，重试消息
     */
    BUSINESS_ERROR_RETRY(300, "业务逻辑错误，重试消息"),
    /**
     * 业务逻辑错误，丢弃消息
     */
    BUSINESS_ERROR_DROP(400, "业务逻辑错误，丢弃消息"),
    /**
     * 未知错误，直接丢弃
     */
    UNKNOWN_ERROR(999, "未知错误，直接丢弃"),
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

    ConsumeErrorTypeEnum(int errorCode, String errorMessage) {
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
    public static ConsumeErrorTypeEnum getEnumByErrorCode(Integer errorCode) {
        if (errorCode == null) {
            return null;
        }
        for (ConsumeErrorTypeEnum typeEnum : ConsumeErrorTypeEnum.values()) {
            if (errorCode == typeEnum.errorCode) {
                return typeEnum;
            }
        }
        return null;
    }
}
