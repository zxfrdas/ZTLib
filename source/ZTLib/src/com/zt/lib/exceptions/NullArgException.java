package com.zt.lib.exceptions;

/**
 * 当一个方法的传入参数为空值，或传入的集合为空时抛出此错误。
 * @author zhaotong
 */
public class NullArgException extends Exception {

	private static final long serialVersionUID = 4896891681448626835L;
    /**
     * Constructs a new {@code NullArgException} that includes the
     * current stack trace.
     */
    public NullArgException() {
    }

    /**
     * Constructs a new {@code NullArgException} with the current stack
     * trace and the specified detail message.
     *
     * @param detailMessage
     *            the detail message for this exception.
     */
    public NullArgException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * Constructs a new {@code NullArgException} with the current stack
     * trace, the specified detail message and the specified cause.
     *
     * @param message
     *            the detail message for this exception.
     * @param cause
     *            the cause of this exception, may be {@code null}.
     * @since 1.5
     */
    public NullArgException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new {@code NullArgException} with the current stack
     * trace and the specified cause.
     *
     * @param cause
     *            the cause of this exception, may be {@code null}.
     * @since 1.5
     */
    public NullArgException(Throwable cause) {
        super((cause == null ? null : cause.toString()), cause);
    }
    
}
