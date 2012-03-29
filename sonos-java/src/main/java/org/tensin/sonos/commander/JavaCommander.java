package org.tensin.sonos.commander;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tensin.sonos.commands.CommandFactory;
import org.tensin.sonos.commands.IZoneCommand;
import org.tensin.sonos.commands.ZoneCommandDispatcher;
import org.tensin.sonos.helpers.CollectionHelper;
import org.tensin.sonos.upnp.SonosException;

/**
 * The Class JavaCommander.
 */
public class JavaCommander extends AbstractCommander {

    /** The Constant LOGGER. */
    private static final Log LOGGER = LogFactory.getLog(JavaCommander.class);

    /** The Constant DEFAULT_MAX_TIMEOUT_WHEN_WORKING_ON_ALL_ZONES. */
    private static final int DEFAULT_MAX_TIMEOUT_WHEN_WORKING_ON_ALL_ZONES = 3000;

    /**
     * Execute.
     * 
     * @param command
     *            the command
     * @throws SonosException
     *             the sonos exception
     */
    public void execute(final String command) throws SonosException {
        execute("ALL", command);
    }

    /**
     * Execute.
     * 
     * @param zoneNames
     *            the zone names
     * @param command
     *            the command
     * @throws SonosException
     *             the sonos exception
     */
    public void execute(final String zoneNames, final String command) throws SonosException {
        initLog();
        if (StringUtils.isEmpty(command)) {
            LOGGER.error("No command provided, won't do anything");
        } else {
            try {
                startDiscovery();
                final Collection<String> commandsAvailables = CollectionHelper.convertStringToCollection(command);
                setCommandStackZone((Collection<IZoneCommand>) CommandFactory.createCommandStack(commandsAvailables, IZoneCommand.class));
                setZonesToWorkOn(CollectionHelper.convertStringToCollection(zoneNames));
                detectIfWorkOnAllZones();
                if (!isWorkOnAllZones()) {
                    for (final IZoneCommand c : getCommandStackZone()) {
                        // ((AbstractCommand) c).setArgs(parameters); // TODO ?
                        for (final String zoneToWorkOn : getZonesToWorkOn()) {
                            ZoneCommandDispatcher.getInstance().dispatchCommand(c, zoneToWorkOn);
                        }
                    }
                } else {
                    // Commands will be re-forwared from listener each time a new
                    // zones is discovered
                    // TODO purge command list after a certain amount of time, maybe
                    // ?
                }
                ZoneCommandDispatcher.getInstance().waitEndExecution(DEFAULT_MAX_TIMEOUT_WHEN_WORKING_ON_ALL_ZONES, !isWorkOnAllZones());
            } finally {
                stopDiscovery();
                ZoneCommandDispatcher.getInstance().logSummary();
                ZoneCommandDispatcher.getInstance().stopExecutors();
            }
        }
    }
}
