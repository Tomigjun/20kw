package com.chargerlink.iot.common.codec.utils

import scodec.bits.{BitVector, ByteOrdering, ByteVector}

/**
  * 编解码工具类
  * 由于JAVA中不能使用scala的枚举类型，在此方法中对scala方法进行封装。
  *
  * @author ZhangHeng zhanghenggoog@gmail.com
  * @since Created on 15:44 2019/6/13.
  */
case class SCodecUtils() {

}

object SCodecUtils {

  /**
    * byte -> int with大端
    *
    * @param byteVector
    * @param signed
    * @return
    */
  def byteToIntBE(byteVector: ByteVector, signed: Boolean = true): Int = {
    byteVector.toInt(signed, ByteOrdering.BigEndian)
  }

  /**
    * byte -> int with小端
    *
    * @param byteVector
    * @param signed
    * @return
    */
  def byteToIntLE(byteVector: ByteVector, signed: Boolean = true): Int = {
    byteVector.toInt(signed, ByteOrdering.LittleEndian)
  }

  /**
    * byte -> long with大端
    *
    * @param byteVector
    * @param signed
    * @return
    */
  def byteToLongBE(byteVector: ByteVector, signed: Boolean = true): Long = {
    byteVector.toLong(signed, ByteOrdering.BigEndian)
  }

  /**
    * byte -> long with小端
    *
    * @param byteVector
    * @param signed
    * @return
    */
  def byteToLongLE(byteVector: ByteVector, signed: Boolean = true): Long = {
    byteVector.toLong(signed, ByteOrdering.LittleEndian)
  }

  /**
    * bit -> int with大端
    *
    * @param bitVector
    * @param signed
    * @return
    */
  def bitToIntBE(bitVector: BitVector, signed: Boolean = true): Int = {
    bitVector.toInt(signed, ByteOrdering.BigEndian)
  }

  /**
    * bit -> int with小端
    *
    * @param bitVector
    * @param signed
    * @return
    */
  def bitToIntLE(bitVector: BitVector, signed: Boolean = true): Int = {
    bitVector.toInt(signed, ByteOrdering.LittleEndian)
  }

  /**
    * bit -> long with大端
    *
    * @param bitVector
    * @param signed
    * @return
    */
  def bitToLongBE(bitVector: BitVector, signed: Boolean = true): Long = {
    bitVector.toLong(signed, ByteOrdering.BigEndian)
  }

  /**
    * bit -> long with小端
    *
    * @param bitVector
    * @param signed
    * @return
    */
  def bitToLongLE(bitVector: BitVector, signed: Boolean = true): Long = {
    bitVector.toLong(signed, ByteOrdering.LittleEndian)
  }

  /**
    * int -> byte with大端
    *
    * @param value
    * @param size
    * @return
    */
  def intToByteBE(value: Int, size: Int): ByteVector = {
    ByteVector.fromInt(value, size, ByteOrdering.BigEndian)
  }

  /**
    * int -> byte with小端
    *
    * @param value
    * @param size
    * @return
    */
  def intToByteLE(value: Int, size: Int): ByteVector = {
    ByteVector.fromInt(value, size, ByteOrdering.LittleEndian)
  }

  /**
    * long -> byte with大端
    *
    * @param value
    * @param size
    * @return
    */
  def longToByteBE(value: Long, size: Int): ByteVector = {
    ByteVector.fromLong(value, size, ByteOrdering.BigEndian)
  }

  /**
    * long -> byte with小端
    *
    * @param value
    * @param size
    * @return
    */
  def longToByteLE(value: Long, size: Int): ByteVector = {
    ByteVector.fromLong(value, size, ByteOrdering.LittleEndian)
  }

  /**
    * int -> bit with大端
    *
    * @param value
    * @param size
    * @return
    */
  def intToBitBE(value: Int, size: Int): BitVector = {
    BitVector.fromInt(value, size, ByteOrdering.BigEndian)
  }

  /**
    * int -> bit with小端
    *
    * @param value
    * @param size
    * @return
    */
  def intToBitLE(value: Int, size: Int): BitVector = {
    BitVector.fromInt(value, size, ByteOrdering.LittleEndian)
  }

  /**
    * long -> bit with大端
    *
    * @param value
    * @param size
    * @return
    */
  def longToBitBE(value: Long, size: Int): BitVector = {
    BitVector.fromLong(value, size, ByteOrdering.BigEndian)
  }

  /**
    * long -> bit with小端
    *
    * @param value
    * @param size
    * @return
    */
  def longToBitLE(value: Long, size: Int): BitVector = {
    BitVector.fromLong(value, size, ByteOrdering.LittleEndian)
  }

  /**
    * 拼接两个BitVector
    *
    * @param first
    * @param second
    * @return
    */
  def spliceBitVector(first: BitVector, second: BitVector): BitVector = {
    first.++(second)
  }

}