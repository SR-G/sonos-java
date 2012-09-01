package org.tensin.sonos;

/**
 * The Class SonosConstants.
 */
public final class SonosConstants {

    /** The Constant DEFAULT_MAX_TIMEOUT_SONOS_COMMANDER_WHEN_WORKING_ON_ALL_ZONES. */
    public static final int DEFAULT_MAX_TIMEOUT_SONOS_COMMANDER_WHEN_WORKING_ON_ALL_ZONES = 20000;

    /** The Constant DEFAULT_MAX_TIMEOUT_WHEN_WORKING_ON_ALL_ZONES. */
    public static final int DEFAULT_MAX_TIMEOUT_JAVA_COMMANDER_WHEN_WORKING_ON_ALL_ZONES = 3000;

    /** The Constant MAX_DISCOVER_TIME_IN_MILLISECONDS. */
    public static final int MAX_DISCOVER_TIME_IN_MILLISECONDS = 2500;

    /** The Constant SSDP_CONTROL_PORT. May be changed if needed. */
    public static final int DEFAULT_SSDP_CONTROL_PORT = 8009;

    /** The Constant SONOS_DEFAULT_RPC_PORT. */
    public static final int SONOS_DEFAULT_RPC_PORT = 1400;

    /** The Constant NEWLINE. */
    public static final String NEWLINE = "\n";

    /** The Constant AV_TRANSPORT. */
    public static final String AV_TRANSPORT = "AVTransport";

    /**
     * Instantiates a new sonos constants.
     */
    private SonosConstants() {

    }

}
