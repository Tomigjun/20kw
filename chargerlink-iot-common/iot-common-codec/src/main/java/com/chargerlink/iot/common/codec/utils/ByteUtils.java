package com.chargerlink.iot.common.codec.utils;

import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.text.NumberFormat;

/**
 * 字节码处理工具类
 *
 * @author GISirFive
 * @date Created on 17:14 2019/6/13.
 */
public class ByteUtils {

    private static final String[] BYTE2HEX_PAD = new String[256];
    private static final String[] BYTE2HEX_NOPAD = new String[256];

    /**
     * 字节数组反转
     *
     * @param data
     * @return
     */
    public static byte[] reverse(byte[] data) {
        int length = data.length;
        byte[] rev = new byte[length];
        for (int i = 0; i < length; i++) {
            rev[length - i - 1] = data[i];
        }
        return rev;
    }

    /**
     * 拼接两个数组
     *
     * @param sourceByte
     * @param operateByte
     * @return
     */
    public static byte[] spliceByteArray(byte[] sourceByte, byte[] operateByte) {
        return ArrayUtils.addAll(sourceByte, operateByte);
    }

    /**
     * 拼接两个数组
     *
     * @param sourceByte
     * @param operateByte
     * @return
     */
    public static byte[] spliceByteArray(byte[] sourceByte, byte operateByte) {
        return ArrayUtils.add(sourceByte, operateByte);
    }

    /**
     * 将整型数值编码为指定长度的BCD格式的byte数组
     *
     * @param value
     * @param length 长度
     * @return
     */
    public static byte[] encodeBCDByte(long value, int length) {
        NumberFormat numberFormat = initNumberFormat(length, length);
        String bcdValue = numberFormat.format(value);
        return decodeHexDump(bcdValue);
    }

    private static NumberFormat initNumberFormat(int maximum, int minimum) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setGroupingUsed(false);
        numberFormat.setMaximumIntegerDigits(maximum);
        numberFormat.setMinimumIntegerDigits(minimum);
        return numberFormat;
    }

    /**
     * Helper to decode half of a hexadecimal number from a string.
     *
     * @param c The ASCII character of the hexadecimal number to decode.
     *          Must be in the range {@code [0-9a-fA-F]}.
     * @return The hexadecimal value represented in the ASCII character
     * given, or {@code -1} if the character is invalid.
     */
    public static int decodeHexNibble(final char c) {
        // Character.digit() is not used here, as it addresses a larger
        // set of characters (both ASCII and full-width latin letters).
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'A' && c <= 'F') {
            return c - ('A' - 0xA);
        }
        if (c >= 'a' && c <= 'f') {
            return c - ('a' - 0xA);
        }
        return -1;
    }

    /**
     * Decode a 2-digit hex byte from within a string.
     */
    public static byte decodeHexByte(CharSequence s, int pos) {
        int hi = decodeHexNibble(s.charAt(pos));
        int lo = decodeHexNibble(s.charAt(pos + 1));
        if (hi == -1 || lo == -1) {
            throw new IllegalArgumentException(String.format(
                    "invalid hex byte '%s' at index %d of '%s'", s.subSequence(pos, pos + 2), pos, s));
        }
        return (byte) ((hi << 4) + lo);
    }

    /**
     * Decodes part of a string with <a href="http://en.wikipedia.org/wiki/Hex_dump">hex dump</a>
     *
     * @param hexDump   a {@link CharSequence} which contains the hex dump
     * @param fromIndex start of hex dump in {@code hexDump}
     * @param length    hex string length
     */
    public static byte[] decodeHexDump(CharSequence hexDump, int fromIndex, int length) {
        if (length < 0 || (length & 1) != 0) {
            throw new IllegalArgumentException("length: " + length);
        }
        if (length == 0) {
            return new byte[]{};
        }
        byte[] bytes = new byte[length >>> 1];
        for (int i = 0; i < length; i += 2) {
            bytes[i >>> 1] = decodeHexByte(hexDump, fromIndex + i);
        }
        return bytes;
    }

    /**
     * Decodes a <a href="http://en.wikipedia.org/wiki/Hex_dump">hex dump</a>
     */
    public static byte[] decodeHexDump(CharSequence hexDump) {
        return decodeHexDump(hexDump, 0, hexDump.length());
    }

    /**
     * Converts the specified byte array into a hexadecimal value.
     */
    public static String toHexString(byte[] src) {
        return toHexString(src, 0, src.length);
    }

    /**
     * Converts the specified byte array into a hexadecimal value.
     */
    public static String toHexString(byte[] src, int offset, int length) {
        return toHexString(new StringBuilder(length << 1), src, offset, length).toString();
    }

    /**
     * Converts the specified byte array into a hexadecimal value and appends it to the specified buffer.
     */
    public static <T extends Appendable> T toHexString(T dst, byte[] src) {
        return toHexString(dst, src, 0, src.length);
    }

    /**
     * Converts the specified byte array into a hexadecimal value and appends it to the specified buffer.
     */
    public static <T extends Appendable> T toHexString(T dst, byte[] src, int offset, int length) {
        assert length >= 0;
        if (length == 0) {
            return dst;
        }

        final int end = offset + length;
        final int endMinusOne = end - 1;
        int i;

        // Skip preceding zeroes.
        for (i = offset; i < endMinusOne; i++) {
            if (src[i] != 0) {
                break;
            }
        }

        byteToHexString(dst, src[i++]);
        int remaining = end - i;
        toHexStringPadded(dst, src, i, remaining);

        return dst;
    }

    /**
     * Converts the specified byte value into a hexadecimal integer.
     */
    public static String byteToHexString(int value) {
        return BYTE2HEX_NOPAD[value & 0xff];
    }

    /**
     * Converts the specified byte value into a 2-digit hexadecimal integer.
     */
    public static String byteToHexStringPadded(int value) {
        return BYTE2HEX_PAD[value & 0xff];
    }

    /**
     * Converts the specified byte value into a hexadecimal integer and appends it to the specified buffer.
     */
    public static <T extends Appendable> T byteToHexString(T buf, int value) {
        try {
            buf.append(byteToHexString(value));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buf;
    }

    /**
     * Converts the specified byte array into a hexadecimal value and appends it to the specified buffer.
     */
    public static <T extends Appendable> T toHexStringPadded(T dst, byte[] src, int offset, int length) {
        final int end = offset + length;
        for (int i = offset; i < end; i++) {
            byteToHexStringPadded(dst, src[i]);
        }
        return dst;
    }

    /**
     * Converts the specified byte value into a 2-digit hexadecimal integer and appends it to the specified buffer.
     */
    public static <T extends Appendable> T byteToHexStringPadded(T buf, int value) {
        try {
            buf.append(byteToHexStringPadded(value));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buf;
    }

    static {
        for (int i = 0; i < BYTE2HEX_PAD.length; ++i) {
            String str = Integer.toHexString(i);
            BYTE2HEX_PAD[i] = i > 15 ? str : '0' + str;
            BYTE2HEX_NOPAD[i] = str;
        }

    }
}
