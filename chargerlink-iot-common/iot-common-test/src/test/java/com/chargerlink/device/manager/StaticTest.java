package com.chargerlink.device.manager;

import com.chargerlink.common.exception.ChargerlinkException;
import com.chargerlink.iot.common.codec.constant.ByteReadOrderEnum;
import com.chargerlink.iot.common.codec.exception.DecodeErrorType;
import com.chargerlink.iot.common.codec.exception.CodecException;
import com.chargerlink.iot.common.codec.utils.SCodecUtils;
import com.sun.org.apache.xpath.internal.operations.String;
import lombok.extern.slf4j.Slf4j;
import scodec.bits.BitVector;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author GISirFive
 * @date Create on 2020/4/3 14:40
 */
@Slf4j
public class StaticTest {

    interface Runnable {
        void run();
    }

    static void test(Runnable r) {

    }

    public static <T> void forEach(List<T> list, Consumer<T> c) {
        for (T t : list) {
            c.accept(t);
        }
    }

    public static void main(String[] args) throws InterruptedException, UnsupportedEncodingException {
//        try {
//            throw new CodecException(DecodeErrorType.INVALID_PROTOCOL_DATA_TYPE);
////            throw new ChargerlinkException(DecodeErrorType.INVALID_PROTOCOL_DATA_TYPE);
//        } catch (CodecException e) {
//            log.error("报错了", e);
//        } catch (ChargerlinkException e){
//            log.error("报错了", e);
//        }
//        DecodeResult result = DecodeResult.failure(DecodeErrorType.INVALID_PARAM, "123123");
//        System.out.println(JSON.toJSONString(result));
//        DecodeResult r1 = DecodeResult.success(new JSONObject().fluentPut("a", 100), null);
//        System.out.println(JSON.toJSONString(r1));
//        EncodeResult r2 = EncodeResult.success(ByteBufUtil.decodeHexDump("AA55"));
//        System.out.println(JSON.toJSONString(r2));
//        byte[] b1 = ByteBufUtil.decodeHexDump("AA55");
//        byte[] b2 = ByteUtils.decodeHexDump("AA55");
//        System.out.println(ByteBufUtil.hexDump(b1));
//        System.out.println(ByteBufUtil.hexDump(b2));
//        System.out.println(ByteBufUtil.hexDump("一".getBytes("UTF-8")));
        //        System.out.println(new String(ByteBufUtil.decodeHexDump("44454647"), Charset.forName("ASCII")));
//        System.out.println(new String(ByteBufUtil.decodeHexDump("E4B880"), Charset.forName("UTF-8")));
//        System.out.println(new String(ByteBufUtil.decodeHexDump("4E00"), Charset.forName("Unicode")));
//        System.out.println(new String(ByteBufUtil.decodeHexDump("D2BB"), Charset.forName("GBK")));
//        test(() -> System.out.println("asdas"));
//        forEach(Arrays.asList(1, 2, 3, 4, 5), (Integer i) -> System.out.println(i));

        //        byte[] b = "test".getBytes("UTF-8");
//        byte[] b2 = ByteUtils.spliceByteArray(b, new byte[5 - b.length]);
//        System.out.println(ByteBufUtil.hexDump(b));
//        System.out.println(ByteBufUtil.hexDump(b2));
//        BitVector v = BitVector.view(b);
//        BitVector v2 = BitVector.view(new byte[1]);
//        BitVector v3 = SCodecUtils.spliceBitVector(v, v2);
//        BitVector v4 = SCodecUtils.spliceBitVector(v2, v);
//        System.out.println(ByteBufUtil.hexDump(v.toByteArray()));
//        System.out.println(ByteBufUtil.hexDump(v2.toByteArray()));
//        System.out.println(ByteBufUtil.hexDump(v3.toByteArray()));
//        System.out.println(ByteBufUtil.hexDump(v4.toByteArray()));
//        System.out.println(ByteBufUtil.hexDump(v4.reverse().toByteArray()));

//        System.out.println(ByteBufUtil.hexDump(encodeNumericValue(Integer.valueOf("255255255"), 32, ByteReadOrderEnum.BIG_END).toByteArray()));
//        System.out.println(ByteBufUtil.hexDump(encodeNumericValue(Long.valueOf("255255255"), 32, ByteReadOrderEnum.BIG_END).toByteArray()));
        //        BitCursor cursor = new BitCursor(0);
//        Integer test = 0;
//        for (int i = 0; i < 100; i++) {
//            Thread.sleep(500L);
//            move(test, true);
//            System.out.println("--->" + test);
//        }
    }

    private static BitVector encodeNumericValue(Object value, int lengthOfBit, ByteReadOrderEnum readOrder) {
        if (value instanceof Integer) {
            switch (readOrder) {
                case LITTLE_END:
                    return SCodecUtils.intToBitLE((Integer) value, lengthOfBit);
                case BIG_END:
                    return SCodecUtils.intToBitBE((Integer) value, lengthOfBit);
                default:
                    return null;
            }
        }
        if (value instanceof Long) {
            switch (readOrder) {
                case LITTLE_END:
                    return SCodecUtils.longToBitLE((Long) value, lengthOfBit);
                case BIG_END:
                    return SCodecUtils.longToBitBE((Long) value, lengthOfBit);
                default:
                    return null;
            }
        }
        return null;
    }

}
