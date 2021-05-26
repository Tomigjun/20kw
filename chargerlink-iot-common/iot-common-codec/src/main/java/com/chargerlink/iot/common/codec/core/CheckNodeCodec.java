package com.chargerlink.iot.common.codec.core;

import com.chargerlink.iot.common.codec.exception.DecodeErrorType;
import com.chargerlink.iot.common.codec.exception.EncodeErrorType;
import com.chargerlink.iot.common.codec.exception.CodecException;
import com.chargerlink.iot.common.codec.node.CheckNode;
import scodec.bits.BitVector;

import java.util.Objects;

/**
 * 校验位节点解析类
 *
 * @author GISirFive
 * @date Created on 10:46 2019/7/15.
 */
class CheckNodeCodec {


    /**
     * Table of crc values for high-order byte
     */
    private final static short[] LOOKUP_CRC_HI = {0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40, 0x01, 0xC0, 0x80, 0x41, 0x01, 0xC0, 0x80, 0x41, 0x00, 0xC1, 0x81, 0x40};

    /**
     * Table of crc values for low-order byte
     */
    private final static short[] LOOKUP_CRC_LO = {0x00, 0xC0, 0xC1, 0x01, 0xC3, 0x03, 0x02, 0xC2, 0xC6, 0x06, 0x07, 0xC7, 0x05, 0xC5, 0xC4, 0x04, 0xCC, 0x0C, 0x0D, 0xCD, 0x0F, 0xCF, 0xCE, 0x0E, 0x0A, 0xCA, 0xCB, 0x0B, 0xC9, 0x09, 0x08, 0xC8, 0xD8, 0x18, 0x19, 0xD9, 0x1B, 0xDB, 0xDA, 0x1A, 0x1E, 0xDE, 0xDF, 0x1F, 0xDD, 0x1D, 0x1C, 0xDC, 0x14, 0xD4, 0xD5, 0x15, 0xD7, 0x17, 0x16, 0xD6, 0xD2, 0x12, 0x13, 0xD3, 0x11, 0xD1, 0xD0, 0x10, 0xF0, 0x30, 0x31, 0xF1, 0x33, 0xF3, 0xF2, 0x32, 0x36, 0xF6, 0xF7, 0x37, 0xF5, 0x35, 0x34, 0xF4, 0x3C, 0xFC, 0xFD, 0x3D, 0xFF, 0x3F, 0x3E, 0xFE, 0xFA, 0x3A, 0x3B, 0xFB, 0x39, 0xF9, 0xF8, 0x38, 0x28, 0xE8, 0xE9, 0x29, 0xEB, 0x2B, 0x2A, 0xEA, 0xEE, 0x2E, 0x2F, 0xEF, 0x2D, 0xED, 0xEC, 0x2C, 0xE4, 0x24, 0x25, 0xE5, 0x27, 0xE7, 0xE6, 0x26, 0x22, 0xE2, 0xE3, 0x23, 0xE1, 0x21, 0x20, 0xE0, 0xA0, 0x60, 0x61, 0xA1, 0x63, 0xA3, 0xA2, 0x62, 0x66, 0xA6, 0xA7, 0x67, 0xA5, 0x65, 0x64, 0xA4, 0x6C, 0xAC, 0xAD, 0x6D, 0xAF, 0x6F, 0x6E, 0xAE, 0xAA, 0x6A, 0x6B, 0xAB, 0x69, 0xA9, 0xA8, 0x68, 0x78, 0xB8, 0xB9, 0x79, 0xBB, 0x7B, 0x7A, 0xBA, 0xBE, 0x7E, 0x7F, 0xBF, 0x7D, 0xBD, 0xBC, 0x7C, 0xB4, 0x74, 0x75, 0xB5, 0x77, 0xB7, 0xB6, 0x76, 0x72, 0xB2, 0xB3, 0x73, 0xB1, 0x71, 0x70, 0xB0, 0x50, 0x90, 0x91, 0x51, 0x93, 0x53, 0x52, 0x92, 0x96, 0x56, 0x57, 0x97, 0x55, 0x95, 0x94, 0x54, 0x9C, 0x5C, 0x5D, 0x9D, 0x5F, 0x9F, 0x9E, 0x5E, 0x5A, 0x9A, 0x9B, 0x5B, 0x99, 0x59, 0x58, 0x98, 0x88, 0x48, 0x49, 0x89, 0x4B, 0x8B, 0x8A, 0x4A, 0x4E, 0x8E, 0x8F, 0x4F, 0x8D, 0x4D, 0x4C, 0x8C, 0x44, 0x84, 0x85, 0x45, 0x87, 0x47, 0x46, 0x86, 0x82, 0x42, 0x43, 0x83, 0x41, 0x81, 0x80, 0x40};

