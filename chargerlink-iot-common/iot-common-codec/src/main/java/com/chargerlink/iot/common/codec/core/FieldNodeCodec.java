package com.chargerlink.iot.common.codec.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chargerlink.iot.common.codec.constant.AnalyticalUnitEnum;
import com.chargerlink.iot.common.codec.constant.AtomicHandleTypeEnum;
import com.chargerlink.iot.common.codec.constant.ByteReadOrderEnum;
import com.chargerlink.iot.common.codec.exception.CodecException;
import com.chargerlink.iot.common.codec.exception.DecodeErrorType;
import com.chargerlink.iot.common.codec.exception.EncodeErrorType;
import com.chargerlink.iot.common.codec.node.FieldNode;
import com.chargerlink.iot.common.codec.rule.NodeGenerator;
import com.chargerlink.iot.common.codec.utils.ByteUtils;
import com.chargerlink.iot.common.codec.utils.SCodecUtils;
import com.chargerlink.iot.common.util.CodeGenerator;
import com.sun.org.apache.xml.internal.utils.StringBufferPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import scodec.bits.BitVector;
import scodec.bits.ByteOrdering;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.regex.Pattern;

import static java.nio.ByteOrder.BIG_ENDIAN;
import static java.nio.ByteOrder.LITTLE_ENDIAN;

/**
 * FieldNode 的编解码工具类
 *
 * @author GISirFive
 * @date Created on 15:44 2019/6/13.
 */
@Slf4j
class FieldNodeCodec {

    /**
     * 数值型正则判断
     */
    private static final Pattern PATTERN_NUMERIC = Pattern.compile("^[-+]?[\\d]*$");
    private static final Pattern PATTERN_FFFF = Pattern.compile("F+",Pattern.CASE_INSENSITIVE);

