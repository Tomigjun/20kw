package com.chargerlink.iot.common.codec.rule;

import com.chargerlink.iot.common.codec.node.base.IAtomicNode;

/**
 * 已Bit为单位的游标
 *
 * @author GISirFive
 * @date Create on 2020/4/3 14:59
 */
public class BitCursor {

    /**
     * 当前游标位置
     */
    private int cursor;

    public BitCursor(int cursor) {
        this.cursor = cursor;
    }

    /**
     * 增加步长
     *
     * @param step
     * @return
     */
    public int add(int step) {
        cursor += step;
        return cursor;
    }

    /**
     * 增加Byte单位的步长
     *
     * @param step
     * @return
     */
    public int addWithByte(int step) {
        cursor += step * 8;
        return cursor;
    }

    /**
     * 增加单个节点的步长
     *
     * @param node
     * @return
     */
    public int addWithNode(IAtomicNode node) {
        return add(node.getLengthOfBit());
    }

    /**
     * 获取当前游标的位置
     *
     * @return
     */
    public int getCurrentIndex() {
        return cursor;
    }

}
