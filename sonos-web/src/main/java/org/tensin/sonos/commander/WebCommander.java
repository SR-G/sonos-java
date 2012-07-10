package org.tensin.sonos.commander;

import org.tensin.sonos.SonosException;
import org.tensin.sonos.SonosJetty;
import org.tensin.sonos.SonosWebConstants;

import com.beust.jcommander.Parameter;

/**
 * The Class WebCommander.
 */
public class WebCommander extends AbstractCommander {

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     * @throws SonosException
     *             the sonos exception
     */
    public static void main(final String[] args) throws SonosException {
        final WebCommander webCommander = new WebCommander();
        webCommander.start(args);
    }

    /** The port. */
    @Parameter(names = { "--http-port" }, description = "HTTP Port to run the web application (default : " + SonosWebConstants.DEFAULT_HTTP_PORT + ")")
    private int port = SonosWebConstants.DEFAULT_HTTP_PORT;

    /**
     * Gets the port.
     * 
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the port.
     * 
     * @param port
     *            the new port
     */
    public void setPort(final int port) {
        this.port = port;
    }

    /**
     * Start.
     * 
     * @throws SonosException
     *             the sonos exception
     */
    public void start(final String[] args) throws SonosException {
        initLog();
        final SonosJetty jetty = new SonosJetty();
        jetty.setPort(getPort());
        jetty.start("src/main/webapp/", "/");
    }
}
