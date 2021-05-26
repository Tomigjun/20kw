package com.chargerlink.iot.common.codec.constant;

/**
 * 报文校验方式枚举
 *
 * @author GISirFive
 * @date Created on 14:57 2019/6/20.
 */
public enum CheckTypeEnum {
    /**
     * 异或校验-校验范围从命令单元的第一个字节开始，同后一字节异或，直到校验码前一字节为止
     */
    BCC,
    /**
     * 校验和-校验和为一个字节，由类型码开始到校验和之前的所有各字节进行二进制算数累加之和，不计超过FFH的溢出值;
     */
    SUM,
    /**
     * CRC校验
     */
    CRC,
    //
    ;

    public static CheckTypeEnum getEnumNyName(String name) {
        if (name == null) {
            return null;
        }
        for (CheckTypeEnum typeEnum : CheckTypeEnum.values()) {
            if (typeEnum.name().equals(name)) {
                return typeEnum;
            }
        }
        return null;
    }
}
