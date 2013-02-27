package org.tensin.sonos.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.jul.LevelChangePropagator;

/**
 * The Class SLF4JHelper.
 */
public final class SLF4JHelper {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SLF4JHelper.class);

    /**
     * Méthode setLoggerLevel.
     * 
     * @param module
     *            the module
     * @param level
     *            the level
     */
    public static void changeLoggerLevel(final String module, final Level level) {
        final String moduleRenamed = module.replaceAll("/", ".");
        if (LOGGER instanceof ch.qos.logback.classic.Logger) {
            final ch.qos.logback.classic.Logger logbackLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(moduleRenamed);
            if (logbackLogger != null) {
                logbackLogger.setLevel(level);
            }
        }
    }

    /**
     * Configure jul logging.
     */
    public static void configureJULLogging() {
        LOGGER.info("Installing JULToSlf4J bridge (for redirectering Jersey logs)");
        if (LOGGER instanceof ch.qos.logback.classic.Logger) {
            final LevelChangePropagator listener = new LevelChangePropagator();
            final LoggerContext loggerContext = ((ch.qos.logback.classic.Logger) LOGGER).getLoggerContext();
            loggerContext.addListener(listener);
            listener.setContext(loggerContext);
            loggerContext.start();
        }

        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    /**
     * Instantiates a new sL f4 j helper.
     */
    private SLF4JHelper() {
    }
}
