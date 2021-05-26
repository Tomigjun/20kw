package com.chargerlink.iot.common.codec.node;


import com.alibaba.fastjson.annotation.JSONField;
import com.chargerlink.iot.common.codec.constant.*;
import com.chargerlink.iot.common.codec.node.base.IAtomicNode;
import com.chargerlink.iot.common.codec.node.base.Node;
import lombok.Data;

import java.util.Map;

/**
 * Field类型节点.
 * 用来指定BYTE或BIT元素的解析方式，是所有报文结构的基础节点
 *
 * @author GISirFive
 * @date Created on 10:15 2019/4/23.
 */
@Data
public class FieldNode extends Node implements IAtomicNode {
    /**
     * 长度
     */
    @JSONField(ordinal = 10)
    private Integer length;
    /**
     * 读取顺序
     */
    @JSONField(ordinal = 11)
    private ByteReadOrderEnum readOrder;
    /**
     * 协议数据类型
     */
    @JSONField(ordinal = 12)
    private ProtocolDataTypeEnum protocolDataType;
    /**
     * 业务数据类型
     */
    @JSONField(ordinal = 13)
    private BusinessDataTypeEnum businessDataType;
    /**
     * 默认值
     */
    @JSONField(ordinal = 14)
    private Object defaultValue;
    /**
     * 调整量
     */
    @JSONField(ordinal = 15)
    private Integer adjustment;
    /**
     * 观察类型
     */
    @JSONField(ordinal = 16)
    private AtomicHandleTypeEnum atomicHandleType;
    /**
     *
     */
    @JSONField(ordinal = 17)
    private String varLength;
    /**
     * 附加参数
     */
    @Deprecated
    private Map<String, Object> extra;

    @Override
    public int getLength() {
        return this.length;
    }

    @Override
    public int getLengthOfBit() {
        if (AnalyticalUnitEnum.BIT.equals(getAnalyticalUnit())) {
            return this.length;
        } else {
            return this.length * 8;
        }
    }
}
