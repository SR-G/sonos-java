package org.tensin.sonos;

import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

/**
 * The Class LogInitializer.
 */
public class LogInitializer {

    /** The init log. */
    private static boolean initLog;

    /**
     * Inits the log.
     */
    public static void initLog() {
        if (!initLog) {
            SLF4JBridgeHandler.removeHandlersForRootLogger();
            SLF4JBridgeHandler.install();
            // BasicConfigurator.configureDefaultContext();

            final Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
            root.setLevel(Level.INFO);
            initLog = true;
        }
    }

}
