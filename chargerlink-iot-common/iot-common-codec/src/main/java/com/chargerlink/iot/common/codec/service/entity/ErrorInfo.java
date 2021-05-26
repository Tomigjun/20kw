package com.chargerlink.iot.common.codec.service.entity;

import lombok.Data;

/**
 * 错误信息
 *
 * @author GISirFive
 * @date Create on 2020/4/20 14:42
 */
@Data
public class ErrorInfo {

    /**
     * 错误码
     */
    private Integer code;
    /**
     * 错误描述
     */
    private String message;
    /**
     * 错误发生的节点名称
     */
    private String nodeName;

    public ErrorInfo(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public ErrorInfo(Integer code, String message, String nodeName) {
        this.code = code;
        this.message = message;
        this.nodeName = nodeName;
    }
}
