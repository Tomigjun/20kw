package com.chargerlink.iot.common.codec.service.entity;

import com.alibaba.fastjson.JSONObject;
import com.chargerlink.common.exception.IErrorType;
import com.chargerlink.iot.common.codec.exception.CodecException;
import com.chargerlink.iot.common.codec.exception.DecodeErrorType;
import lombok.Data;

/**
 * 解码结果
 *
 * @author GISirFive
 * @date Create on 2020/4/17 11:11
 */
@Data
public class DecodeResult {

    /**
     * 编解码成功/失败
     */
    private Boolean success;
    /**
     * 解码数据
     */
    private JSONObject data;
    /**
     * 设备ID
     */
    private String deviceId;
    /**
     * 错误信息，当success为false时，对应{@link DecodeErrorType}中的getErrorCode()和getErrorMessage()
     */
    private ErrorInfo error;

    /**
     * 成功
     *
     * @param data
     * @return
     */
    public static DecodeResult success(JSONObject data, String deviceId) {
        DecodeResult result = new DecodeResult();
        result.success = true;
        result.data = data;
        result.deviceId = deviceId;
        return result;
    }

    /**
     * 失败
     *
     * @param error
     * @return
     */
    public static DecodeResult failure(IErrorType error) {
        DecodeResult result = new DecodeResult();
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
    public static DecodeResult failure(IErrorType error, String errorMessage) {
        DecodeResult result = new DecodeResult();
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
    public static DecodeResult failure(CodecException exception) {
        DecodeResult result = new DecodeResult();
        result.success = false;
        result.error = new ErrorInfo(exception.getErrorCode(), exception.getMessage());
        if (exception.getErrorNode() != null) {
            result.error.setNodeName(exception.getErrorNode().getNodeName());
        }
        return result;
    }

}