    /**
     * 检查校验位
     *
     * @param targetNode
     * @param sourceData
     * @param cursorIndex 当前游标位置
     * @return
     */
    public static boolean decode(CheckNode targetNode, BitVector sourceData, long cursorIndex) {
        //NOTE 先计算校验位的解析值
        long startIndex = cursorIndex;
        if (targetNode.getStartIndex() != null) {
            startIndex = targetNode.getStartIndex() * 8;
        }
        //从报文尾开始索引
        if (startIndex < 0) {
            startIndex = sourceData.length() + startIndex;
        }
        BitVector targetData = sourceData.slice(startIndex, startIndex + Math.abs(targetNode.getLengthOfBit()));
        byte[] checkBytes = targetData.toByteArray();

        //NOTE 再计算校验值
        //先转为Bit长度
        long checkStartIndex = targetNode.getCheckStartIndex() * 8;
        long checkEndIndex = targetNode.getCheckEndIndex() * 8;
        if (checkStartIndex < 0) {
            checkStartIndex = checkStartIndex + sourceData.length();
        }
        if (checkEndIndex < 0) {
            checkEndIndex = checkEndIndex + sourceData.length() - 8;
        }
        if (checkStartIndex >= sourceData.length()) {
            //校验位计算的起始位置越界
            throw new CodecException(DecodeErrorType.CHECK_START_INDEX_OUT_OF_INDEX);
        }
        if (checkEndIndex >= sourceData.length()) {
            //校验位计算的截止位置越界
            throw new CodecException(DecodeErrorType.CHECK_END_INDEX_OUT_OF_INDEX);
        }
        if (checkStartIndex >= checkEndIndex) {
            //校验位计算的起始位置超出截止位置
            throw new CodecException(DecodeErrorType.CHECK_START_INDEX_OUT_OF_CHECK_END_INDEX);
        }
        //再转为Byte长度
        checkStartIndex = checkStartIndex / 8;
        checkEndIndex = checkEndIndex / 8;

        byte[] calculateCheckByte;
        switch (targetNode.getCheckType()) {
            case BCC:
                calculateCheckByte = bcc(sourceData, checkStartIndex, checkEndIndex);
                break;
            case SUM:
                calculateCheckByte = sum(sourceData, checkStartIndex, checkEndIndex);
                break;
            case CRC:
                calculateCheckByte = crc(sourceData, checkStartIndex, checkEndIndex);
                break;
            default:
                throw new CodecException(DecodeErrorType.INVALID_CHECK_TYPE);
        }
        return Objects.deepEquals(checkBytes, calculateCheckByte);
    }

