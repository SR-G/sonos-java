package org.tensin.sonos;

/**
 * The Class MirrorException.
 */
public class SonosRuntimeException extends RuntimeException {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -5425682063963568465L;

    /**
     * Instantiates a new mirror exception.
     */
    public SonosRuntimeException() {
        super();
    }

    /**
     * Instantiates a new mirror exception.
     *
     * @param message
     *            the message
     */
    public SonosRuntimeException(final String message) {
        super(message);
    }

    /**
     * Instantiates a new mirror exception.
     *
     * @param message
     *            the message
     * @param cause
     *            the cause
     */
    public SonosRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new mirror exception.
     *
     * @param cause
     *            the cause
     */
    public SonosRuntimeException(final Throwable cause) {
        super(cause);
    }
}