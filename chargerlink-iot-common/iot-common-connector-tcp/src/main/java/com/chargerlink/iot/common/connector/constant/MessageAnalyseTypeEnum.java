package com.chargerlink.iot.common.connector.constant;

/**
 * 报文包结构枚举-用来描述报文解决粘包和拆包的方式
 *
 * @author GISirFive
 * @date Created on 15:01 2019/6/10.
 */
public enum MessageAnalyseTypeEnum {

    /**
     * 根据起始位和长度位
     */
    START_AND_LENGTH_FIELD(1),
    /**
     * 根据指定分隔符
     */
    DELIMITER_BASED(2),
    /**
     * 根据起始位和长度位和结束位
     */
    START_AND_LENGTH_AND_END_FIELD(3),
    //
    ;

    /**
     * 编码
     */
    private int code;

    MessageAnalyseTypeEnum(int code) {
        this.code = code;
    }
}
