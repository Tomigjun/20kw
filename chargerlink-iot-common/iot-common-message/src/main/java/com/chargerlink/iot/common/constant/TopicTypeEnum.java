package com.chargerlink.iot.common.constant;

import org.apache.commons.lang3.ArrayUtils;

/**
 * topic类型
 *
 * @author GISirFive
 * @date Create on 2020/3/1 16:05
 */
public enum TopicTypeEnum {

    /**
     * connector与gateway之间的topic
     */
    CONNECTOR_GATEWAY("cn-gt", "gt-cn"),
    /**
     * connector与codec之间的topic
     */
    CONNECTOR_CODEC("cn-cd", "cd-cn"),
    /**
     * gateway与codec之间的topic
     */
    GATEWAY_CODEC("gt-cd", "cd-gt"),
    /**
     * gateway与convert之间的topic
     */
    GATEWAY_CONVERT("gt-cv", "cv-gt"),;

    /**
     * 简称，简称可以有多个
     */
    private String[] aliases;

    TopicTypeEnum(String... aliases) {
        this.aliases = aliases;
    }

    public String[] getAliases() {
        return aliases;
    }

    /**
     * 根据别名获取枚举
     *
     * @param alias
     * @return
     */
    public static TopicTypeEnum getEnumByAlias(String alias) {
        if (alias == null) {
            return null;
        }
        for (TopicTypeEnum typeEnum : TopicTypeEnum.values()) {
            if (ArrayUtils.contains(typeEnum.aliases, alias)) {
                return typeEnum;
            }
        }
        return null;
    }

}
