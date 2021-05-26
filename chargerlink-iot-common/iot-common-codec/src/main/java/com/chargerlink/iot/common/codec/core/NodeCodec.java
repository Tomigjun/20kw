package com.chargerlink.iot.common.codec.core;

import com.alibaba.fastjson.JSONObject;
import com.chargerlink.iot.common.codec.constant.AtomicHandleTypeEnum;
import com.chargerlink.iot.common.codec.exception.CodecException;
import com.chargerlink.iot.common.codec.exception.DecodeErrorType;
import com.chargerlink.iot.common.codec.exception.EncodeErrorType;
import com.chargerlink.iot.common.codec.node.CheckNode;
import com.chargerlink.iot.common.codec.node.FieldNode;
import com.chargerlink.iot.common.codec.node.ListNode;
import com.chargerlink.iot.common.codec.node.MapNode;
import com.chargerlink.iot.common.codec.node.base.IAtomicNode;
import com.chargerlink.iot.common.codec.node.base.Node;
import com.chargerlink.iot.common.codec.rule.BitCursor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import scodec.bits.BitVector;

/**
 * 总的节点编解码工具类
 *
 * @author GISirFive
 * @date Created on 15:39 2019/6/13.
 */
@Slf4j
public class NodeCodec {

    /**
     * 解码
     *
     * @param targetNode   目标节点
     * @param sourceData   原始数据
     * @param cursor       初始游标
     * @param completeItem 已经解码完成的参数
     * @return
     */
    public static Object decode(Node targetNode, BitVector sourceData, BitCursor cursor, JSONObject completeItem) {
        //根据解码索引位置获取带解码的字节
        switch (targetNode.getNodeType()) {
            case FIELD: {
                FieldNode fieldNode = (FieldNode) targetNode;
                //可变节点需要重新设置length
                if(StringUtils.isNotBlank(fieldNode.getVarLength())) {
                    //TODO::验证长度格式是否正确
                    Integer realLength = completeItem.getInteger(fieldNode.getVarLength());
                    fieldNode.setLength(realLength);
                }
                if (cursor.getCurrentIndex() + ((FieldNode) targetNode).getLengthOfBit() > sourceData.length()) {
                    if (fieldNode.getAtomicHandleType() == AtomicHandleTypeEnum.OPTIONAL) {
                        //NOTE 可选节点，如果游标越界，直接返回null，且游标不移动
                        log.info("游标越界，当前节点为可选节点，跳过解析过程并返回null[nodeName={}]", targetNode.getNodeName());
                        return null;
                    }
                    throw new CodecException(DecodeErrorType.CURSOR_OUT_OF_INDEX);
                }
                if (fieldNode.getAtomicHandleType() == AtomicHandleTypeEnum.HOLDING) {
                    //NOTE 占位节点，只移动游标
                    cursor.addWithNode((IAtomicNode) targetNode);
                    return null;
                }
                if (fieldNode.getAtomicHandleType() == AtomicHandleTypeEnum.SKIPING) {
                    //NOTE 忽略节点，直接返回
                    return null;
                }

                //目标数据
                BitVector targetData = sourceData.slice(cursor.getCurrentIndex(), cursor.getCurrentIndex() + ((FieldNode) targetNode).getLengthOfBit());
                Object result = FieldNodeCodec.decode(fieldNode, targetData);
                //移动游标
                cursor.addWithNode((IAtomicNode) targetNode);
                return result;
            }
            case CHECK: {
                CheckNode checkNode = (CheckNode) targetNode;
                if (checkNode.getStartIndex() == null) {
                    //如果没有手动指定校验位的起始解析位置，则需要判断当前游标是否越界
                    if (cursor.getCurrentIndex() + checkNode.getLengthOfBit() > sourceData.length()) {
                        throw new CodecException(DecodeErrorType.CURSOR_OUT_OF_INDEX);
                    }
                } else if (checkNode.getStartIndex() >= 0) {
                    //如果起始解析位置是从报文头开始索引，则需要判断起始解析位置是否越界
                    if (checkNode.getStartIndex() + checkNode.getLengthOfBit() > sourceData.length()) {
                        throw new CodecException(DecodeErrorType.START_INDEX_OUT_OF_INDEX);
                    }
                }
                boolean result = CheckNodeCodec.decode(checkNode, sourceData, cursor.getCurrentIndex());
                //移动游标
                cursor.addWithNode((IAtomicNode) targetNode);
                return result;
            }
            case LIST:
                return ListNodeCodec.decode((ListNode) targetNode, sourceData, cursor, completeItem);
            case MAP:
                return MapNodeCodec.decode((MapNode) targetNode, sourceData, cursor, completeItem);
            default:
                throw new CodecException(DecodeErrorType.INVALID_NODE_TYPE, targetNode);
        }

    }

    /**
     * 解码
     *
     * @param targetNode 目标节点
     * @param sourceData 原始数据
     * @param cursor     初始游标
     * @return
     */
    public static Object decode(Node targetNode, BitVector sourceData, BitCursor cursor) {
        return decode(targetNode, sourceData, cursor, new JSONObject());
    }

    /**
     * 编码
     *
     * @param targetNode
     * @param sourceData
     * @param completeData 已编码完成的数据
     * @return
     */
    public static BitVector encode(Node targetNode, JSONObject sourceData, BitVector completeData) {
        switch (targetNode.getNodeType()) {
            case FIELD:
                return FieldNodeCodec.encode((FieldNode) targetNode, sourceData);
            case CHECK:
                return CheckNodeCodec.encode((CheckNode) targetNode, completeData);
            case MAP:
                return MapNodeCodec.encode((MapNode) targetNode, sourceData);
            case LIST:
                return ListNodeCodec.encode((ListNode) targetNode, sourceData);
            default:
                throw new CodecException(EncodeErrorType.INVALID_NODE_TYPE, targetNode);
        }
    }
}
