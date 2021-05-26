package com.chargerlink.iot.common.codec.rule;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chargerlink.iot.common.codec.constant.*;
import com.chargerlink.iot.common.codec.node.CheckNode;
import com.chargerlink.iot.common.codec.node.FieldNode;
import com.chargerlink.iot.common.codec.node.ListNode;
import com.chargerlink.iot.common.codec.node.MapNode;
import com.chargerlink.iot.common.codec.node.base.Node;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 节点生成器
 *
 * @author GISirFive
 * @date Create on 2020/3/27 15:49
 */
public class NodeGenerator {

    /**
     * 当前规则引擎的业务标记码
     */
    private String protocolId;
    /**
     * 版本号
     */
    private String version;
    /**
     * 已初始化完成的节点集合
     */
    private LinkedHashMap<String, Node> completeNodeMap;

    /**
     * 规则集合中第一个节点
     */
    private Node firstNode;

    /**
     * 生成节点规则链
     *
     * @param protocolId
     * @param version
     * @param rulesValue
     * @param firstNodeName
     * @return
     */
    static NodeGenerator generateNode(String protocolId, String version, JSONObject rulesValue, String firstNodeName) {
        NodeGenerator generator = new NodeGenerator(protocolId, version);
        generator.firstNode = generator.initNodeByLinkValue(firstNodeName, rulesValue);
        return generator;
    }

    /**
     * 已初始化完成的节点集合
     *
     * @return
     */
    LinkedHashMap<String, Node> getCompleteNodeMap() {
        return completeNodeMap;
    }

    /**
     * 获取规则集合中第一个节点
     *
     * @return
     */
    Node getFirstNode() {
        return firstNode;
    }

    /**
     * 获取规则集合中的最后一个节点
     *
     * @return
     */
    Node getLastNode() {
        try {
            Field tail = completeNodeMap.getClass().getDeclaredField("tail");
            tail.setAccessible(true);
            return ((Map.Entry<String, Node>) tail.get(completeNodeMap)).getValue();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Iterator<Map.Entry<String, Node>> iterator = completeNodeMap.entrySet().iterator();
            Map.Entry<String, Node> tail = null;
            while (iterator.hasNext()) {
                tail = iterator.next();
            }
            return tail.getValue();
        }
    }

    private NodeGenerator(String protocolId, String version) {
        this.protocolId = protocolId;
        this.version = version;
        completeNodeMap = new LinkedHashMap<>();
    }

    /**
     * 根据规则链的原始JSON，初始化nodeName节点
     *
     * @param nodeName   节点的名称
     * @param rulesValue 规则集合的原始JSON
     * @return
     */
    private Node initNodeByLinkValue(String nodeName, JSONObject rulesValue) {
        if (StringUtils.isBlank(nodeName)) {
            return null;
        }
        JSONObject nodeValue = rulesValue.getJSONObject(nodeName);
        if (nodeValue == null) {
            return null;
        }
        //同名节点
        if (completeNodeMap.containsKey(nodeName)) {
            throw new IllegalArgumentException(String.format("节点解析失败，检测到同名节点[curr=%s, pre=%s]",
                    JSON.toJSONString(nodeValue), JSON.toJSONString(completeNodeMap.get(nodeName))));
        }
        // 节点类型
        NodeTypeEnum nodeType = NodeTypeEnum.getEnumNyName(nodeValue.getString(NodeKeyConst.NODE_NODE_TYPE));
        if (nodeType == null) {
            throw new IllegalArgumentException(String.format("未识别的节点类型%s[nodeName=%s, nodeValue=%s]",
                    NodeKeyConst.NODE_NODE_TYPE, nodeName, nodeValue.toJSONString()));
        }

        //NOTE 根据节点类型填充节点信息
        Node node = null;
        switch (nodeType) {
            case FIELD:
                node = getFieldNode(nodeName, nodeValue);
                break;
            case MAP:
                node = getMapNode(nodeName, nodeValue, completeNodeMap);
                break;
            case LIST:
                node = getListNode(nodeName, nodeValue, completeNodeMap);
                break;
            case CHECK:
                node = getCheckNode(nodeName, nodeValue);
                break;
            default:
                break;
        }
        if (node == null) {
            throw new IllegalArgumentException(String.format("节点解析失败[nodeName=%s, nodeValue=%s]", nodeName, nodeValue.toJSONString()));
        }
        //当前节点添加到成功的集合中
        completeNodeMap.put(nodeName, node);

        //下一个节点
        String nextNodeName = nodeValue.getString(NodeKeyConst.NODE_NEXT_NODE_NAME);
        if (!StringUtils.isBlank(nextNodeName)) {
            node.setNextNode(initNodeByLinkValue(nextNodeName, rulesValue));
        }
        return node;
    }

