package com.chargerlink.iot.common.codec.rule;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.chargerlink.iot.common.codec.constant.RuleLinkKeyConst;
import com.chargerlink.iot.common.codec.node.base.Node;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * 规则链表
 * 封装编解码消息
 *
 * @author GISirFive
 * @date Created on 16:36 2019/6/14.
 */
@Slf4j
@Getter
public class RuleLink implements Serializable {

    private static final long serialVersionUID = 6876719942911076552L;

    /**
     * 规则链描述
     */
    @JSONField(ordinal = 0)
    private String description;
    /**
     * 当前规则链的协议ID
     */
    @JSONField(ordinal = 1)
    private String protocolId;
    /**
     * 版本号
     */
    @JSONField(ordinal = 2)
    private String version;
    /**
     * 设备ID的字段名称
     */
    @JSONField(ordinal = 3)
    private String deviceIdNodeName;
    /**
     * 首个节点
     */
    @JSONField(ordinal = 4)
    private Node firstNode;
    /**
     * 最后节点
     */
    @JSONField(ordinal = 5)
    private Node lastNode;
    /**
     * 当前规则链中所有规则的集合
     */
    @JSONField(ordinal = 6)
    private LinkedHashMap<String, Node> nodeMap;

    /**
     * @param ruleLinkValue 规则链的原始JSON
     */
    public RuleLink(JSONObject ruleLinkValue) {
        this(ruleLinkValue, null, null);
    }

    /**
     * @param ruleLinkValue 规则链的原始JSON
     * @param protocolId    协议ID
     * @param version       版本
     */
    public RuleLink(JSONObject ruleLinkValue, String protocolId, String version) {
        if (StringUtils.isBlank(protocolId)) {
            protocolId = ruleLinkValue.getString(RuleLinkKeyConst.PROTOCOL_ID);
        }
        this.protocolId = protocolId;
        if (version == null) {
            version = ruleLinkValue.getString(RuleLinkKeyConst.VERSION);
        }
        this.version = version;
        init(ruleLinkValue);
    }

    private void init(JSONObject ruleLinkValue) {
        if (StringUtils.isBlank(protocolId)) {
            throw new NullPointerException(String.format("规则链初始化失败，未找到协议ID[rule=%s]", ruleLinkValue.toJSONString()));
        }
        //规则链描述
        description = ruleLinkValue.getString(RuleLinkKeyConst.DESCRIPTION);
        //设备ID的节点名称
        deviceIdNodeName = ruleLinkValue.getString(RuleLinkKeyConst.DEVICE_ID_NODE_NAME);
        //当前规则链中所有规则的集合
        nodeMap = new LinkedHashMap<>(ruleLinkValue.size());
        //规则
        JSONObject rulesValue = ruleLinkValue.getJSONObject(RuleLinkKeyConst.RULES);
        if (rulesValue == null) {
            throw new NullPointerException(String.format("规则链初始化失败，未找到规则配置[rule=%s]", ruleLinkValue.toJSONString()));
        }
        //获取规则集合中第一个节点的key
        String firstNodeKey = ruleLinkValue.getString(RuleLinkKeyConst.FIRST_NODE_NAME);
        if (StringUtils.isBlank(firstNodeKey)) {
            throw new NullPointerException(String.format("规则链初始化失败，未找到首节点的名称[rule=%s]", ruleLinkValue.toJSONString()));
        }

        //初始化规则链
        NodeGenerator generator = NodeGenerator.generateNode(protocolId, version, rulesValue, firstNodeKey);
        nodeMap = generator.getCompleteNodeMap();
        firstNode = generator.getFirstNode();
        lastNode = generator.getLastNode();
        if (firstNode == null) {
            throw new IllegalArgumentException(String.format("规则链初始化失败，根据首节点名称未找到首节点的配置[protocolId=%s, %s=%s]",
                    protocolId, RuleLinkKeyConst.FIRST_NODE_NAME, firstNodeKey));
        }
    }

    /**
     * 根据名称获取指定节点
     *
     * @param nodeName
     * @return
     */
    public Node getNodeByName(String nodeName) {
        if (nodeMap == null) {
            return null;
        }
        return nodeMap.get(nodeName);
    }
}
