package com.zt.lib.exceptions;

/**
 * 非UI线程试图刷新数据时抛出
 */
public class IllegalThreadNotifyException extends Exception {

	private static final long serialVersionUID = -9212237638331580514L;
    /**
     * Constructs a new {@code NullArgException} that includes the
     * current stack trace.
     */
    public IllegalThreadNotifyException() {
    }

    /**
     * Constructs a new {@code NullArgException} with the current stack
     * trace and the specified detail message.
     *
     * @param detailMessage
     *            the detail message for this exception.
     */
    public IllegalThreadNotifyException(String detailMessage) {
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
    public IllegalThreadNotifyException(String message, Throwable cause) {
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
    public IllegalThreadNotifyException(Throwable cause) {
        super((cause == null ? null : cause.toString()), cause);
    }
    
}
