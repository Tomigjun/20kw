package com.chargerlink.iot.common.connector.constant;

import com.chargerlink.common.exception.IErrorType;

/**
 * TCP消息错误枚举
 *
 * @author GISirFive
 * @date Create on 2019/6/20 17:17
 */
public enum TCPErrorTypeEnum implements IErrorType {

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

    TCPErrorTypeEnum(int errorCode, String errorMessage) {
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
    public static TCPErrorTypeEnum getEnumByErrorCode(Integer errorCode) {
        if (errorCode == null) {
            return null;
        }
        for (TCPErrorTypeEnum typeEnum : TCPErrorTypeEnum.values()) {
            if (errorCode == typeEnum.errorCode) {
                return typeEnum;
            }
        }
        return null;
    }
}
