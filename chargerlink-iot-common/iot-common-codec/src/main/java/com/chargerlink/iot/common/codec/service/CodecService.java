package com.chargerlink.iot.common.codec.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chargerlink.common.util.AssertUtils;
import com.chargerlink.iot.common.codec.exception.CodecException;
import com.chargerlink.iot.common.codec.exception.DecodeErrorType;
import com.chargerlink.iot.common.codec.exception.EncodeErrorType;
import com.chargerlink.iot.common.codec.node.base.IAtomicNode;
import com.chargerlink.iot.common.codec.node.base.Node;
import com.chargerlink.iot.common.codec.rule.RuleEngine;
import com.chargerlink.iot.common.codec.rule.RuleLink;
import com.chargerlink.iot.common.codec.service.config.RuleLinkManager;
import com.chargerlink.iot.common.codec.service.entity.DecodeResult;
import com.chargerlink.iot.common.codec.service.entity.EncodeResult;
import com.chargerlink.iot.common.codec.utils.ByteUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scodec.bits.BitVector;

/**
 * 通用的编解码服务
 *
 * @author GISirFive
 * @date Created on 15:07 2019/6/17.
 */
@Slf4j
@Service
public class CodecService {

    @Autowired
    private RuleLinkManager ruleLinkManager;


    /**
     * 消息解码，开始解码的字节位置默认为0
     *
     * @param data       待解码的原始数据
     * @param protocolId 解码使用的协议ID
     * @return
     */
    public DecodeResult decode(String protocolId, byte[] data) {
        return decode(protocolId, data, 0);
    }

    /**
     * 消息解码
     *
     * @param protocolId 解码使用的协议ID
     * @param data       待解码的原始数据
     * @param startIndex 开始解码的字节位置
     * @return
     */
    public DecodeResult decode(String protocolId, byte[] data, int startIndex) {
        log.debug("消息解码开始[protocolId={}, data={}, startIndex={}]", protocolId, ByteUtils.toHexString(data), startIndex);
        //NOTE 参数校验
        if (StringUtils.isBlank(protocolId)) {
            return DecodeResult.failure(DecodeErrorType.INVALID_PARAM, "协议ID为空");
        }
        AssertUtils.throwBlank(protocolId, "protocolId不能为空");
        if (data == null || data.length == 0) {
            return DecodeResult.failure(DecodeErrorType.INVALID_PARAM, "待解码的数据为空");
        }
        RuleLink ruleLink = ruleLinkManager.getRuleLink(protocolId);
        if (ruleLink == null) {
            return DecodeResult.failure(DecodeErrorType.INVALID_PARAM, String.format("根据协议ID[%s]未找到协议信息", protocolId));
        }
        //NOTE 开始解码
        try {
            JSONObject decode = RuleEngine.decode(data, ruleLink, startIndex);
            if (decode == null) {
                return DecodeResult.failure(DecodeErrorType.INVALID_RESULT);
            }
            log.debug("消息解码成功[protocolId={}, data={}, result={}]", protocolId, ByteUtils.toHexString(data), decode.toJSONString());
            return DecodeResult.success(decode, decode.getString(ruleLink.getDeviceIdNodeName()));
        } catch (CodecException e) {
            if (e.getErrorNode() == null) {
                log.error(String.format("消息解码失败[protocolId=%s, data=%s]", protocolId, ByteUtils.toHexString(data)), e);
            } else if (e.getErrorNode() instanceof IAtomicNode) {
                log.error(String.format("消息解码失败[protocolId=%s, data=%s, errorNode=%s]", protocolId, ByteUtils.toHexString(data), JSON.toJSON(e.getErrorNode())), e);
            } else {
                log.error(String.format("消息解码失败[protocolId=%s, data=%s, errorNodeName=%s]", protocolId, ByteUtils.toHexString(data), e.getErrorNode().getNodeName()), e);
            }
            return DecodeResult.failure(e);
        } catch (Exception ex) {
            log.error(String.format("消息解码失败[protocolId=%s, data=%s]", protocolId, ByteUtils.toHexString(data)), ex);
            return DecodeResult.failure(DecodeErrorType.UNKNOWN_ERROR, ex.getMessage());
        }
    }

