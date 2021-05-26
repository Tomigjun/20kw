package com.chargerlink.iot.common.connector.netty.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteOrder;
import java.util.List;

/**
 * 根据指定报文头和长度位解决报文粘包和拆包
 *
 * @author GISirFive
 * @date Created on 17:30 2019/5/28.
 */
@Slf4j
public class StartLengthFieldFrameDecoder extends ByteToMessageDecoder {

    /**
     * 默认的最大报文长度
     */
    protected static final int DEFAULT_MAX_FRAME_LENGTH = 10240;

    /**
     * 读取方式-大端小端-默认大端
     */
    final ByteOrder byteOrder;
    /**
     * 处理的最大报文长度
     */
    private final int maxFrameLength;
    /**
     * 基础报文的长度-当长度位的值仅包含数据域时，需要指定基础报文的长度来获取完成的报文信息
     */
    final int baseFrameLength;
    /**
     * 长度位起始位置
     */
    final int lengthFieldOffset;
    /**
     * 长度位的长度
     */
    final int lengthFieldLength;
    /**
     * 起始标记位的长度
     */
    final int startFieldLength;
    /**
     * 起始标记位的数组形式
     */
    final ByteBuf startFieldByteBuf;

    public StartLengthFieldFrameDecoder(ByteOrder byteOrder, int maxFrameLength, int baseFrameLength, int lengthFieldOffset, int lengthFieldLength, ByteBuf startFieldByte) {
        this.byteOrder = byteOrder;
        this.maxFrameLength = maxFrameLength;
        this.baseFrameLength = baseFrameLength;
        this.lengthFieldOffset = lengthFieldOffset;
        this.lengthFieldLength = lengthFieldLength;
        this.startFieldLength = startFieldByte.readableBytes();
        this.startFieldByteBuf = startFieldByte;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        log.debug("decode 开始处理报文[message = {},refCnt = {}]", ByteBufUtil.hexDump(in), in.refCnt());

        //NOTE-1 对比报文头,如果不符合则直接舍弃
        if (!ByteBufUtil.equals(startFieldByteBuf, 0, in, in.readerIndex(), startFieldLength)) {
            byte headByte = in.readByte();
            log.info("丢弃意外报文头[headByte = {}]", ByteBufUtil.hexDump(new byte[]{headByte}));
            return;
        }

        //NOTE-2 判断报文的长度是否可以处理
        if (!determineLength(in)) {
            return;
        }

        //NOTE-3 处理报文超长的情况,将超出分舍弃掉
        discardingTooLongFrame(in);

        //NOTE-4 获取报文中的长度位
        long length = getUnadjustedFrameLength(in, in.readerIndex() + lengthFieldOffset, lengthFieldLength, byteOrder) + baseFrameLength;
        //如果报文的长度位小于实际的报文长度，认为报文拆包，不做处理
        if (in.readableBytes() >= length) {
            ByteBuf byteBuf = in.readSlice((int) length);
            log.debug("decode 处理报文结束[outMessage = {},refCnt = {}]", ByteBufUtil.hexDump(byteBuf), byteBuf.refCnt());
            //处理ByteBuf的引用计数, 防止refCnt == 0
            out.add(byteBuf.retainedDuplicate());
        }
    }

    /**
     * 判断报文长度是否合规
     *
     * @param in
     * @return
     */
    boolean determineLength(ByteBuf in) {
        int readableBytes = in.readableBytes();
        if (readableBytes <= baseFrameLength) {
            //当报文长度小于活等于基础报文的长度时，不做处理
            return false;
        }
        if (readableBytes < startFieldLength) {
            //当报文长度小于报文起始标记位的长度，不做处理
            return false;
        }
        if (readableBytes < lengthFieldOffset + lengthFieldLength) {
            //当报文的长度小于报文长度标记位长度，不做处理
            return false;
        }
        return true;
    }

    /**
     * 判断报文是否超出指定的长度，是则抛弃过长部分
     *
     * @param in
     */
    void discardingTooLongFrame(ByteBuf in) {
        if (in.readableBytes() > maxFrameLength) {
            in.skipBytes(in.readableBytes() - maxFrameLength);
        }
    }

    /**
     * 根据起始位和偏移量读取长度位
     *
     * @param buf
     * @param offset
     * @param length
     * @param order
     * @return
     */
    long getUnadjustedFrameLength(ByteBuf buf, int offset, int length, ByteOrder order) {
        buf = buf.order(order);
        long frameLength;
        switch (length) {
            case 1:
                frameLength = buf.getUnsignedByte(offset);
                break;
            case 2:
                frameLength = buf.getUnsignedShort(offset);
                break;
            case 3:
                frameLength = buf.getUnsignedMedium(offset);
                break;
            case 4:
                frameLength = buf.getUnsignedInt(offset);
                break;
            case 8:
                frameLength = buf.getLong(offset);
                break;
            default:
                throw new DecoderException(
                        "unsupported lengthFieldLength: " + lengthFieldLength + " (expected: 1, 2, 3, 4, or 8)");
        }
        return frameLength;
    }
}
