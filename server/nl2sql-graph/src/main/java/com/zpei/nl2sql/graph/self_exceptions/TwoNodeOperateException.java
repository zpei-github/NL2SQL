package com.zpei.nl2sql.graph.self_exceptions;


public class TwoNodeOperateException extends RuntimeException{

    public TwoNodeOperateException() {
        super();
    }
    public TwoNodeOperateException(String message) {
        super(message);
    }

    // 0: no backtrace filled in, no message computed.
    // 1: backtrace filled in, no message computed.
    // 2: message computed
    private transient int extendedMessageState;
    private transient String extendedMessage;

    /**
     * {@inheritDoc}
     */
    public synchronized Throwable fillInStackTrace() {
        // If the stack trace is changed the extended NPE algorithm
        // will compute a wrong message. So compute it beforehand.
        if (extendedMessageState == 0) {
            extendedMessageState = 1;
        } else if (extendedMessageState == 1) {
            extendedMessage = getExtendedNPEMessage();
            extendedMessageState = 2;
        }
        return super.fillInStackTrace();
    }

    /**
     * Returns the detail message string of this throwable.
     *
     * <p> If a non-null message was supplied in a constructor it is
     * returned. Otherwise, an implementation specific message or
     * {@code null} is returned.
     *
     * @implNote
     * If no explicit message was passed to the constructor, and as
     * long as certain internal information is available, a verbose
     * description of the null reference is returned.
     * The internal information is not available in deserialized
     * NullPointerExceptions.
     *
     * @return the detail message string, which may be {@code null}.
     */
    public String getMessage() {
        String message = super.getMessage();
        if (message == null) {
            synchronized(this) {
                if (extendedMessageState == 1) {
                    // Only the original stack trace was filled in. Message will
                    // compute correctly.
                    extendedMessage = getExtendedNPEMessage();
                    extendedMessageState = 2;
                }
                return extendedMessage;
            }
        }
        return message;
    }

    /**
     * Get an extended exception message. This returns a string describing
     * the location and cause of the exception. It returns null for
     * exceptions where this is not applicable.
     */
    private native String getExtendedNPEMessage();
}
