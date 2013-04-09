package org.tensin.sonos.commander;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensin.sonos.SonosException;
import org.tensin.sonos.SonosJetty;
import org.tensin.sonos.SonosWebConstants;
import org.tensin.sonos.commands.ZoneCommandDispatcher;
import org.tensin.sonos.control.ZonePlayer;
import org.tensin.sonos.web.ISonosState;
import org.tensin.sonos.web.SonosState;

import com.beust.jcommander.Parameter;
import org.tensin.sonos.boot.LogInitializer;

/**
 * The Class WebCommander.
 */
public class WebController extends AbstractController implements ISonosController {

    /**
     * Creates the controller.
     * 
     * @return the i sonos controller
     */
    public static WebController createController() {
        return new WebController();
    }

    /** The sonos state. */
    private final ISonosState sonosState = SonosState.getInstance();

    /** The zone command dispatcher. */
    private final ZoneCommandDispatcher zoneCommandDispatcher = SonosState.getInstance().getZoneCommandDispatcher();

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(WebController.class);

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     * @throws SonosException
     *             the sonos exception
     */
    public static void main(final String[] args) throws SonosException {
        final WebController webCommander = createController();
        webCommander.start(args);
    }

    @Override
    public void zonePlayerAdded(ZonePlayer player) {
	super.zonePlayerAdded(player);
	final String name = player.getDevicePropertiesService().getZoneAttributes().getName();
	synchronized (sonosState.getZonesData()) {
	    final Object id = sonosState.getZonesData().addItem();
	    sonosState.getZonesData().getContainerProperty(id, "Name").setValue(name);
	    sonosState.loadMusicLibrary(player, name);
	}
    }

    @Override
    public void zonePlayerRemoved(ZonePlayer player) {
	super.zonePlayerRemoved(player);
    }

    /** The port. */
    @Parameter(names = { "--http-port" }, description = "HTTP Port to run the web application (default : " + SonosWebConstants.DEFAULT_HTTP_PORT + ")")
    private int port = SonosWebConstants.DEFAULT_HTTP_PORT;

    /**
     * Instantiates a new web controller.
     */
    protected WebController() {
        super();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commander.ISonosController#execute(java.lang.String)
     */
    @Override
    public void execute(final String command) throws SonosException {
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commander.ISonosController#execute(java.lang.String, java.lang.String)
     */
    @Override
    public void execute(final String zoneNames, final String command) throws SonosException {
    }

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
     * @param args
     *            the args
     * @throws SonosException
     *             the sonos exception
     */
    public void start(final String[] args) throws SonosException {
        LogInitializer.initLog();
        final SonosJetty jetty = new SonosJetty();
        startDiscovery();
        jetty.setPort(getPort());
        jetty.start("src/main/webapp/", "/");
    }

    @Override
    protected String getName() {
	return "WebController";
    }
}
