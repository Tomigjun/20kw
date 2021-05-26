package com.chargerlink.iot.common.codec.constant;

/**
 * 描述原子节点的处理类型，决定如何解析
 *
 * @author GISirFive
 * @date Create on 2020/4/15 10:53
 */
public enum AtomicHandleTypeEnum {

    /**
     * （默认）聚焦节点，表示该节点必须解析，如果解析异常则会使用默认值，如果没有默认值则解析失败并返回指定错误码
     */
    FOCUS,
    /**
     * 占位节点，表示该节点的作用只用来占据报文中固定一段长度，在编码结果中不包含该节点的解析值，在解码结果中使用该节点的业务默认，常用来处理协议中包含冗余字段的情况
     */
    HOLDING,
    /**
     * 可选节点，即可解析可不解析，该类型设置规则如下：<br>
     * <ul>
     * <li>当报文中不包含该节点描述的字段时，节点长度忽略</li>
     * <li>当报文中包含该节点描述的字段时，该节点参与解析，当解析异常或解析值为空时，使用默认值</li>
     * <li>常用来处理同一类型的报文中字段数量不固定的情况</li>
     * </ul>
     */
    OPTIONAL,
    /**
     * 忽略节点，不需要解析的节点，该类型设置规则如下：<br>
     * <ul>
     * <li>当报文中不包含该节点描述的字段时，节点长度忽略</li>
     * <li>当报文中包含该节点描述的字段时，该节点参与解析，当解析异常或解析值为空时，使用默认值</li>
     * <li>常用来处理同一类型的报文中字段数量不固定的情况</li>
     * </ul>
     */
    SKIPING
    ;

    public static AtomicHandleTypeEnum getEnumNyName(String name) {
        if (name == null) {
            return null;
        }
        for (AtomicHandleTypeEnum typeEnum : AtomicHandleTypeEnum.values()) {
            if (typeEnum.name().equals(name)) {
                return typeEnum;
            }
        }
        return null;
    }
}
