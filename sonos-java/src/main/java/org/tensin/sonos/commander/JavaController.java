package org.tensin.sonos.commander;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensin.sonos.boot.LogInitializer;
import org.tensin.sonos.SonosException;
import org.tensin.sonos.commands.CommandFactory;
import org.tensin.sonos.commands.IStandardCommand;
import org.tensin.sonos.commands.IZoneCommand;
import org.tensin.sonos.commands.ZoneCommandDispatcher;
import org.tensin.sonos.control.ZonePlayer;
import org.tensin.sonos.helpers.CollectionHelper;

/**
 * The Class JavaCommander.
 */
public class JavaController extends AbstractController implements ISonosController {

    /**
     * Creates the controller.
     * 
     * @return the i sonos controller
     */
    public static JavaController createController() {
        return new JavaController();
    }

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(JavaController.class);

    /** The zone command dispatcher. */
    private final ZoneCommandDispatcher zoneCommandDispatcher = ZoneCommandDispatcher.getInstance();

    /**
     * Instantiates a new java controller.
     */
    public JavaController() {
        super();
    }

    @Override
    public void zonePlayerAdded(ZonePlayer player) {
	super.zonePlayerAdded(player);
	if (isWorkOnAllZones()) {
	    final String name = player.getDevicePropertiesService().getZoneAttributes().getName();
	    // in "all zones" mode, commands have not yet been pushed (as we don't know the zone yet, we can't create ZoneCommandExecutor before), so
	    // we propage all needed command to the ZoneCommandDispatcher for him to propagate the command to the newly created ZonecommandExecutor
	    for (final IZoneCommand command : getCommandStackZone()) {
		zoneCommandDispatcher.dispatchCommand(command, name);
	    }
	}
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
        LogInitializer.initLog();
        if (StringUtils.isEmpty(command)) {
            LOGGER.error("No command provided, won't do anything");
        } else {
            try {
                startDiscovery();
                final Collection<String> commandsAvailables = CollectionHelper.convertStringToCollection(command);
                setCommandStackZone((Collection<IZoneCommand>) CommandFactory.createCommandStack(commandsAvailables, IZoneCommand.class));
                setCommandStackStandard((Collection<IStandardCommand>) CommandFactory.createCommandStack(commandsAvailables, IStandardCommand.class));
                executeStandardCommands();
                executeZoneCommands(zoneNames, new ArrayList<String>());
            } finally {
                shutdown();
                zoneCommandDispatcher.logSummary();
                zoneCommandDispatcher.stopExecutors();
            }
        }
    }

    @Override
    protected String getName() {
	return "JavaController";
    }
}
