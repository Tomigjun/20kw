package com.chargerlink.iot.common.connector.entity.convert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.chargerlink.iot.common.connector.config.TCPConnectorProperties;
import com.chargerlink.iot.common.connector.entity.ProtocolInfo;
import com.chargerlink.iot.common.connector.netty.decoder.StartLengthEndFieldFrameDecoder;
import com.chargerlink.iot.common.connector.netty.decoder.StartLengthFieldFrameDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import org.springframework.core.io.Resource;

import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 协议转换
 *
 * @author GISirFive
 * @date Create on 2019/12/4 22:12
 */
public class ProtocolConverter {

    /**
     * @param resource
     * @param config
     * @return
     * @throws Exception
     */
    public static ProtocolInfo convertToProtocolInfo(Resource resource, TCPConnectorProperties.ProtocolConfig config) throws Exception {
        JSONObject configs = JSON.parseObject(resource.getInputStream(), StandardCharsets.UTF_8, JSONObject.class,
                // 自动关闭流
                Feature.AutoCloseSource,
                // 允许注释
                Feature.AllowComment,
                // 允许单引号
                Feature.AllowSingleQuotes,
                // 使用 Big decimal
                Feature.UseBigDecimal);
        if (configs == null || configs.isEmpty()) {
            throw new NullPointerException("协议配置文件中，未找到配置信息");
        }
        JSONObject protocolJson = configs.getJSONObject(config.getProtocolId());
        if (protocolJson == null) {
            throw new NullPointerException(String.format("协议配置文件中，位找到指定的协议配置信息[protocolId=%s]", config.getProtocolId()));
        }
        ProtocolInfo protocol = protocolJson.toJavaObject(ProtocolInfo.class);
        protocol.setProtocolId(config.getProtocolId());
        return protocol;
    }

    /**
     * 获取当前节点粘包拆包的解决方案
     *
     * @return
     */
    public static ByteToMessageDecoder convertToMessageDecoder(ProtocolInfo protocol) throws Exception {
        if (protocol.getMessageAnalyseType() == null) {
            throw new NullPointerException("未找到包结构枚举类型");
        }
        if (protocol.getMessageAnalyseParam() == null) {
            throw new NullPointerException("未找到包结构参数");
        }
        switch (protocol.getMessageAnalyseType()) {
            case START_AND_LENGTH_FIELD:
                return buildStartLengthFieldFrameDecoder(protocol.getMessageAnalyseParam());
            case DELIMITER_BASED:
                return buildDelimiterBasedFrameDecoder(protocol.getMessageAnalyseParam());
            case START_AND_LENGTH_AND_END_FIELD:
                return buildStartLengthEndFieldFrameDecoder(protocol.getMessageAnalyseParam());
            default:
                throw new IllegalArgumentException("未找到匹配的协议解析器");
        }
    }

    /**
     * 创建StartLengthFieldFrameDecoder解码器
     *
     * @param parameter
     * @return
     */
    private static StartLengthFieldFrameDecoder buildStartLengthFieldFrameDecoder(Map<String, Object> parameter) {
        //读取方式-大端小端-默认大端
        ByteOrder byteOrder = getByteOrderByString((String) parameter.get("byteOrder"));
        //处理的最大报文长度
        Integer maxFrameLength = (Integer) parameter.get("maxFrameLength");
        if (maxFrameLength == null) {
            throw new NullPointerException("最大报文长度配置不可为空");
        }
        //基础报文的长度-当长度位的值仅包含数据域时，需要指定基础报文的长度来获取完成的报文信息
        Integer baseFrameLength = (Integer) parameter.get("baseFrameLength");
        if (baseFrameLength == null) {
            throw new NullPointerException("基础报文的长度配置不可为空");
        }
        //长度位起始位置
        Integer lengthFieldOffset = (Integer) parameter.get("lengthFieldOffset");
        if (lengthFieldOffset == null) {
            throw new NullPointerException("长度位起始位置配置不可为空");
        }
        //长度位的长度
        Integer lengthFieldLength = (Integer) parameter.get("lengthFieldLength");
        if (lengthFieldLength == null) {
            throw new NullPointerException("长度位的长度配置不可为空");
        }
        //起始标记位的数组形式
        String parameterString = (String) parameter.get("startFieldByteBuf");
        ByteBuf startFieldByte = getByteArrayByJson(parameterString);
        if (startFieldByte.readableBytes() == 0) {
            throw new NullPointerException("起始标记位的数组不可为空");
        }
//        log.debug("使用起始域长度解码器[parameter = {}]", parameter);
        return new StartLengthFieldFrameDecoder(byteOrder, maxFrameLength, baseFrameLength, lengthFieldOffset, lengthFieldLength, startFieldByte);
    }