    /**
     * 初始化节点的基础信息
     *
     * @param node
     * @param nodeName  节点的名称
     * @param nodeValue 规则集合的原始JSON
     */
    private void initNodeCommonInfo(Node node, String nodeName, JSONObject nodeValue) {
        //节点名称
        node.setNodeName(nodeName);
        // 节点描述
        node.setDescription(nodeValue.getString(NodeKeyConst.NODE_DESCRIPTION));
        // 节点类型
        NodeTypeEnum nodeType = NodeTypeEnum.getEnumNyName(nodeValue.getString(NodeKeyConst.NODE_NODE_TYPE));
        if (nodeType == null) {
            throw new IllegalArgumentException(String.format("未识别的节点类型%s[nodeName=%s, nodeValue=%s]",
                    NodeKeyConst.NODE_NODE_TYPE, nodeName, nodeValue.toJSONString()));
        }
        node.setNodeType(nodeType);
        // 解析单位
        AnalyticalUnitEnum analyticalUnit = AnalyticalUnitEnum.getEnumNyName(nodeValue.getString(NodeKeyConst.NODE_ANALYTICAL_UNIT));
        if (analyticalUnit == null) {
            throw new IllegalArgumentException(String.format("未识别的解析单位%s[nodeName=%s, nodeValue=%s]",
                    NodeKeyConst.NODE_ANALYTICAL_UNIT, nodeName, nodeValue.toJSONString()));
        }
        node.setAnalyticalUnit(analyticalUnit);
    }

    /**
     * 初始化FieldNode
     *
     * @param nodeName  节点的名称
     * @param nodeValue 规则集合的原始JSON
     * @return
     */
    private Node getFieldNode(String nodeName, JSONObject nodeValue) {
        FieldNode node = new FieldNode();
        initNodeCommonInfo(node, nodeName, nodeValue);
        //长度
        Integer length = nodeValue.getInteger(NodeKeyConst.FIELD_NODE_LENGTH);
        if (length == null) {
            throw new NullPointerException(String.format("FIELD节点[%s]解析失败，解析长度[%s]不能为空", nodeName, NodeKeyConst.FIELD_NODE_LENGTH));
        }
        node.setLength(length);
        //如果有变长length，也需要设置
        if (nodeValue.containsKey(NodeKeyConst.FIELD_NODE_VARLENGTH) || StringUtils.isNotBlank(nodeValue.getString(NodeKeyConst.FIELD_NODE_VARLENGTH))) {
            node.setVarLength(nodeValue.getString(NodeKeyConst.FIELD_NODE_VARLENGTH));
        }
        //观察类型
        AtomicHandleTypeEnum handleType = AtomicHandleTypeEnum.getEnumNyName(nodeValue.getString(NodeKeyConst.FIELD_NODE_HANDLE));
        if (handleType == null) {
            //取默认值
            handleType = AtomicHandleTypeEnum.FOCUS;
        }
        node.setAtomicHandleType(handleType);
        //读取方式
        ByteReadOrderEnum readOrder = ByteReadOrderEnum.getEnumNyName(nodeValue.getString(NodeKeyConst.FIELD_NODE_READ_ORDER));
        if (readOrder == null) {
            throw new IllegalArgumentException(String.format("FIELD节点[%s]解析失败，未识别的读取方式[%s]", nodeName, NodeKeyConst.FIELD_NODE_READ_ORDER));
        }
        node.setReadOrder(readOrder);
        // 协议数据类型
        ProtocolDataTypeEnum protocolDataType = ProtocolDataTypeEnum.getEnumNyName(nodeValue.getString(NodeKeyConst.FIELD_NODE_PROTOCOL_DATA_TYPE));
        if (protocolDataType == null) {
            throw new IllegalArgumentException(String.format("FIELD节点[%s]解析失败，未识别的协议数据类型[%s]", nodeName, NodeKeyConst.FIELD_NODE_PROTOCOL_DATA_TYPE));
        }
        node.setProtocolDataType(protocolDataType);
        // 业务数据类型
        BusinessDataTypeEnum businessDataType = BusinessDataTypeEnum.getEnumNyName(nodeValue.getString(NodeKeyConst.FIELD_NODE_BUSINESS_DATA_TYPE));
        if (businessDataType == null) {
            throw new IllegalArgumentException(String.format("FIELD节点[%s]解析失败，未识别的业务数据类型[%s]", nodeName, NodeKeyConst.FIELD_NODE_BUSINESS_DATA_TYPE));
        }
        node.setBusinessDataType(businessDataType);
        // 默认值
        node.setDefaultValue(nodeValue.get(NodeKeyConst.FIELD_NODE_DEFAULT_VALUE));
        // 调整量
        node.setAdjustment(nodeValue.getIntValue(NodeKeyConst.FIELD_NODE_ADJUSTMENT));
        return node;
    }

