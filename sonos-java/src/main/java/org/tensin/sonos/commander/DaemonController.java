package org.tensin.sonos.commander;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensin.sonos.SonosException;
import org.tensin.sonos.commands.CommandFactory;
import org.tensin.sonos.commands.IStandardCommand;
import org.tensin.sonos.commands.IZoneCommand;
import org.tensin.sonos.commands.ZoneCommandDispatcher;
import org.tensin.sonos.helpers.CollectionHelper;

/**
 * The Class JavaCommander.
 */
public class DaemonController extends AbstractController implements ISonosController {

    /**
     * Creates the controller.
     * 
     * @return the i sonos controller
     */
    public static DaemonController createController() {
        return new DaemonController();
    }

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DaemonController.class);

    /** The zone command dispatcher. */
    private final ZoneCommandDispatcher zoneCommandDispatcher = ZoneCommandDispatcher.getInstance();

    /**
     * Instantiates a new java controller.
     */
    public DaemonController() {
        super();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commander.ISonosController#execute(java.lang.String)
     */
    @Override
    public void execute(final String command) throws SonosException {
        execute("ALL", command);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commander.ISonosController#execute(java.lang.String, java.lang.String)
     */
    @Override
    public void execute(final String zoneNames, final String command) throws SonosException {
        if (StringUtils.isEmpty(command)) {
            LOGGER.error("No command provided, won't do anything");
        } else {
            try {
                final Collection<String> commandsAvailables = CollectionHelper.convertStringToCollection(command);
                setCommandStackZone((Collection<IZoneCommand>) CommandFactory.createCommandStack(commandsAvailables, IZoneCommand.class));
                setCommandStackStandard((Collection<IStandardCommand>) CommandFactory.createCommandStack(commandsAvailables, IStandardCommand.class));
                executeStandardCommands();
                executeZoneCommands(zoneNames, new ArrayList<String>());
            } finally {
                zoneCommandDispatcher.logSummary();
            }
        }
    }

    /**
     * Start.
     * 
     * @throws SonosException
     *             the sonos exception
     */
    public void start() throws SonosException {
        startDiscovery();
    }

    /**
     * Stop.
     * 
     * @throws SonosException
     *             the sonos exception
     */
    public void stop() throws SonosException {
        shutdown();
        zoneCommandDispatcher.stopExecutors();
    }

        @Override
    protected String getName() {
	return "DaemonController";
    }

}
