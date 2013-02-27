package org.tensin.sonos;

import org.slf4j.LoggerFactory;
import org.tensin.sonos.helpers.SLF4JHelper;

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
            SLF4JHelper.configureJULLogging();
            final Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
            root.setLevel(Level.INFO);
            initLog = true;
        }
    }
}
