package com.chargerlink.iot.common.codec.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chargerlink.iot.common.codec.exception.CodecException;
import com.chargerlink.iot.common.codec.exception.DecodeErrorType;
import com.chargerlink.iot.common.codec.exception.EncodeErrorType;
import com.chargerlink.iot.common.codec.node.ListNode;
import com.chargerlink.iot.common.codec.rule.BitCursor;
import com.chargerlink.iot.common.codec.rule.RuleEngine;
import com.chargerlink.iot.common.codec.rule.RuleLink;
import com.chargerlink.iot.common.codec.utils.SCodecUtils;
import scodec.bits.BitVector;

/**
 * List节点编解码工具类
 *
 * @author GISirFive
 * @date Created on 15:45 2019/6/13.
 */
class ListNodeCodec {
    /**
     * 解码
     *
     * @param targetNode
     * @param sourceData
     * @param cursor
     * @param completeItem
     * @return
     */
    static JSONArray decode(ListNode targetNode, BitVector sourceData, BitCursor cursor, JSONObject completeItem) {
        //NOTE 计算循环周期
        Integer cycle = targetNode.getCycleDefaultValue();
        if (targetNode.getCycleSourceNode() != null) {
            if (!completeItem.containsKey(targetNode.getCycleSourceNode().getNodeName())) {
                throw new CodecException(DecodeErrorType.NOT_FOUND_CYCLE_SOURCE_NODE, targetNode);
            }
            cycle = completeItem.getIntValue(targetNode.getCycleSourceNode().getNodeName());
        }
        if (cycle == null) {
            throw new CodecException(DecodeErrorType.NONE_CYCLE_VALUE, targetNode);
        }
        //调整量
        if (targetNode.getAdjustment() != 0) {
            cycle += targetNode.getAdjustment();
        }
        JSONArray result = new JSONArray();
        //NOTE 循环解码
        for (int i = 0; i < cycle; i++) {
            RuleLink itemRuleLink = targetNode.getItem();
            JSONObject itemResult = RuleEngine.decode(sourceData, itemRuleLink, cursor);
            result.add(itemResult);
        }
        return result;
    }

    /**
     * 编码
     *
     * @param targetNode
     * @param sourceData
     * @return
     */
    static BitVector encode(ListNode targetNode, JSONObject sourceData) {
        //NOTE 计算循环周期
        Integer cycle = targetNode.getCycleDefaultValue();
        if (targetNode.getCycleSourceNode() != null) {
            if (!sourceData.containsKey(targetNode.getCycleSourceNode().getNodeName())) {
                throw new CodecException(EncodeErrorType.NOT_FOUND_CYCLE_SOURCE_NODE, targetNode);
            }
            cycle = sourceData.getIntValue(targetNode.getCycleSourceNode().getNodeName());
        }
        if (cycle == null) {
            throw new CodecException(EncodeErrorType.NONE_CYCLE_VALUE, targetNode);
        }
        //调整量
        if (targetNode.getAdjustment() != 0) {
            cycle += targetNode.getAdjustment();
        }
        //NOTE 循环编码
        JSONArray itemArray = sourceData.getJSONArray(targetNode.getNodeName());
        if (itemArray.size() != cycle) {
            throw new CodecException(EncodeErrorType.CYCLE_VALUE_NOT_MATCHED_ARRAY_SIZE);
        }
        BitVector result = BitVector.empty();
        for (int i = 0; i < cycle; i++) {
            JSONObject item = itemArray.getJSONObject(i);
            BitVector itemResult = RuleEngine.encode(item, targetNode.getItem());
            if (itemResult == null) {
                throw new CodecException(EncodeErrorType.NONE_ENCODING_RESULT, targetNode);
            }
            result = SCodecUtils.spliceBitVector(result, itemResult);
        }
        return result;
    }
}