    /**
     * 解码
     *
     * @param targetNode
     * @param targetData
     * @return
     */
    static Object decode(FieldNode targetNode, BitVector targetData) {
        if (targetData.isEmpty()) {
            throw new CodecException(DecodeErrorType.NONE_TARGET_DATA, targetNode);
        }
        //调整量
        if (targetNode != null && targetNode.getAdjustment() != 0) {
            //每个字节拼接上调整量
            byte[] bytes = targetData.toByteArray();
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = (byte) (bytes[i] + targetNode.getAdjustment());
            }
            targetData = BitVector.view(bytes);
        }
        // 定义转换后long数据
        Long targetLong = Long.MIN_VALUE;
        //读取方式为小端
        if (targetNode.getReadOrder() == ByteReadOrderEnum.LITTLE_END) {
            //解析方式为按字节解析
            if (targetNode.getAnalyticalUnit() == AnalyticalUnitEnum.BYTE) {
                //节点长度大于1
                if (targetNode.getLength() > 1) {
                    //需要反转字节数组
                    targetData = BitVector.view(ByteUtils.reverse(targetData.toByteArray()));
                    targetLong = targetData.toLong(true, ByteOrdering.fromJava(LITTLE_ENDIAN));
                }
            }
        } else {
            targetLong = targetData.toLong(true, ByteOrdering.fromJava(BIG_ENDIAN));
        }
        //NOTE 根据协议数据类型转换
        Object result;
        switch (targetNode.getProtocolDataType()) {
            case BYTE:
            case BIT:
                result = decodeByte(targetNode, targetData);
                break;
            case ASCII:
            case UTF8:
            case UTF16:
            case GBK:
                result = new String(targetData.toByteArray(), Charset.forName(targetNode.getProtocolDataType().charsetName)).trim();
                break;
            case BCD:
            case HEX:
                result = targetData.toHex().toUpperCase().trim();
                break;
            case HEX2DEC:
                result = hexParseSignedDec(targetNode, targetLong);
                break;
            default:
                throw new CodecException(DecodeErrorType.INVALID_PROTOCOL_DATA_TYPE, targetNode);
        }
        if (result == null) {
            //NOTE 解析值为null，直接返回业务默认值
            return targetNode.getDefaultValue();
        }
        //NOTE 根据业务数据类型转换
        switch (targetNode.getBusinessDataType()) {
            case STRING:
                return result.toString();
            case LONG:
                if (result instanceof Long) {
                    return result;
                }
                // 对于无效的FFFF格式的报文，增加兼容
                if (PATTERN_FFFF.matcher(result.toString()).matches()) {
                    return "";
                }
                if (!PATTERN_NUMERIC.matcher(result.toString()).matches()) {
                    log.error("{}无法转换为Long类型[targetNode={}]", targetData.toHex(), JSON.toJSONString(targetNode));
                    throw new CodecException(DecodeErrorType.FAIL_CONVERT_BUSINESS_DATA_TYPE,
                            String.format("%s无法转换为Long类型", targetData.toHex()), targetNode);
                }
                return Long.parseLong(result.toString());
            case INT:
                if (result instanceof Integer) {
                    return result;
                }
                // 对于无效的FFFF格式的报文，增加兼容
                if (PATTERN_FFFF.matcher(result.toString()).matches()) {
                    return "";
                }
                if (!PATTERN_NUMERIC.matcher(result.toString()).matches()) {
                    log.error("{}无法转换为Int类型[targetNode={}]", targetData.toHex(), JSON.toJSONString(targetNode));
                    throw new CodecException(DecodeErrorType.FAIL_CONVERT_BUSINESS_DATA_TYPE,
                            String.format("%s无法转换为Int类型[nodeName=%s]", targetData.toHex(), targetNode.getNodeName()));
                }
                return Integer.parseInt(result.toString());
            default:
                throw new CodecException(DecodeErrorType.INVALID_BUSINESS_DATA_TYPE);
        }
    }

    /**
     * hex转换为带符号的十进制数值
     *
     * @param targetNode
     * @param targetLong
     * @return
     */
    private static Long hexParseSignedDec(FieldNode targetNode, Long targetLong) {
        if (targetLong < 0) {
            return -(0x80 << targetNode.getLength() * 4) - targetLong;
        }
        return targetLong;
    }

    /**
     * Byte解码
     *
     * @param targetNode
     * @param targetData
     * @return
     */
    private static Object decodeByte(FieldNode targetNode, BitVector targetData) {
        switch (targetNode.getBusinessDataType()) {
            case STRING:
                return new String(targetData.toByteArray()).trim();
            case LONG:
                return SCodecUtils.bitToLongBE(targetData, false);
            case INT:
                return SCodecUtils.bitToIntBE(targetData, false);
            default:
                throw new CodecException(DecodeErrorType.INVALID_BUSINESS_DATA_TYPE, targetNode);
        }
    }

    /**
     * 编码
     *
     * @param targetNode
     * @param targetData
     * @return
     */
    static BitVector encode(FieldNode targetNode, JSONObject targetData) {
        if (targetNode.getAtomicHandleType() == AtomicHandleTypeEnum.SKIPING) {
            //NOTE 忽略节点，直接返回
            return BitVector.empty();
        }
        //NOTE 先获取解析值
        String businessValue = null;
        if (StringUtils.isNotBlank(targetData.getString(targetNode.getNodeName()))) {
            businessValue = targetData.getString(targetNode.getNodeName());
        } else if (targetNode.getDefaultValue() != null) {
            //取默认值
            businessValue = targetNode.getDefaultValue().toString();
        }
        if (StringUtils.isBlank(businessValue)) {
            log.error("未找到待编码的数据[targetNode={}]", JSON.toJSONString(targetNode));
            throw new CodecException(EncodeErrorType.NONE_TARGET_DATA);
        }
        BitVector result = null;
        switch (targetNode.getBusinessDataType()) {
            case STRING:
                switch (targetNode.getProtocolDataType()) {
                    case BCD:
                    case HEX:
                        result = BitVector.view(ByteUtils.decodeHexDump(businessValue));
                        break;
                    default:
                        result = encodeCharSequenceValue(businessValue, targetNode);
                        break;
                }
                break;
            case LONG:
            case INT:
                if (!PATTERN_NUMERIC.matcher(businessValue).matches()) {
                    log.error("{}无法转换为数值型[targetNode={}]", businessValue, JSON.toJSONString(targetNode));
                    throw new CodecException(EncodeErrorType.FAIL_CONVERT_BUSINESS_DATA_TYPE,
                            String.format("%s无法转换为数值型[nodeName=%s]", businessValue, targetNode.getNodeName()));
                }
                if (targetNode.getAnalyticalUnit() == AnalyticalUnitEnum.BIT) {
                    result = SCodecUtils.longToBitBE(Long.parseLong(businessValue), targetNode.getLengthOfBit());
                } else {
                    switch (targetNode.getProtocolDataType()) {
                        case BYTE:
                        case BIT:
                            result = SCodecUtils.longToBitBE(Long.parseLong(businessValue), targetNode.getLengthOfBit());
                            break;
                        case HEX2DEC:
                            result = decParseTrueForm(targetNode, businessValue);
                            break;
                        case BCD:
                            result = BitVector.view(ByteUtils.encodeBCDByte(Long.parseLong(businessValue), targetNode.getLength() * 2));
                            break;
                        case HEX:
                            result = BitVector.view(ByteUtils.decodeHexDump(businessValue));
                            break;
                        default:
                            result = encodeCharSequenceValue(businessValue, targetNode);
                            break;
                    }
                }
                break;
            default:
                throw new CodecException(DecodeErrorType.INVALID_PROTOCOL_DATA_TYPE);
        }

        if (result == null) {
            throw new CodecException(EncodeErrorType.NONE_ENCODING_RESULT);
        }
        //补位
        result = encodeFillLength(businessValue, result, targetNode);

        //读取方式为小端
        if (targetNode.getReadOrder() == ByteReadOrderEnum.LITTLE_END) {
            //解析方式为按字节解析
            if (targetNode.getAnalyticalUnit() == AnalyticalUnitEnum.BYTE) {
                //节点长度大于1
                if (targetNode.getLength() > 1) {
                    //需要反转字节数组
                    result = BitVector.view(ByteUtils.reverse(result.toByteArray()));
                }
            }
        }
        //处理调整量
        if (targetNode.getAdjustment() != 0) {
            byte[] bytes = result.toByteArray();
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = (byte) (bytes[i] - targetNode.getAdjustment());
            }
            result = BitVector.view(bytes);
        }
        return result;
    }

    /**
     * 带符号的十进制数值转换为十六进制补码（同余数的补码=原码）
     *
     * @param targetNode
     * @param businessValue
     * @return
     */
    private static BitVector decParseTrueForm(FieldNode targetNode, String businessValue) {
        if (businessValue.startsWith("-")) {
            long l = -(0x80 << targetNode.getLength() * 4) - (Long.parseLong(businessValue)) ;
            return SCodecUtils.longToBitBE(l, targetNode.getLengthOfBit());
        } else {
            return SCodecUtils.longToBitBE(Long.parseLong(businessValue), targetNode.getLengthOfBit());
        }
    }

    /**
     * 字符型编码
     *
     * @param value      代编码的数据
     * @param targetNode
     * @return
     */
    private static BitVector encodeCharSequenceValue(String value, FieldNode targetNode) {
        String charsetName = targetNode.getProtocolDataType().charsetName;
        if (charsetName == null) {
            return BitVector.view(value.getBytes());
        } else {
            try {
                return BitVector.view(value.getBytes(charsetName));
            } catch (UnsupportedEncodingException e) {
                log.error(String.format("根据协议数据类型转换失败[value=%s, charsetName=%s, targetNode=%s]", value, charsetName,
                        JSON.toJSONString(targetNode)), e);
                throw new CodecException(EncodeErrorType.FAIL_CONVERT_PROTOCOL_DATA_TYPE);
            }
        }
    }

    /**
     * 补位
     *
     * @param businessValue
     * @param data
     * @param targetNode
     * @return
     */
    private static BitVector encodeFillLength(String businessValue, BitVector data, FieldNode targetNode) {
        if (targetNode.getAnalyticalUnit() == AnalyticalUnitEnum.BIT) {
            //按位解析时不需要补位
            return data;
        }
        if (data.size() == targetNode.getLengthOfBit()) {
            //节点长度和编码值长度相等时，直接返回
            return data;
        }
        if (targetNode.getLength() == 1) {
            //节点长度为1字节时不需要补位
            return data;
        }
        if (data.size() > targetNode.getLengthOfBit()) {
            log.error("节点原始数据超长[businessValue={}, data={}, targetNode={}]", businessValue, data.toHex(), JSON.toJSONString(targetNode));
            throw new CodecException(EncodeErrorType.DATA_OUT_OF_LENGTH);
        }
        //NOTE 补位
        return SCodecUtils.spliceBitVector(data, BitVector.view(new byte[(int) (targetNode.getLength() - data.length() / 8)]));
    }

    public static Long parseSignedLong(String hex) {
        if (StringUtils.isBlank(hex)) {
            throw new CodecException(EncodeErrorType.NONE_SOURCE_KEY_VALUE);
        }
        String sign = hex.charAt(0) == '0' ? "-" : "+";
        return Long.parseLong(sign + hex.substring(1), 16);
    }

}
