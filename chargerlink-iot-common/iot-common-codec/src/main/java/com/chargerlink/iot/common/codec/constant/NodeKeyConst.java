package com.chargerlink.iot.common.codec.constant;

import com.chargerlink.iot.common.codec.node.CheckNode;
import com.chargerlink.iot.common.codec.node.FieldNode;
import com.chargerlink.iot.common.codec.node.ListNode;
import com.chargerlink.iot.common.codec.node.MapNode;
import com.chargerlink.iot.common.codec.node.base.Node;
import com.chargerlink.iot.common.codec.rule.RuleLink;

/**
 * node节点中固定的key名称
 *
 * @author GISirFive
 */
public interface NodeKeyConst {

    /**↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓{@link Node}↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓**/
    /**
     * (选填)节点描述
     */
    String NODE_DESCRIPTION = "description";
    /**
     * 节点类型
     */
    String NODE_NODE_TYPE = "nodeType";
    /**
     * 解析单位
     */
    String NODE_ANALYTICAL_UNIT = "analyticalUnit";
    /**
     * 下一个节点的名称
     */
    String NODE_NEXT_NODE_NAME = "nextNodeName";

    /**↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓{@link FieldNode}↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓**/
    /**
     * 解析长度
     */
    String FIELD_NODE_LENGTH = "length";
    /**
     * 变长长度
     */
    String FIELD_NODE_VARLENGTH = "varLength";
    /**
     * 读取顺序
     */
    String FIELD_NODE_READ_ORDER = "readOrder";
    /**
     * 协议数据类型
     */
    String FIELD_NODE_PROTOCOL_DATA_TYPE = "protocolDataType";
    /**
     * 业务数据类型
     */
    String FIELD_NODE_BUSINESS_DATA_TYPE = "businessDataType";
    /**
     * (选填)业务默认值
     */
    String FIELD_NODE_DEFAULT_VALUE = "defaultValue";
    /**
     * (选填)调整量
     */
    String FIELD_NODE_ADJUSTMENT = "adjustment";
    /**
     * (选填)处理类型，描述该节点是否需要解析，取值范围及设置规则参考{@link AtomicHandleTypeEnum}
     */
    String FIELD_NODE_HANDLE = "handle";

    /**↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓{@link MapNode}↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓**/
    /**
     * key值参考节点的名称，参考节点的解析值将作为规则链集合中每一个规则链的key值，规则如下：<br>
     * <ul>
     * <li>参考节点的节点类型必须为FIELD</li>
     * <li>参考节点必须在规则链集合之前配置</li>
     * <li>参考节点可以配置多个(用数组配置)，规则链的key值匹配规则为：按照参考节点的配置顺序，将解析值以'-'拼接</li>
     * </ul>
     * <b>example</b>
     * <pre><b>
     * {
     *      "keySourceNodeName": "a",  //单个参考点
     *      "ruleLinks": {
     *          "节点a的解析值": ....
     *      }
     * }
     * {
     *      "keySourceNodeName": ["a", "b"],   //多个参考点
     *      "ruleLinks": {
     *          "节点a的解析值-节点b的解析值": ....
     *      }
     * }
     * </b></pre>
     */
    String MAP_NODE_KEY_SOURCE_NODE_NAME = "keySourceNodeName";
    /**
     * 规则链集合，key为规则链的名称，value为该节点下的若干规则链{@link RuleLink}
     */
    String MAP_NODE_RULE_LINKS = "ruleLinks";

    /**↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓{@link ListNode}↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓**/
    /**
     * 代表规则链循环周期的参考节点名称，参考节点的解析值将作为循环解析规则链的次数，规则如下：<br>
     * 1-参考节点的节点类型必须为FIELD<br>
     * 2-参考节点的业务数据类型必须为数值型<br>
     * 3-参考节点必须在规则链集合之前配置<br>
     */
    String LIST_NODE_CYCLE_SOURCE_NODE_NAME = "cycleSourceNodeName";
    /**
     * (选填)默认循环周期
     */
    String LIST_NODE_CYCLE_DEFAULT_VALUE = "cycleDefaultValue";
    /**
     * (选填)调整量
     */
    String LIST_NODE_ADJUSTMENT = "adjustment";
    /**
     * 列表节点的规则链
     */
    String LIST_NODE_RULE_LINK = "ruleLink";

    /**↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓{@link CheckNode}↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓**/
    /**
     * 长度
     */
    String CHECK_NODE_LENGTH = "length";
    /**
     * (选填)校验位在报文中的起始解析位置，该属性配置条件如下：<br>
     * <ul>
     * <li>当同一类型的报文其中的节点数量不定时，建议设置该属性为true，否则会影响后续节点的解析</li>
     * <li>该属性为非负数时，会从报文头开始索引；否则从报文尾开始索引</li>
     * </ul>
     */
    String CHECK_NODE_START_INDEX = "startIndex";
    /**
     * 校验位的计算方式
     */
    String CHECK_NODE_CHECK_TYPE = "checkType";
    /**
     * 校验位计算的起始位置<br>
     * 该属性为非负数时，会从报文头开始索引；否则从报文尾开始索引
     */
    String CHECK_NODE_CHECK_START_INDEX = "checkStartIndex";
    /**
     * 校验位计算的截至位置<br>
     * 该属性为非负数时，会从报文头开始索引；否则从报文尾开始索引
     */
    String CHECK_NODE_CHECK_END_INDEX = "checkEndIndex";

}
