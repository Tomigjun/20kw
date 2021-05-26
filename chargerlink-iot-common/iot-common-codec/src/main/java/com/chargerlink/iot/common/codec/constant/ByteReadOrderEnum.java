package com.chargerlink.iot.common.codec.constant;

/**
 * 节点读取方式
 *
 * @author GISirFive
 * @date Created on 10:11 2019/4/23.
 */
public enum ByteReadOrderEnum {
    /**
     * 大端读取
     */
    BIG_END,
    /**
     * 小端读取
     */
    LITTLE_END,
    //
    ;

    public static ByteReadOrderEnum getEnumNyName(String name) {
        if (name == null) {
            return null;
        }
        for (ByteReadOrderEnum typeEnum : ByteReadOrderEnum.values()) {
            if (typeEnum.name().equals(name)) {
                return typeEnum;
            }
        }
        return null;
    }
}
