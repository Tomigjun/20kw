package com.chargerlink.iot.common.codec.constant;

/**
 * 节点解析单位
 *
 * @author GISirFive
 * @date Created on 9:32 2019/4/23.
 */
public enum AnalyticalUnitEnum {
    /**
     * 字节为单位解析
     */
    BYTE,
    /**
     * BIT为单位解析
     */
    BIT,
    //
    ;

    public static AnalyticalUnitEnum getEnumNyName(String name) {
        if (name == null) {
            return null;
        }
        for (AnalyticalUnitEnum typeEnum : AnalyticalUnitEnum.values()) {
            if (typeEnum.name().equals(name)) {
                return typeEnum;
            }
        }
        return null;
    }
}
