package org.tensin.sonos.helpers;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.LoggerConfig;

/**
 * The Class SLF4JHelper.
 */
public final class LogHelper {

    /**
     * MÃ©thode setLoggerLevel.
     * 
     * @param module
     *            the module
     * @param level
     *            the level
     */
    public static void changeLoggerLevel(final String module, final Level level) {
        final String moduleRenamed = module.replaceAll("/", ".");
        final org.apache.logging.log4j.core.LoggerContext ctx = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
        final org.apache.logging.log4j.core.config.AbstractConfiguration configuration = (org.apache.logging.log4j.core.config.AbstractConfiguration) ctx
                .getConfiguration();
        if (configuration.getLogger(moduleRenamed) != null) {
            final LoggerConfig loggerConfig = configuration.getLoggerConfig(moduleRenamed);
            loggerConfig.setLevel(level);
        } else {
            final LoggerConfig loggerConfig = new LoggerConfig(moduleRenamed, level, true);
            configuration.addLogger(moduleRenamed, loggerConfig);
        }
        ctx.updateLoggers(configuration);
    }
    
    /**
     * Change logger level.
     *
     * @param module the module
     * @param level the level
     */
    public static void changeLoggerLevel(final String module, final String level) {
        changeLoggerLevel(module, Level.valueOf(level));
    }
    
    /**
     * Inits the loggers.
     */
    public static void initLoggers() {
        System.setProperty("log4j.configuration", ""); // zapper la configuration LOG4J 
        System.setProperty("org.jboss.logging.provider", "log4j2"); // Forcer jboss-logging à utiliser l'implémentation LOG4J2
        System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager"); // Forcer le logger du JDK (JUL) à utiliser l'implémentation LOG4J2 (nécessite le bridge log4j-jul à partir de log4j2 2.1+)
    }

    /**
     * Instantiates a new sL f4 j helper.
     */
    private LogHelper() {
    }
}