package com.chargerlink.iot.common.codec.node.base;


import com.alibaba.fastjson.annotation.JSONField;
import com.chargerlink.iot.common.codec.constant.AnalyticalUnitEnum;
import com.chargerlink.iot.common.codec.constant.NodeTypeEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * 配置节点
 * 所有节点类型的根节点
 *
 * @author GISirFive
 * @date Created on 9:28 2019/4/23.
 */
@Data
public class Node implements Serializable {

    private static final long serialVersionUID = -5397750643564690945L;

    /**
     * 节点名称
     */
    @JSONField(ordinal = 0)
    private String nodeName;
    /**
     * 节点描述
     */
    @JSONField(ordinal = 1)
    private String description;
    /**
     * 节点类型
     */
    @JSONField(ordinal = 2)
    private NodeTypeEnum nodeType;
    /**
     * 解析单位
     */
    @JSONField(ordinal = 3)
    private AnalyticalUnitEnum analyticalUnit;
    /**
     * 后一个节点，不参与序列化
     */
    @JSONField(ordinal = 99, serialize = false)
    private Node nextNode;
}
