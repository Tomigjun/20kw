package com.chargerlink.iot.common.util;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.HashMap;
import java.util.Random;

/**
 * 编号生成器
 *
 * @author GISirFive
 * @since 2017年6月26日下午4:20:16
 */
public class CodeGenerator {

    private static final char[] CHARS = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
            'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    /**
     * 生成链路ID
     *
     * @return
     */
    public static String generateTraceId() {
        return SystemClock.now() + "-" + generateNumberCode(15);
    }

    /**
     * 生成messageId
     *
     * @return
     */
    public static String generateMessageId() {
        return SystemClock.now() + "-" + generateNumberCode(15);
    }

    /**
     * 生成messageId
     *
     * @param codeType
     * @return
     */
    public static String generateMessageId(CodeType codeType) {
        return codeType.prefix + "-" + SystemClock.now() + "-" + generateNumberCode(15);
    }

    /**
     * 生成节点ID
     *
     * @return
     */
    public static String generateNodeId() {
        return generateNumberCode(4);
    }

    /**
     * 生成编号
     *
     * @param length 长度
     * @return
     */
    private static String generateNumberCode(int length) {
        return RandomStringUtils.randomNumeric(length);
    }

    /**
     * 生成字母
     *
     * @param length
     * @return
     */
    private static String generateCharCode(int length) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            buffer.append(CHARS[new Random().nextInt(CHARS.length)]);
        }
        return buffer.toString();
    }

    /**
     * 编号类型
     *
     * @author GISirFive
     * @since 2017年6月26日下午4:23:28
     */
    public enum CodeType {
        /**
         * rock消息
         */
        MESSAGE_ROCKET("RKT", ""),
        /**
         * kafka消息
         */
        MESSAGE_KAFKA("KFK", ""),
        /**
         * emq消息
         */
        MESSAGE_EMQ("EMQ", ""),
        //
        ;

        /**
         * 前缀
         */
        private String prefix;
        /**
         * 后缀
         */
        private String suffix;

        CodeType(String prefix, String suffix) {
            this.prefix = prefix;
            this.suffix = suffix;
        }

    }
}