    /**
     * 指定节点解码，开始解码的字节位置默认为0
     *
     * @param protocolId 解码使用的协议ID
     * @param data       待解码的原始数据
     * @param nodeName   解码使用的节点名称
     * @return
     */
    public DecodeResult decodeByNode(String protocolId, byte[] data, String nodeName) {
        log.debug("消息解码开始[protocolId={}, data={}, nodeName={}]", protocolId, ByteUtils.toHexString(data), nodeName);
        //NOTE 参数校验
        if (StringUtils.isBlank(protocolId)) {
            return DecodeResult.failure(DecodeErrorType.INVALID_PARAM, "协议ID为空");
        }
        AssertUtils.throwBlank(protocolId, "protocolId不能为空");
        if (data == null || data.length == 0) {
            return DecodeResult.failure(DecodeErrorType.INVALID_PARAM, "待解码的数据为空");
        }
        RuleLink ruleLink = ruleLinkManager.getRuleLink(protocolId);
        if (ruleLink == null) {
            return DecodeResult.failure(DecodeErrorType.INVALID_PARAM, String.format("根据协议ID[%s]未找到协议信息", protocolId));
        }
        Node node = ruleLink.getNodeByName(nodeName);
        if (node == null) {
            return DecodeResult.failure(DecodeErrorType.INVALID_RESULT);
        }
        //NOTE 开始解码
        try {
            JSONObject decode = RuleEngine.decodeByNode(data, node, 0);
            if (decode == null) {
                return DecodeResult.failure(DecodeErrorType.INVALID_RESULT);
            }
            log.debug("消息解码成功[protocolId={}, data={}, result={}]", protocolId, ByteUtils.toHexString(data), decode.toJSONString());
            return DecodeResult.success(decode, decode.getString(ruleLink.getDeviceIdNodeName()));
        } catch (CodecException e) {
            if (e.getErrorNode() == null) {
                log.error(String.format("消息解码失败[protocolId=%s, data=%s]", protocolId, ByteUtils.toHexString(data)), e);
            } else if (e.getErrorNode() instanceof IAtomicNode) {
                log.error(String.format("消息解码失败[protocolId=%s, data=%s, errorNode=%s]", protocolId, ByteUtils.toHexString(data), JSON.toJSON(e.getErrorNode())), e);
            } else {
                log.error(String.format("消息解码失败[protocolId=%s, data=%s, errorNodeName=%s]", protocolId, ByteUtils.toHexString(data), e.getErrorNode().getNodeName()), e);
            }
            return DecodeResult.failure(e);
        } catch (Exception ex) {
            log.error(String.format("消息解码失败[protocolId=%s, data=%s]", protocolId, ByteUtils.toHexString(data)), ex);
            return DecodeResult.failure(DecodeErrorType.UNKNOWN_ERROR, ex.getMessage());
        }
    }

    /**
     * 消息编码
     *
     * @param protocolId 编码使用的协议ID
     * @param data       待编码的原始数据
     * @return
     */
    public EncodeResult encode(String protocolId, JSONObject data) {
        log.debug("消息编码开始[protocolId={}, data={}]", protocolId, data);
        //NOTE 参数校验
        if (StringUtils.isBlank(protocolId)) {
            return EncodeResult.failure(EncodeErrorType.INVALID_PARAM, "协议ID为空");
        }
        if (data == null || data.isEmpty()) {
            return EncodeResult.failure(EncodeErrorType.INVALID_PARAM, "待解码的数据不可为空");
        }
        RuleLink ruleLink = ruleLinkManager.getRuleLink(protocolId);
        if (ruleLink == null) {
            return EncodeResult.failure(EncodeErrorType.INVALID_PARAM, String.format("根据协议ID[%s]未找到协议信息", protocolId));
        }
        //NOTE 开始编码
        try {
            BitVector encode = RuleEngine.encode(data, ruleLink);
            if (encode == null) {
                return EncodeResult.failure(EncodeErrorType.INVALID_RESULT);
            }
            log.info("消息编码成功[protocolId={}, data={}, result={}]", protocolId, data.toJSONString(), encode.toHex());
            return EncodeResult.success(encode.toByteArray());
        } catch (CodecException e) {
            if (e.getErrorNode() == null) {
                log.error(String.format("消息编码失败[protocolId=%s, data=%s]", protocolId, data.toJSONString()), e);
            } else if (e.getErrorNode() instanceof IAtomicNode) {
                log.error(String.format("消息编码失败[protocolId=%s, data=%s, errorNode=%s]", protocolId, data.toJSONString(), JSON.toJSON(e.getErrorNode())), e);
            } else {
                log.error(String.format("消息编码失败[protocolId=%s, data=%s, errorNodeName=%s]", protocolId, data.toJSONString(), e.getErrorNode().getNodeName()), e);
            }
            return EncodeResult.failure(e);
        } catch (Exception ex) {
            log.error(String.format("消息解码失败[protocolId=%s, data=%s]", protocolId, data.toJSONString()), ex);
            return EncodeResult.failure(EncodeErrorType.UNKNOWN_ERROR, ex.getMessage());
        }
    }

}
