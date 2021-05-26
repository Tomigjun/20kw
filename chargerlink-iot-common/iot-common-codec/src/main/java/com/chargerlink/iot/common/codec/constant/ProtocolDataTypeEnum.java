package com.chargerlink.iot.common.codec.constant;

import org.apache.commons.lang3.StringUtils;

/**
 * 协议数据类型
 *
 * @author GISirFive
 * @date Created on 10:18 2019/4/23.
 */
public enum ProtocolDataTypeEnum {
    /**
     * BYTE
     **/
    BYTE(getDefaultCharsetName()),
    /**
     * ASCII
     **/
    ASCII("ASCII"),
    /**
     * UTF8
     **/
    UTF8("UTF-8"),
    /**
     * UTF16
     **/
    UTF16("UTF-16"),
    /**
     * GBK
     **/
    GBK("GBK"),
    /**
     * BIT
     **/
    BIT(null),
    /**
     * BCD
     **/
    BCD(null),
    /**
     * HEX
     */
    HEX(null),
    HEX2DEC(null),
    //
    ;

    /**
     * 编码名称
     */
    public String charsetName;

    ProtocolDataTypeEnum(String charsetName) {
        this.charsetName = charsetName;
    }

    public static ProtocolDataTypeEnum getEnumNyName(String name) {
        if (name == null) {
            return null;
        }
        for (ProtocolDataTypeEnum typeEnum : ProtocolDataTypeEnum.values()) {
            if (typeEnum.name().equals(name)) {
                return typeEnum;
            }
        }
        return null;
    }

    /**
     * 获取系统默认编码方式
     *
     * @return
     */
    private static String getDefaultCharsetName() {
        String encoding = System.getProperty("file.encoding");
        if (StringUtils.isBlank(encoding)) {
            encoding = "UTF-8";
        }
        return encoding;
    }
}
