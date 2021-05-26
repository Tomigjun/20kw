package com.chargerlink.iot.common.codec.rule;

import com.alibaba.fastjson.JSONObject;
import com.chargerlink.iot.common.codec.constant.NodeKeyConst;
import com.chargerlink.iot.common.codec.core.NodeCodec;
import com.chargerlink.iot.common.codec.node.base.Node;
import com.chargerlink.iot.common.codec.exception.CodecException;
import com.chargerlink.iot.common.codec.utils.SCodecUtils;
import lombok.extern.slf4j.Slf4j;
import scodec.bits.BitVector;

/**
 * 规则引擎，实现消息编解码方法的调用
 *
 * @author GISirFive
 * @date Created on 9:45 2019/6/17.
 */
@Slf4j
public class RuleEngine {


    /**
     * 解码
     *
     * @param data     解码数据
     * @param ruleLink 规则链
     * @return
     */
    public static JSONObject decode(byte[] data, RuleLink ruleLink) throws CodecException {
        return decode(data, ruleLink, 0);
    }

    /**
     * 解码
     *
     * @param data       解码数据
     * @param ruleLink   规则链
     * @param startIndex 起始位置，即从data中的第几个字节开始解码
     * @return
     */
    public static JSONObject decode(byte[] data, RuleLink ruleLink, int startIndex) throws CodecException {
        return decode(BitVector.view(data), ruleLink, new BitCursor(startIndex));
    }

    /**
     * 解码
     *
     * @param sourceData 待解码的Bit类型的集合
     * @param ruleLink   规则链
     * @param cursor     初始游标
     * @return
     */
    public static JSONObject decode(BitVector sourceData, RuleLink ruleLink, BitCursor cursor) throws CodecException {
        //已经解码完成的参数
        JSONObject completeItem = new JSONObject();
        //当前待解析的节点，默认从首节点开始
        Node currentNode = ruleLink.getFirstNode();
        //开始遍历解析
        while (currentNode != null) {
            log.debug("开始解析节点[nodeName={}, {}={}]", currentNode.getNodeName(), NodeKeyConst.NODE_DESCRIPTION, currentNode.getDescription());
            Object result = NodeCodec.decode(currentNode, sourceData, cursor, completeItem);
            if (result != null) {
                completeItem.put(currentNode.getNodeName(), result);
            }
            //继续下一节点
            currentNode = currentNode.getNextNode();
        }
        return completeItem;
    }

    /**
     * 解析指定的Node节点
     *
     * @param data       数据
     * @param sourceNode 原始节点
     * @param startIndex 起始位置，即从data中的第几个字节开始解码
     * @return
     */
    public static JSONObject decodeByNode(byte[] data, Node sourceNode, int startIndex) throws CodecException {
        Object value = NodeCodec.decode(sourceNode, BitVector.view(data), new BitCursor(startIndex * 8));
        return new JSONObject().fluentPut(sourceNode.getNodeName(), value);
    }

    /**
     * 消息编码
     *
     * @param data     编码数据
     * @param ruleLink 规则链
     * @return
     */
    public static BitVector encode(JSONObject data, RuleLink ruleLink) throws CodecException {
        Node node = ruleLink.getFirstNode();
        if (node == null) {
            log.debug("未找到ruleLink中的节点信息");
            return null;
        }
        BitVector completeData = BitVector.empty();
        while (node != null) {
            log.debug("{}节点开始编码[nodeName = {}, nodeValue = {}]", node.getNodeType(), node.getNodeName(), data.get(node.getNodeName()));
            BitVector itemData = NodeCodec.encode(node, data, completeData);
            log.debug("{}节点编码完成[nodeName = {}, nodeBytes = {}]", node.getNodeType(), node.getNodeName(), itemData.toHex());
            completeData = SCodecUtils.spliceBitVector(completeData, itemData);
            node = node.getNextNode();
        }
        return completeData;
    }

}
