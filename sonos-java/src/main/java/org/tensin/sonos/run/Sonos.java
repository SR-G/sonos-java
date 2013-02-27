package org.tensin.sonos.run;

import org.tensin.sonos.SonosException;
import org.tensin.sonos.boot.CLIBootConstants;
import org.tensin.sonos.boot.ClasspathBooter;
import org.tensin.sonos.helpers.SystemHelper;

/**
 * The Class Sonos.
 */
public class Sonos {

    /** The Constant BEERDUINO_MAIN_CLASS. */
    private static final String SONOS_MAIN_CLASS = org.tensin.sonos.commander.CLIController.class.getName();

    /** The Constant BEERDUINO_BOOT_JAR. */
    private static final String SONOS_BOOT_JAR = "sonos-java-.*\\.jar";

    /** The Constant FAKE_LOG_LABEL. */
    private static final String FAKE_LOG_LABEL = "0 [main] INFO org.tensin.sonos.commander.SonosCommander  - ";

    /** The Constant LINE_SEPARATOR. */
    private static final String LINE_SEPARATOR = "\n     ";

    /**
     * Gestion du classpath.
     * 
     * @param args
     *            the arguments
     */
    public static void main(final String args[]) {
        System.setProperty("java.net.preferIPv4Stack", "true"); // CLINQ library is not compatible with IPv6 stack (which is, by the way, for example the default in recent archlinux distributions)
        final SystemHelper systemHelper = new SystemHelper();
        try {
            final ClasspathBooter cb = new ClasspathBooter(SONOS_BOOT_JAR, "SONOS-Command Line Interface");
            // final String libDirectory = cb.getLibraryPathName(BEERDUINO_BOOT_JAR);
            // cb.initLibDirectory(libDirectory);
            cb.addJarsToClasspath(CLIBootConstants.LIBS);
            systemHelper.outln(FAKE_LOG_LABEL + "Classpath :" + cb.displayClasspath(LINE_SEPARATOR));
            systemHelper.outln(FAKE_LOG_LABEL + "Manifest :" + cb.getManifest(SONOS_BOOT_JAR, LINE_SEPARATOR));
            cb.execute(SONOS_MAIN_CLASS, "main", new Class[] { args.getClass() }, new Object[] { args });
        } catch (final SonosException e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            systemHelper.exit(0);
        }
    }
}