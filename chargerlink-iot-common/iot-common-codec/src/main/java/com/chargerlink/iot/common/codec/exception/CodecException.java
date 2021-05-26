package com.chargerlink.iot.common.codec.exception;

import com.chargerlink.common.exception.IErrorType;
import com.chargerlink.iot.common.codec.node.base.Node;

/**
 * Codec自定义异常
 *
 * @author GISirFive
 * @date Create on 2020/4/17 11:55
 */
public class CodecException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 异常类型
     */
    private Integer errorCode;
    /**
     * 发送错误的节点
     */
    private Node errorNode;

    public CodecException(IErrorType errorType) {
        this(errorType, null, null);
    }

    public CodecException(IErrorType errorType, String message) {
        this(errorType, message, null);
    }

    public CodecException(IErrorType errorType, Node errorNode) {
        this(errorType, null, errorNode);
    }

    public CodecException(IErrorType errorType, String message, Node errorNode) {
        super(String.format("[%s]%s", errorType.getErrorCode(), message == null ? errorType.getErrorMessage() : message));
        this.errorCode = errorType.getErrorCode();
        this.errorNode = errorNode;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public Node getErrorNode() {
        return errorNode;
    }

    public void setErrorNode(Node errorNode) {
        this.errorNode = errorNode;
    }
}