    /**
     * 初始化MapNode
     *
     * @param nodeName        节点的名称
     * @param nodeValue       当前节点的原始JSON
     * @param completeNodeMap 已初始化完成的节点集合
     * @return
     */
    private Node getMapNode(String nodeName, JSONObject nodeValue, HashMap<String, Node> completeNodeMap) {
        MapNode node = new MapNode();
        initNodeCommonInfo(node, nodeName, nodeValue);
        //参考节点的名称
        Object keySourceNodeName = nodeValue.get(NodeKeyConst.MAP_NODE_KEY_SOURCE_NODE_NAME);
        if (keySourceNodeName == null) {
            throw new NullPointerException(String.format("MAP节点[%s]解析失败，参考节点名称[%s]不能为空", nodeName,
                    NodeKeyConst.MAP_NODE_KEY_SOURCE_NODE_NAME));
        }
        if (keySourceNodeName instanceof String) {
            if (StringUtils.isBlank(keySourceNodeName.toString())) {
                throw new NullPointerException(String.format("MAP节点[%s]解析失败，参考节点名称[%s]不能为空", nodeName,
                        NodeKeyConst.MAP_NODE_KEY_SOURCE_NODE_NAME));
            }
        } else if (keySourceNodeName instanceof JSONArray) {
            if (((JSONArray) keySourceNodeName).isEmpty()) {
                throw new NullPointerException(String.format("MAP节点[%s]解析失败，参考节点名称[%s]不能为空", nodeName,
                        NodeKeyConst.MAP_NODE_KEY_SOURCE_NODE_NAME));
            }
        } else {
            throw new NullPointerException(String.format("MAP节点[%s]解析失败，无法识别的参考节点名称[%s]", nodeName,
                    NodeKeyConst.MAP_NODE_KEY_SOURCE_NODE_NAME));
        }
        //规则链集合
        JSONObject ruleLinksValue = nodeValue.getJSONObject(NodeKeyConst.MAP_NODE_RULE_LINKS);
        if (ruleLinksValue == null || ruleLinksValue.isEmpty()) {
            throw new NullPointerException(String.format("MAP节点[%s]解析失败，规则链集合[%s]不能为空", nodeName,
                    NodeKeyConst.MAP_NODE_RULE_LINKS));
        }
        //NOTE 1-先找到所有的参考节点
        List<FieldNode> sourceNodeList = new LinkedList<>();
        if (keySourceNodeName instanceof String) {
            Node sourceNode = completeNodeMap.get(keySourceNodeName);
            if (sourceNode == null) {
                throw new NullPointerException(String.format("MAP节点[%s]解析失败，根据参考节点名称[%s]未找到对应的参考节点[参考节点必须在当前节点之前解析]",
                        nodeName, keySourceNodeName));
            }
            if (!(sourceNode instanceof FieldNode)) {
                throw new IllegalArgumentException(String.format("MAP节点[%s]解析失败，参考节点的节点类型必须为FIELD[curr=%s]",
                        nodeName, JSON.toJSONString(sourceNode)));
            }
            sourceNodeList.add((FieldNode) sourceNode);
        } else {
            for (Object sourceNodeName : (JSONArray) keySourceNodeName) {
                Node sourceNode = completeNodeMap.get(sourceNodeName.toString());
                if (sourceNode == null) {
                    throw new NullPointerException(String.format("MAP节点[%s]解析失败，根据参考节点名称[%s]未找到对应的参考节点[参考节点必须在当前节点之前解析]",
                            nodeName, sourceNodeName));
                }
                if (!(sourceNode instanceof FieldNode)) {
                    throw new IllegalArgumentException(String.format("MAP节点[%s]解析失败，参考节点的节点类型必须为FIELD[curr=%s]",
                            nodeName, JSON.toJSONString(sourceNode)));
                }
                sourceNodeList.add((FieldNode) sourceNode);
            }
        }
        node.setKeySourceNodeList(sourceNodeList);

        //NOTE 2-再初始化所有的规则链
        Map<String, RuleLink> ruleLinkMap = new HashMap<>(ruleLinksValue.size());
        for (Map.Entry<String, Object> entry : ruleLinksValue.entrySet()) {
            JSONObject ruleLinkJson = (JSONObject) entry.getValue();
            RuleLink ruleLink = new RuleLink(ruleLinkJson, protocolId, version);
            ruleLinkMap.put(entry.getKey(), ruleLink);
        }
        node.setRuleLinkMap(ruleLinkMap);
        return node;
    }

