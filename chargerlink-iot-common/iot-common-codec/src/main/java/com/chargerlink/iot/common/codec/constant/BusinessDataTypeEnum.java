package com.chargerlink.iot.common.codec.constant;

/**
 * 业务数据类型
 *
 * @author GISirFive
 * @date Created on 10:22 2019/4/23.
 */
public enum BusinessDataTypeEnum {
    /**
     * String
     */
    STRING(false),
    /**
     * Long
     */
    LONG(true),
    /**
     * Int
     */
    INT(true),
    //
    ;
    /**
     * 是否为数值型
     */
    private Boolean numeric;

    BusinessDataTypeEnum(boolean numeric) {
        this.numeric = numeric;
    }

    /**
     * 是否为数值型
     *
     * @return
     */
    public Boolean isNumeric() {
        return numeric;
    }

    public static BusinessDataTypeEnum getEnumNyName(String name) {
        if (name == null) {
            return null;
        }
        for (BusinessDataTypeEnum typeEnum : BusinessDataTypeEnum.values()) {
            if (typeEnum.name().equals(name)) {
                return typeEnum;
            }
        }
        return null;
    }
}
