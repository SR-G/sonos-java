package org.tensin.sonos.commander;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tensin.sonos.SonosConstants;
import org.tensin.sonos.commands.AbstractCommand;
import org.tensin.sonos.commands.CommandFactory;
import org.tensin.sonos.commands.IStandardCommand;
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

    /** The debug. */
    private boolean debug;

    /** The command stack standard. */
    private Collection<IStandardCommand> commandStackStandard;

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
                final Collection<String> commandsAvailables = CollectionHelper.convertStringToCollection(command);
                setCommandStackZone((Collection<IZoneCommand>) CommandFactory.createCommandStack(commandsAvailables, IZoneCommand.class));
                setCommandStackStandard((Collection<IStandardCommand>) CommandFactory.createCommandStack(commandsAvailables, IStandardCommand.class));
                executeStandardCommands();
                executeZoneCommands(zoneNames, new ArrayList<String>());
            } finally {
                ZoneCommandDispatcher.getInstance().logSummary();
                ZoneCommandDispatcher.getInstance().stopExecutors();
            }
        }
    }

    /**
     * Execute standard commands.
     * 
     * @throws SonosException
     *             the sonos exception
     */
    public void executeStandardCommands() throws SonosException {
        if (!CollectionUtils.isEmpty(commandStackStandard)) {
            for (final IStandardCommand command : commandStackStandard) {
                command.execute();
            }
        }
    }

    /**
     * Execute zone commands.
     * 
     * @throws SonosException
     */
    public void executeZoneCommands(final String zone, final List<String> parameters) throws SonosException {
        if (!CollectionUtils.isEmpty(getCommandStackZone())) {
            try {
                startDiscovery(debug);
                setZonesToWorkOn(CollectionHelper.convertStringToCollection(zone));
                detectIfWorkOnAllZones();
                if (!isWorkOnAllZones()) {
                    // We propagate immediately the command that have to be runned
                    // on a specific zone, even if that zone has still not be found
                    // (it's up to the discovery process to detect Sonos box and to
                    // fire up that event to the corresponding executor, allowing at
                    // that time the executor to run every command that are in its
                    // queue.
                    for (final IZoneCommand command : getCommandStackZone()) {
                        ((AbstractCommand) command).setArgs(parameters);
                        for (final String zoneToWorkOn : getZonesToWorkOn()) {
                            ZoneCommandDispatcher.getInstance().dispatchCommand(command, zoneToWorkOn);
                        }
                    }
                } else {
                    // Commands will be re-forwared from listener each time a new
                    // zones is discovered
                    // TODO purge command list after a certain amount of time, maybe
                    // ?
                }
                ZoneCommandDispatcher.getInstance().waitEndExecution(SonosConstants.DEFAULT_MAX_TIMEOUT_SONOS_COMMANDER_WHEN_WORKING_ON_ALL_ZONES,
                        !isWorkOnAllZones());
                // if "ALL" mode, then we don't want to check empty queues (are queues may be filled at a later time, once a new zone will be
                // discovered (and at this time, the wanted commands will be propagated by the listener)
            } finally {
                stopDiscovery();
            }
        }
    }

    /**
     * Gets the command stack standard.
     * 
     * @return the command stack standard
     */
    public Collection<IStandardCommand> getCommandStackStandard() {
        return commandStackStandard;
    }

    /**
     * Checks if is debug.
     * 
     * @return true, if is debug
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * Sets the command stack standard.
     * 
     * @param commandStackStandard
     *            the new command stack standard
     */
    public void setCommandStackStandard(final Collection<IStandardCommand> commandStackStandard) {
        this.commandStackStandard = commandStackStandard;
    }

    /**
     * Sets the debug.
     * 
     * @param debug
     *            the new debug
     */
    public void setDebug(final boolean debug) {
        this.debug = debug;
    }
}
