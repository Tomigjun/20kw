package com.chargerlink.iot.common.codec.node;

import com.alibaba.fastjson.annotation.JSONField;
import com.chargerlink.iot.common.codec.node.base.IAggregationNode;
import com.chargerlink.iot.common.codec.node.base.Node;
import com.chargerlink.iot.common.codec.rule.RuleLink;
import lombok.Data;

/**
 * 列表节点类型。用于解析重复出现的报文结构
 *
 * @author GISirFive
 * @date Created on 10:43 2019/4/23.
 */
@Data
public class ListNode extends Node implements IAggregationNode {

    /**
     * 代表规则链循环周期的参考节点
     */
    @JSONField(ordinal = 10)
    private FieldNode cycleSourceNode;
    /**
     * 默认循环周期
     */
    private Integer cycleDefaultValue;
    /**
     * 调整量
     */
    @JSONField(ordinal = 11)
    private Integer adjustment;
    /**
     * 列表节点的规则链
     */
    @JSONField(ordinal = 12)
    private RuleLink item;
}
