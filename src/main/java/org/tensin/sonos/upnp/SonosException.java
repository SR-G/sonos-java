package org.tensin.sonos.upnp;

/**
 * The Class SonosException.
 */
public class SonosException extends Exception {

	public SonosException(final String message) {
		super(message);
	}

	public SonosException(final Throwable e) {
		super(e);
	}

	public SonosException(final String message, final Throwable e) {
		super(message, e);
	}

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -9055220250142132262L;

}
