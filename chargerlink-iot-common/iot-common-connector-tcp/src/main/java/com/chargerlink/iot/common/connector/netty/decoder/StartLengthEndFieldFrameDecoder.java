package com.chargerlink.iot.common.connector.netty.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteOrder;
import java.util.List;

/**
 * @author GISirFive
 * @date Created on 16:45 2019/11/5.
 */
@Slf4j
public class StartLengthEndFieldFrameDecoder extends StartLengthFieldFrameDecoder {

    /**
     * 起始标记位的长度
     */
    private final int endFieldLength;

    /**
     * 起始标记位的数组形式
     */
    private final ByteBuf endFieldByteBuf;

    public StartLengthEndFieldFrameDecoder(ByteOrder byteOrder, int maxFrameLength, int baseFrameLength, int lengthFieldOffset, int lengthFieldLength, ByteBuf startFieldByte, ByteBuf endFieldByte) {
        super(byteOrder, maxFrameLength, baseFrameLength, lengthFieldOffset, lengthFieldLength, startFieldByte);
        this.endFieldLength = endFieldByte.readableBytes();
        this.endFieldByteBuf = endFieldByte;
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

        //NOTE-5 检查报文结束位
        if (!checkEndFrame(length, in)) {
            byte headByte = in.readByte();
            log.info("报文起始位长度位与结束位不符合[headByte = {}]", ByteBufUtil.hexDump(new byte[]{headByte}));
            return;
        }

        //如果报文的长度位小于实际的报文长度，认为报文拆包，不做处理
        if (in.readableBytes() >= length) {
            ByteBuf byteBuf = in.readSlice((int) length);
            log.debug("decode 处理报文结束[outMessage = {},refCnt = {}]", ByteBufUtil.hexDump(byteBuf), byteBuf.refCnt());
            //处理ByteBuf的引用计数, 防止refCnt == 0
            out.add(byteBuf.retainedDuplicate());
        }
    }

    /**
     * 检查报文结束位
     *
     * @param in
     */
    private boolean checkEndFrame(long length, ByteBuf in) {
        Integer lengthInt = Math.toIntExact(length);
        //判断报文是否与结尾一致
        return ByteBufUtil.equals(endFieldByteBuf, 0, in, in.readerIndex() + lengthInt - endFieldLength, endFieldLength);
    }
}
