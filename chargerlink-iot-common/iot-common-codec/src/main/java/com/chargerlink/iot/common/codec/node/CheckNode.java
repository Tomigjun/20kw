package com.chargerlink.iot.common.codec.node;

import com.alibaba.fastjson.annotation.JSONField;
import com.chargerlink.iot.common.codec.constant.AnalyticalUnitEnum;
import com.chargerlink.iot.common.codec.constant.AtomicHandleTypeEnum;
import com.chargerlink.iot.common.codec.constant.CheckTypeEnum;
import com.chargerlink.iot.common.codec.node.base.IAtomicNode;
import com.chargerlink.iot.common.codec.node.base.Node;
import lombok.Data;

/**
 * 校验位节点-定义报文的校验方式
 *
 * @author GISirFive
 * @date Created on 9:28 2019/7/15.
 */
@Data
public class CheckNode extends Node implements IAtomicNode {
    /**
     * 校验位长度
     */
    @JSONField(ordinal = 10)
    private int length;
    /**
     * 校验位在报文中的起始解析位置
     */
    @JSONField(ordinal = 11)
    private Integer startIndex;
    /**
     * 校验位计算方式
     */
    @JSONField(ordinal = 12)
    private CheckTypeEnum checkType;
    /**
     * 校验位计算的起始位置
     */
    @JSONField(ordinal = 13)
    private int checkStartIndex;
    /**
     * 校验位计算的截止位置
     */
    @JSONField(ordinal = 14)
    private int checkEndIndex;

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

    @Override
    public AtomicHandleTypeEnum getAtomicHandleType() {
        return AtomicHandleTypeEnum.FOCUS;
    }
}
