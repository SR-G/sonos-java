package org.tensin.sonos.run;

import org.tensin.sonos.SonosException;
import org.tensin.sonos.boot.WebBootConstants;
import org.tensin.sonos.boot.ClasspathBooter;
import org.tensin.sonos.helpers.SystemHelper;

/**
 * The Class Sonos.
 */
public class SonosWeb {

    /** The Constant SONOS_MAIN_CLASS. */
    private static final String SONOS_MAIN_CLASS = org.tensin.sonos.commander.WebController.class.getName();

    /** The Constant SONOS_BOOT_JAR. */
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
        final SystemHelper systemHelper = new SystemHelper();
        try {
            final ClasspathBooter cb = new ClasspathBooter(SONOS_BOOT_JAR, "SONOS-Web Interface");
            // final String libDirectory = cb.getLibraryPathName(BEERDUINO_BOOT_JAR);
            // cb.initLibDirectory(libDirectory);
            cb.addJarsToClasspath(WebBootConstants.LIBS);
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