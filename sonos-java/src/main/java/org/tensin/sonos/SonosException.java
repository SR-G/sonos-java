package org.tensin.sonos;

/**
 * The Class SonosException.
 */
public class SonosException extends Exception {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -9055220250142132262L;

    /**
     * Instantiates a new sonos exception.
     * 
     * @param message
     *            the message
     */
    public SonosException(final String message) {
        super(message);
    }

    /**
     * Instantiates a new sonos exception.
     * 
     * @param message
     *            the message
     * @param e
     *            the e
     */
    public SonosException(final String message, final Throwable e) {
        super(message, e);
    }

    /**
     * Instantiates a new sonos exception.
     * 
     * @param e
     *            the e
     */
    public SonosException(final Throwable e) {
        super(e);
    }

}
