package com.chargerlink.iot.common.connector.entity;

import com.chargerlink.iot.common.connector.constant.MessageAnalyseTypeEnum;
import lombok.Data;

import java.util.Map;

/**
 * 协议信息
 *
 * @author GISirFive
 * @date Create on 2019/12/4 21:43
 */
@Data
public class ProtocolInfo {

    /**
     * 协议编码
     */
    private String protocolId;
    /**
     * 配置描述
     */
    private String description;
    /**
     * 包结构枚举类型-粘包拆包解决方式
     */
    private MessageAnalyseTypeEnum messageAnalyseType;
    /**
     * 包结构参数
     */
    private Map<String, Object> messageAnalyseParam;
}