    /**
     * 创建StartLengthEndFieldFrameDecoder解码器
     *
     * @param parameter
     * @return
     */
    private static StartLengthEndFieldFrameDecoder buildStartLengthEndFieldFrameDecoder(Map<String, Object> parameter) {
        //读取方式-大端小端-默认大端
        ByteOrder byteOrder = getByteOrderByString((String) parameter.get("byteOrder"));
        //处理的最大报文长度
        Integer maxFrameLength = (Integer) parameter.get("maxFrameLength");
        if (maxFrameLength == null) {
            throw new NullPointerException("最大报文长度配置不可为空");
        }
        //基础报文的长度-当长度位的值仅包含数据域时，需要指定基础报文的长度来获取完成的报文信息
        Integer baseFrameLength = (Integer) parameter.get("baseFrameLength");
        if (baseFrameLength == null) {
            throw new NullPointerException("基础报文的长度配置不可为空");
        }
        //长度位起始位置
        Integer lengthFieldOffset = (Integer) parameter.get("lengthFieldOffset");
        if (lengthFieldOffset == null) {
            throw new NullPointerException("长度位起始位置配置不可为空");
        }
        //长度位的长度
        Integer lengthFieldLength = (Integer) parameter.get("lengthFieldLength");
        if (lengthFieldLength == null) {
            throw new NullPointerException("长度位的长度配置不可为空");
        }
        //起始标记位的数组形式
        String parameterString = (String) parameter.get("startFieldByteBuf");
        ByteBuf startFieldByte = getByteArrayByJson(parameterString);
        if (startFieldByte.readableBytes() == 0) {
            throw new NullPointerException("起始标记位的数组不可为空");
        }
        //结束标记位的数组形式
        String endFameString = (String) parameter.get("endFieldByteBuf");
        ByteBuf endFieldByte = getByteArrayByJson(endFameString);
        if (startFieldByte.readableBytes() == 0) {
            throw new NullPointerException("结束标记位的数组不可为空");
        }
        return new StartLengthEndFieldFrameDecoder(byteOrder, maxFrameLength, baseFrameLength, lengthFieldOffset, lengthFieldLength, startFieldByte, endFieldByte);
    }

    /**
     * 构建DelimiterBasedFrameDecoder类型解码器
     *
     * @param parameter
     * @return
     */
    private static DelimiterBasedFrameDecoder buildDelimiterBasedFrameDecoder(Map<String, Object> parameter) {
        int maxFrameLength = (int) parameter.get("maxFrameLength");
        String parameterString = (String) parameter.get("delimiters");
        ByteBuf delimitersBytes = getByteArrayByJson(parameterString);
//        log.debug("使用固定结尾解码器[maxFrameLength = {}，delimitersBuf = {}]", maxFrameLength, ByteBufUtil.hexDump(delimitersBytes));
        return new DelimiterBasedFrameDecoder(maxFrameLength, false, delimitersBytes);
    }

    /**
     * 根据字符名称获取ByteOrder
     *
     * @param byteOrderStr
     * @return
     */
    private static ByteOrder getByteOrderByString(String byteOrderStr) {
        if (ByteOrder.LITTLE_ENDIAN.toString().equals(byteOrderStr)) {
            return ByteOrder.LITTLE_ENDIAN;
        } else {
            return ByteOrder.BIG_ENDIAN;
        }
    }

    /**
     * 将int型的json数组转为byte[]
     *
     * @param hexBytes
     * @return
     */
    private static ByteBuf getByteArrayByJson(String hexBytes) {
        byte[] fieldByte = ByteBufUtil.decodeHexDump(hexBytes);
        ByteBuf byteBuf = Unpooled.buffer(fieldByte.length);
        return byteBuf.writeBytes(fieldByte);
    }
}
