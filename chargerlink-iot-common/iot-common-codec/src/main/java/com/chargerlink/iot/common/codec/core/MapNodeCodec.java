package com.chargerlink.iot.common.codec.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chargerlink.iot.common.codec.exception.CodecException;
import com.chargerlink.iot.common.codec.exception.DecodeErrorType;
import com.chargerlink.iot.common.codec.exception.EncodeErrorType;
import com.chargerlink.iot.common.codec.node.FieldNode;
import com.chargerlink.iot.common.codec.node.MapNode;
import com.chargerlink.iot.common.codec.rule.BitCursor;
import com.chargerlink.iot.common.codec.rule.RuleEngine;
import com.chargerlink.iot.common.codec.rule.RuleLink;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import scodec.bits.BitVector;

import java.util.List;

/**
 * Map节点编解码工具类
 *
 * @author GISirFive
 * @date Created on 15:44 2019/6/13.
 */
@Slf4j
class MapNodeCodec {
    /**
     * @param targetNode
     * @param sourceData
     * @param cursor
     * @param completeItem
     * @return
     */
    static JSONObject decode(MapNode targetNode, BitVector sourceData, BitCursor cursor, JSONObject completeItem) {
        //先找到key值
        String sourceKey = getSourceKey(targetNode.getKeySourceNodeList(), completeItem);
        if (StringUtils.isBlank(sourceKey)) {
            throw new CodecException(DecodeErrorType.NONE_SOURCE_KEY_VALUE, targetNode);
        }
        //根据key值找到对应的规则链
        RuleLink itemRuleLink = targetNode.getRuleLinkMap().get(sourceKey);
        if (itemRuleLink == null) {
            log.error("根据参考节点未找到规则链[sourceKey={}, targetNode={}]", sourceKey, JSON.toJSONString(targetNode));
            return null;
        }
        return RuleEngine.decode(sourceData, itemRuleLink, cursor);
    }

    /**
     * 编码
     *
     * @param targetNode
     * @param sourceData
     * @return
     */
    static BitVector encode(MapNode targetNode, JSONObject sourceData) {
        //先找到key值
        String sourceKey = getSourceKey(targetNode.getKeySourceNodeList(), sourceData);
        if (StringUtils.isBlank(sourceKey)) {
            throw new CodecException(EncodeErrorType.NONE_SOURCE_KEY_VALUE, targetNode);
        }
        //规则链
        RuleLink ruleLink = targetNode.getRuleLinkMap().get(sourceKey);
        if (ruleLink == null) {
            throw new CodecException(EncodeErrorType.NOT_FOUND_RULE_LINK_WITH_KEY_NODE, targetNode);
        }
        //编码数据
        JSONObject data = sourceData.getJSONObject(targetNode.getNodeName());
        if (data == null) {
            throw new CodecException(EncodeErrorType.NONE_TARGET_DATA, targetNode);
        }
        return RuleEngine.encode(data, ruleLink);
    }

    /**
     * 获取参考节点的解析值
     *
     * @param keySourceNodeList 参考节点列表
     * @param sourceData        参考节点所在的数据集
     * @return
     */
    private static String getSourceKey(List<FieldNode> keySourceNodeList, JSONObject sourceData) {
        StringBuilder sourceKey = new StringBuilder();
        for (int i = 0; i < keySourceNodeList.size(); i++) {
            FieldNode sourceNode = keySourceNodeList.get(i);
            if (!sourceData.containsKey(sourceNode.getNodeName())) {
                log.error("未找到参考节点的解析值[sourceNode={}]", JSON.toJSONString(sourceNode));
                return null;
            }
            String sourceValue = sourceData.getString(sourceNode.getNodeName());
            //NOTE 使用“-”分割拼接多个key
            sourceKey.append(i == 0 ? "" : "-").append(sourceValue);
        }
        return sourceKey.toString();
    }
}
