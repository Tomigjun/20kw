package com.chargerlink.iot.common.codec.constant;

/**
 * node节点中固定的key名称
 *
 * @author GISirFive
 * @date Create on 2020/3/27 15:00
 */
public interface RuleLinkKeyConst {

    /**
     * 规则链描述
     */
    String DESCRIPTION = "description";
    /**
     * 当前规则链的协议ID
     */
    String PROTOCOL_ID = "protocolId";
    /**
     * 协议版本号
     */
    String VERSION = "version";
    /**
     * 存放设备ID的节点名称
     */
    String DEVICE_ID_NODE_NAME = "deviceIdNodeName";
    /**
     * 规则集合中第一个节点的名称
     */
    String FIRST_NODE_NAME = "firstNodeName";
    /**
     * 规则集合
     */
    String RULES = "rules";

}