    /**
     * 初始化ListNode
     *
     * @param nodeName        节点的名称
     * @param nodeValue       当前节点的原始JSON
     * @param completeNodeMap 已初始化完成的节点集合
     * @return
     */
    private Node getListNode(String nodeName, JSONObject nodeValue, HashMap<String, Node> completeNodeMap) {
        ListNode node = new ListNode();
        initNodeCommonInfo(node, nodeName, nodeValue);
        //NOTE 循环周期
        //默认循环周期
        Integer cycleDefaultValue = nodeValue.getInteger(NodeKeyConst.LIST_NODE_CYCLE_DEFAULT_VALUE);
        if (cycleDefaultValue == null) {
            //NOTE 如果未设置默认循环周期，则使用参考节点
            String sourceCycleNodeName = nodeValue.getString(NodeKeyConst.LIST_NODE_CYCLE_SOURCE_NODE_NAME);
            if (StringUtils.isBlank(sourceCycleNodeName)) {
                throw new NullPointerException(String.format("LIST节点[%s]解析失败，参考节点名称[%s]不能为空", nodeName,
                        NodeKeyConst.LIST_NODE_CYCLE_SOURCE_NODE_NAME));
            }
            Node sourceNode = completeNodeMap.get(sourceCycleNodeName);
            if (sourceNode == null) {
                throw new NullPointerException(String.format("LIST节点[%s]解析失败，根据参考节点名称[%s]未找到对应的参考节点[参考节点必须在当前节点之前解析]",
                        nodeName, sourceCycleNodeName));
            }
            if (!(sourceNode instanceof FieldNode)) {
                throw new IllegalArgumentException(String.format("LIST节点[%s]解析失败，参考节点的节点类型必须为FIELD[curr=%s]",
                        nodeName, JSON.toJSONString(sourceNode)));
            }
            if (!((FieldNode) sourceNode).getBusinessDataType().isNumeric()) {
                String numericStr = Arrays.stream(BusinessDataTypeEnum.values())
                        .filter(BusinessDataTypeEnum::isNumeric)
                        .map(Enum::name)
                        .reduce((s, s2) -> s + "," + s2)
                        .get();
                throw new IllegalArgumentException(String.format("LIST节点[%s]解析失败，参考节点的业务数据类型必须为数值型[curr=%s][数值型包含:%s]",
                        nodeName, JSON.toJSONString(sourceNode), numericStr));
            }
            node.setCycleSourceNode((FieldNode) sourceNode);
        } else if (cycleDefaultValue <= 0) {
            throw new IllegalArgumentException(String.format("LIST节点[%s]解析失败，默认循环周期必须大于0[curr=%s]",
                    nodeName, cycleDefaultValue));
        } else {
            node.setCycleDefaultValue(cycleDefaultValue);
        }

        // 调整量
        node.setAdjustment(nodeValue.getIntValue(NodeKeyConst.LIST_NODE_ADJUSTMENT));

        //NOTE 规则链
        JSONObject ruleLinkValue = nodeValue.getJSONObject(NodeKeyConst.LIST_NODE_RULE_LINK);
        if (ruleLinkValue == null) {
            throw new NullPointerException(String.format("LIST节点[%s]解析失败，列表节点的规则链[%s]不能为空",
                    nodeName, NodeKeyConst.LIST_NODE_RULE_LINK));
        }
        RuleLink ruleLink = new RuleLink(ruleLinkValue, protocolId, version);
        node.setItem(ruleLink);
        return node;
    }

