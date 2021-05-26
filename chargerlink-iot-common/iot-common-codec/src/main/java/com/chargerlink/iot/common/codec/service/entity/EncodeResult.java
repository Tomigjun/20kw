package com.chargerlink.iot.common.codec.service.entity;

import com.chargerlink.common.exception.IErrorType;
import com.chargerlink.iot.common.codec.exception.CodecException;
import com.chargerlink.iot.common.codec.exception.DecodeErrorType;
import lombok.Data;

/**
 * 编码结果
 *
 * @author GISirFive
 * @date Create on 2020/4/17 11:11
 */
@Data
public class EncodeResult {

    /**
     * 编解码成功/失败
     */
    private Boolean success;
    /**
     * 错误信息，当success为false时，对应{@link DecodeErrorType}中的getErrorCode()和getErrorMessage()
     */
    private ErrorInfo error;
    /**
     * 编码数据
     */
    private byte[] data;

    /**
     * 成功
     *
     * @param data
     * @return
     */
    public static EncodeResult success(byte[] data) {
        EncodeResult result = new EncodeResult();
        result.success = true;
        result.data = data;
        return result;
    }

    /**
     * 失败
     *
     * @param error
     * @return
     */
    public static EncodeResult failure(IErrorType error) {
        EncodeResult result = new EncodeResult();
        result.success = false;
        result.error = new ErrorInfo(error.getErrorCode(), error.getErrorMessage());
        return result;
    }

    /**
     * 失败
     *
     * @param error
     * @param errorMessage
     * @return
     */
    public static EncodeResult failure(IErrorType error, String errorMessage) {
        EncodeResult result = new EncodeResult();
        result.success = false;
        result.error = new ErrorInfo(error.getErrorCode(), errorMessage);
        return result;
    }

    /**
     * 失败
     *
     * @param exception
     * @return
     */
    public static EncodeResult failure(CodecException exception) {
        EncodeResult result = new EncodeResult();
        result.success = false;
        result.error = new ErrorInfo(exception.getErrorCode(), exception.getMessage());
        if (exception.getErrorNode() != null) {
            result.error.setNodeName(exception.getErrorNode().getNodeName());
        }
        return result;
    }


}
