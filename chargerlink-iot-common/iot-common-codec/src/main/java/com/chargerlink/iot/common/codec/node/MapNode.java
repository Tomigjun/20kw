package com.chargerlink.iot.common.codec.node;

import com.alibaba.fastjson.annotation.JSONField;
import com.chargerlink.iot.common.codec.node.base.IAggregationNode;
import com.chargerlink.iot.common.codec.node.base.Node;
import com.chargerlink.iot.common.codec.rule.RuleLink;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 字典节点类型，用于配置随不同字节值变化报文结构有变化的结构。
 *
 * @author GISirFive
 * @date Created on 10:47 2019/4/23.
 */
@Data
public class MapNode extends Node implements IAggregationNode {

    /**
     * 代表规则链key值的参考节点列表
     */
    @JSONField(ordinal = 10)
    private List<FieldNode> keySourceNodeList;
    /**
     * 规则链集合，key为规则链的名称，value为该节点下的若干规则链{@link RuleLink}
     */
    @JSONField(ordinal = 11)
    private Map<String, RuleLink> ruleLinkMap;
}
