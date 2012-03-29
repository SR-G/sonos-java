package org.tensin.sonos.run;

import org.tensin.sonos.boot.BootConstants;
import org.tensin.sonos.boot.ClasspathBooter;
import org.tensin.sonos.upnp.SonosException;

public class Sonos {

    /** The Constant BEERDUINO_MAIN_CLASS. */
    private static final String BEERDUINO_MAIN_CLASS = org.tensin.sonos.commander.SonosCommander.class.getName();

    /** The Constant BEERDUINO_BOOT_JAR. */
    private static final String BEERDUINO_BOOT_JAR = "sonos-java-.*\\.jar";

    /** The Constant FAKE_LOG_LABEL. */
    private static final String FAKE_LOG_LABEL = "0 [main] INFO org.tensin.sonos.SonosCommander  - ";

    /** The Constant LINE_SEPARATOR. */
    private static final String LINE_SEPARATOR = "\n     ";

    /**
     * Gestion du classpath.
     * 
     * @param args
     *            the arguments
     */
    public static void main(final String args[]) {
        try {
            final ClasspathBooter cb = new ClasspathBooter(BEERDUINO_BOOT_JAR, "Beerduino");
            // final String libDirectory = cb.getLibraryPathName(BEERDUINO_BOOT_JAR);
            // cb.initLibDirectory(libDirectory);
            cb.addJarsToClasspath(BootConstants.LIBS);
            System.out.println(FAKE_LOG_LABEL + "Classpath :" + cb.displayClasspath(LINE_SEPARATOR));
            System.out.println(FAKE_LOG_LABEL + "Manifest :" + cb.getManifest(BEERDUINO_BOOT_JAR, LINE_SEPARATOR));
            cb.execute(BEERDUINO_MAIN_CLASS, "main", new Class[] { args.getClass() }, new Object[] { args });
        } catch (final SonosException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