    /**
     * 计算校验位
     *
     * @param targetNode
     * @param completeData
     * @return
     */
    public static BitVector encode(CheckNode targetNode, BitVector completeData) {
        //先转为Bit长度
        long checkStartIndex = targetNode.getCheckStartIndex() * 8;
        long checkEndIndex = targetNode.getCheckEndIndex() * 8;
        if (checkStartIndex < 0) {
            checkStartIndex = completeData.length() + checkStartIndex;
        }
        //FIXME 此处没有考虑该场景：校验位不是最后一位，校验位后面还有内容
        if (checkEndIndex < 0) {
            //完整报文长度 - 截止位索引 - 8位索引
            checkEndIndex = (completeData.length() + targetNode.getLengthOfBit()) + checkEndIndex - 8;
        }
        if (checkStartIndex >= completeData.length()) {
            //校验位计算的起始位置越界
            throw new CodecException(EncodeErrorType.CHECK_START_INDEX_OUT_OF_INDEX);
        }
        if (checkEndIndex >= completeData.length()) {
            //校验位计算的截止位置越界
            throw new CodecException(EncodeErrorType.CHECK_END_INDEX_OUT_OF_INDEX);
        }
        if (checkStartIndex >= checkEndIndex) {
            //校验位计算的起始位置超出截止位置
            throw new CodecException(EncodeErrorType.CHECK_START_INDEX_OUT_OF_CHECK_END_INDEX);
        }
        //再转为Byte长度
        checkStartIndex = checkStartIndex / 8;
        checkEndIndex = checkEndIndex / 8;

        byte[] calculateCheckByte;
        switch (targetNode.getCheckType()) {
            case BCC:
                calculateCheckByte = bcc(completeData, checkStartIndex, checkEndIndex);
                break;
            case SUM:
                calculateCheckByte = sum(completeData, checkStartIndex, checkEndIndex);
                break;
            case CRC:
                calculateCheckByte = crc(completeData, checkStartIndex, checkEndIndex);
                break;
            default:
                throw new CodecException(EncodeErrorType.INVALID_CHECK_TYPE);
        }
        return BitVector.view(calculateCheckByte);
    }

    /**
     * 逐位异或算法
     *
     * @return
     */
    private static byte[] bcc(BitVector sourceData, long checkStartIndex, long checkEndIndex) {
        byte checkByte = 0;
        for (long i = checkStartIndex; i <= checkEndIndex; i++) {
            checkByte = (byte) (checkByte ^ sourceData.getByte(i));
        }
        return new byte[]{checkByte};
    }

    /**
     * 校验和算法
     *
     * @param sourceData
     * @param checkStartIndex
     * @param checkEndIndex
     * @return
     */
    private static byte[] sum(BitVector sourceData, long checkStartIndex, long checkEndIndex) {
        int checkByte = 0;
        for (long i = checkStartIndex; i <= checkEndIndex; i++) {
            checkByte += sourceData.getByte(i);
        }
        return new byte[]{(byte) checkByte};
    }

    /**
     * crc16校验
     *
     * @param sourceData
     * @param checkStartIndex
     * @param checkEndIndex
     * @return
     */
    private static byte[] crc(BitVector sourceData, long checkStartIndex, long checkEndIndex) {
        int high = 0xff;
        int low = 0xff;
        int nextByte;
        int uIndex;

        for (long i = checkStartIndex; i <= checkEndIndex; i++) {
            nextByte = 0xFF & sourceData.getByte(i);
            uIndex = high ^ nextByte;
            high = low ^ LOOKUP_CRC_HI[uIndex];
            low = LOOKUP_CRC_LO[uIndex];
        }

        byte[] resultByte = new byte[2];
        resultByte[0] = (byte) (0xff & high);
        resultByte[1] = (byte) (0xff & low);

        return resultByte;
    }

//    public static void main(String[] args) {
//        CheckNode node = new CheckNode();
//        node.setNodeName("校验位");
//        node.setNodeType(NodeTypeEnum.CHECK);
//        node.setAnalyticalUnit(AnalyticalUnitEnum.BYTE);
//        node.setLength(2);
//        node.setStartIndex(-2);
//        node.setCheckType(CheckTypeEnum.CRC);
//        node.setCheckEndIndex(0);
//        node.setCheckEndIndex(-2);
//
//        BitVector data = BitVector.view(ByteUtils.decodeHexDump("4B48002801960D5E0B35304131353130303034343148434330303030330002000100C1FBFF901B"));
//        BitVector checkData = encode(node, data);
//        System.out.println(ByteUtils.toHexString(checkData.toByteArray()));
//        data = SCodecUtils.spliceBitVector(data, checkData);
//        System.out.println(ByteUtils.toHexString(data.toByteArray()));
//        boolean success = decode(node, data, 0);
//        System.out.println(success);
//    }

}