    /**
     * 初始化CheckNode
     *
     * @param nodeName  节点的名称
     * @param nodeValue 规则集合的原始JSON
     * @return
     */
    private Node getCheckNode(String nodeName, JSONObject nodeValue) {
        CheckNode node = new CheckNode();
        initNodeCommonInfo(node, nodeName, nodeValue);
        //长度
        Integer length = nodeValue.getInteger(NodeKeyConst.CHECK_NODE_LENGTH);
        if (length == null) {
            throw new NullPointerException(String.format("CHECK节点[%s]解析失败，解析长度[%s]不能为空", nodeName, NodeKeyConst.CHECK_NODE_LENGTH));
        }
        node.setLength(length);
        //校验位在报文中的起始解析位置
        node.setStartIndex(nodeValue.getInteger(NodeKeyConst.CHECK_NODE_START_INDEX));
        //校验位计算方式
        CheckTypeEnum checkType = CheckTypeEnum.getEnumNyName(nodeValue.getString(NodeKeyConst.CHECK_NODE_CHECK_TYPE));
        if (checkType == null) {
            throw new IllegalArgumentException(String.format("CHECK节点[%s]解析失败，未识别的校验位计算方式[%s]", nodeName, NodeKeyConst.CHECK_NODE_CHECK_TYPE));
        }
        node.setCheckType(checkType);
        //校验位计算的起始位置
        Integer checkStartIndex = nodeValue.getInteger(NodeKeyConst.CHECK_NODE_CHECK_START_INDEX);
        if (checkStartIndex == null) {
            throw new NullPointerException(String.format("CHECK节点[%s]解析失败，校验位计算的起始位置[%s]不能为空", nodeName, NodeKeyConst.CHECK_NODE_CHECK_START_INDEX));
        }
        node.setCheckStartIndex(checkStartIndex);
        //校验位计算的截至位置
        Integer checkEndIndex = nodeValue.getInteger(NodeKeyConst.CHECK_NODE_CHECK_END_INDEX);
        if (checkEndIndex == null) {
            throw new NullPointerException(String.format("CHECK节点[%s]解析失败，校验位计算的截至位置[%s]不能为空", nodeName, NodeKeyConst.CHECK_NODE_CHECK_END_INDEX));
        }
        node.setCheckEndIndex(checkEndIndex);
        return node;
    }

}
