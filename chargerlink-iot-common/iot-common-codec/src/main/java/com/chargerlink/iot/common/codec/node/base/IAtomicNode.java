package com.chargerlink.iot.common.codec.node.base;

import com.chargerlink.iot.common.codec.constant.AtomicHandleTypeEnum;

/**
 * 原子节点，即不可拆分的最小的解析节点
 *
 * @author GISirFive
 * @date Create on 2020/4/3 16:35
 */
public interface IAtomicNode {

    /**
     * 获取节点长度
     *
     * @return
     */
    int getLength();

    /**
     * 按位获取节点长度
     *
     * @return
     */
    int getLengthOfBit();

    /**
     * 处理类型{@link AtomicHandleTypeEnum}
     *
     * @return
     */
    AtomicHandleTypeEnum getAtomicHandleType();

}
