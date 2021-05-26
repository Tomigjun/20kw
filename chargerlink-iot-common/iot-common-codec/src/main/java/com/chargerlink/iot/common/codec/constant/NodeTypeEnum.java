package com.chargerlink.iot.common.codec.constant;

/**
 * @author GISirFive
 * @date Created on 10:03 2019/4/23.
 */
public enum NodeTypeEnum {
    /**
     * 基础的解析规则，直接将报文解析为属性值
     */
    FIELD,
    /**
     * 校验位节点类型
     */
    CHECK,
    /**
     * 用于描述解析方式会根据某个变量改变的解析规则集合
     */
    LIST,
    /**
     * 用于描述重复解析的规则集合
     */
    MAP,
    //
    ;

    public static NodeTypeEnum getEnumNyName(String name) {
        if (name == null) {
            return null;
        }
        for (NodeTypeEnum typeEnum : NodeTypeEnum.values()) {
            if (typeEnum.name().equals(name)) {
                return typeEnum;
            }
        }
        return null;
    }

}
