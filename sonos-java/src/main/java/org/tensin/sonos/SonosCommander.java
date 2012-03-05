package org.tensin.sonos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.tensin.sonos.commands.AbstractCommand;
import org.tensin.sonos.commands.CommandFactory;
import org.tensin.sonos.commands.ICommand;
import org.tensin.sonos.commands.IStandardCommand;
import org.tensin.sonos.commands.IZoneCommand;
import org.tensin.sonos.commands.ZoneCommandDispatcher;
import org.tensin.sonos.helpers.CollectionHelper;
import org.tensin.sonos.upnp.DiscoverFactory;
import org.tensin.sonos.upnp.DiscoverImpl.Listener;
import org.tensin.sonos.upnp.IDiscover;
import org.tensin.sonos.upnp.SonosException;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

public class SonosCommander {

    /**
     * The listener interface for receiving zonesDiscovered events. The class
     * that is interested in processing a zonesDiscovered event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's <code>addZonesDiscoveredListener<code> method. When
     * the zonesDiscovered event occurs, that object's appropriate
     * method is invoked.
     * 
     * @see ZonesDiscoveredEvent
     */
    class ZonesDiscoveredListener implements Listener {

        /**
         * {@inheritDoc}
         * 
         * @see org.tensin.sonos.upnp.DiscoverImpl.Listener#found(java.lang.String)
         */
        @Override
        public void found(final String host) {
            try {
                ISonos sonos = SonosFactory.build(host);
                sonos.refreshZoneAttributes();
                String name = sonos.getZoneName();
                if (StringUtils.isNotEmpty(name)) {
                    LOGGER.info("New zone found [" + name + "]");
                    if (debug) {
                        sonos.trace_io(true);
                        sonos.trace_reply(true);
                        sonos.trace_browse(true);
                    }
                    ZoneCommandDispatcher.getInstance().registerZoneAsAvailable(sonos, name);
                    if (workOnAllZones) {
                        for (final IZoneCommand command : commandStackZone) {
                            ZoneCommandDispatcher.getInstance().dispatchCommand(command, name);
                        }
                    }
                }
            } catch (SonosException e) {
                LOGGER.error("Internal error while working on new found host [" + host + "]", e);
            }
        }
    }

    private static final int DEFAULT_MAX_TIMEOUT_WHEN_WORKING_ON_ALL_ZONES = 10000;

    /** The Constant LOGGER. */
    private static final Log LOGGER = LogFactory.getLog(SonosCommander.class);

    /** The init log. */
    private static boolean initLog;

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     * @throws SonosException
     *             the sonos exception
     */
    public static void main(final String args[]) throws SonosException {
        SonosCommander a = new SonosCommander();
        a.start(args);
    }

    /**
     * Sets the system helper.
     * 
     * @param systemHelper
     *            the new system helper
     */
    public static void setSystemHelper(final SystemHelper systemHelper) {
        SonosCommander.systemHelper = systemHelper;
    }

    /** The zone. */
    @Parameter(names = { "--zone", "-z" }, description = "Sonos zone logic name to run command on. Put 'ALL' for interacting on all zones, or separate zones with comma. Valid examples are : '-z kitchen', '-z ALL', '-z kitchen,room', '-z 192.168.1.54'.")
    private String zone = "";

    /** The command. */
    @Parameter(names = { "--command", "-c" }, description = "Command to be run")
    private String command;

    /** The debug. */
    @Parameter(names = "--debug", description = "Debug mode")
    private boolean debug;

    /** The usage. */
    @Parameter(names = { "-h", "--usage", "--help" }, description = "Shows available commands")
    private boolean usage;

    /** The zones to work on. */
    private Collection<String> zonesToWorkOn;

    /** The command stack standard. */
    private Collection<IStandardCommand> commandStackStandard;

    /** The command stack zone. */
    private Collection<IZoneCommand> commandStackZone;

    /** The work on all zones. */
    private boolean workOnAllZones = false;

    /** The parameters. */
    @Parameter(description = "Additionnal command parameters")
    private List<String> parameters = new ArrayList<String>();

    /** The system helper. */
    private static SystemHelper systemHelper = new SystemHelper();

    /**
     * Gets the system helper.
     * 
     * @return the system helper
     */
    public static SystemHelper getSystemHelper() {
        return systemHelper;
    }

    /** The discover. */
    private IDiscover discover;

    /**
     * Builds the j commander from command line.
     * 
     * @param args
     *            the args
     * @return the j commander
     */
    private void buildJCommanderFromCommandLine(final String[] args) throws SonosException {
        JCommander jCommander = null;
        try {
            jCommander = new JCommander(this, args);
        } catch (ParameterException e) {
            LOGGER.error("The given options haven't been recognized : " + CollectionHelper.singleDump(Arrays.asList(args)));
            jCommander = new JCommander(this);
            usage(jCommander);
        }
        if (usage || (args == null) || (args.length == 0)) {
            usage(jCommander);
        }
        if (debug) {
            LOGGER.info("Debug activated");
        }
        Collection<String> commandsAvailables = CollectionHelper.convertStringToCollection(command);
        commandStackZone = (Collection<IZoneCommand>) CommandFactory.createCommandStack(commandsAvailables, IZoneCommand.class);
        commandStackStandard = (Collection<IStandardCommand>) CommandFactory.createCommandStack(commandsAvailables, IStandardCommand.class);
        if (!checkAllCommandsHaveBeenMapped(commandsAvailables)) {
            LOGGER.error("The following commands haven't been recognized : " + CollectionHelper.singleDump(commandsAvailables));
            usage(jCommander);
        }
        if ((commandStackZone.size() == 0) && (commandStackStandard.size() == 0)) {
            usage(jCommander);
        }

    }

    /**
     * Check all commands have been mapped.
     * 
     * @param commandsAvailables
     *            the commands availables
     * @return true, if successful
     */
    private boolean checkAllCommandsHaveBeenMapped(final Collection<String> commandsAvailables) {
        return CollectionUtils.isEmpty((commandsAvailables));
    }

    /**
     * Detect if work on all zones.
     */
    private void detectIfWorkOnAllZones() {
        if ((zonesToWorkOn == null) || (zonesToWorkOn.size() == 0)) {
            // No zones provided on command line => we will work on every zone
            // found
            workOnAllZones = true;
        } else {
            final Iterator<String> itr = zonesToWorkOn.iterator();
            String s;
            while (itr.hasNext()) {
                s = itr.next();
                // ALL keyword has been specified, we'll work on every zone
                // found too
                if (s.equalsIgnoreCase("ALL")) {
                    itr.remove();
                    workOnAllZones = true;
                }
            }
        }
        if (workOnAllZones) {
            LOGGER.info("Working on all available zones");
        } else {
            LOGGER.info("Working on zones " + CollectionHelper.singleDump(zonesToWorkOn) + "");
        }
    }

    /**
     * Execute standard commands.
     * 
     * @throws SonosException
     *             the sonos exception
     */
    private void executeStandardCommands() throws SonosException {
        if (!CollectionUtils.isEmpty(commandStackStandard)) {
            for (IStandardCommand command : commandStackStandard) {
                command.execute();
            }
        }
    }

    /**
     * Execute zone commands.
     * 
     * @throws SonosException
     */
    private void executeZoneCommands() throws SonosException {
        if (!CollectionUtils.isEmpty(commandStackZone)) {
            try {
                startDiscovery();
                zonesToWorkOn = CollectionHelper.convertStringToCollection(zone);
                detectIfWorkOnAllZones();
                if (!workOnAllZones) {
                    // We propagate immediately the command that have to be runned
                    // on a specific zone, even if that zone has still not be found
                    // (it's up to the discovery process to detect Sonos box and to
                    // fire up that event to the corresponding executor, allowing at
                    // that time the executor to run every command that are in its
                    // queue.
                    for (final IZoneCommand command : commandStackZone) {
                        ((AbstractCommand) command).setArgs(parameters);
                        for (final String zoneToWorkOn : zonesToWorkOn) {
                            ZoneCommandDispatcher.getInstance().dispatchCommand(command, zoneToWorkOn);
                        }
                    }
                } else {
                    // Commands will be re-forwared from listener each time a new
                    // zones is discovered
                    // TODO purge command list after a certain amount of time, maybe
                    // ?
                }
                ZoneCommandDispatcher.getInstance().waitEndExecution(DEFAULT_MAX_TIMEOUT_WHEN_WORKING_ON_ALL_ZONES, !workOnAllZones);
                // if "ALL" mode, then we don't want to check empty queues (are queues may be filled at a later time, once a new zone will be
                // discovered (and at this time, the wanted commands will be propagated by the listener)
            } finally {
                stopDiscovery();
            }
        }
    }

    /**
     * Gets the command.
     * 
     * @return the command
     */
    public String getCommand() {
        return command;
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
     * Gets the command stack zone.
     * 
     * @return the command stack zone
     */
    public Collection<IZoneCommand> getCommandStackZone() {
        return commandStackZone;
    }

    /**
     * Gets the parameters.
     * 
     * @return the parameters
     */
    public List<String> getParameters() {
        return parameters;
    }

    /**
     * Gets the zone.
     * 
     * @return the zone
     */
    public String getZone() {
        return zone;
    }

    /**
     * Inits the log.
     */
    private void initLog() {
        if (!initLog) {
            BasicConfigurator.configure();
            Logger rootLogger = Logger.getRootLogger();
            rootLogger.setLevel(Level.INFO);
            initLog = true;
        }
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
     * Sets the command.
     * 
     * @param command
     *            the new command
     */
    public void setCommand(final String command) {
        this.command = command;
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

    /**
     * Sets the parameters.
     * 
     * @param parameters
     *            the new parameters
     */
    public void setParameters(final List<String> parameters) {
        this.parameters = parameters;
    }

    /**
     * Sets the zone.
     * 
     * @param zone
     *            the new zone
     */
    public void setZone(final String zone) {
        this.zone = zone;
    }

    /**
     * Start.
     * 
     * @param args
     *            the args
     * @throws SonosException
     *             the sonos exception
     */
    @SuppressWarnings("unchecked")
    private void start(final String[] args) throws SonosException {
        try {
            initLog();
            buildJCommanderFromCommandLine(args);
            executeStandardCommands();
            executeZoneCommands();
        } finally {
            ZoneCommandDispatcher.getInstance().logSummary();
            ZoneCommandDispatcher.stopExecutors();
            systemHelper.exit(0);
        }
    }

    /**
     * Start discovery.
     * 
     * @throws SonosException
     *             the sonos exception
     */
    private void startDiscovery() throws SonosException {
        Listener zonesDiscoveredListener = new ZonesDiscoveredListener();
        discover = DiscoverFactory.build(zonesDiscoveredListener);
        discover.launch();
    }

    /**
     * Stop discovery.
     * 
     * @throws SonosException
     *             the sonos exception
     */
    private void stopDiscovery() throws SonosException {
        discover.done();
    }

    /**
     * Usage.
     * 
     * @param jCommander
     *            the j commander
     */
    private void usage(final JCommander jCommander) {
        StringBuilder sb = new StringBuilder();
        jCommander.usage(sb);
        sb.append("\n");
        sb.append("  Commands :").append("\n");
        for (final ICommand c : CommandFactory.getAvailableCommands(ICommand.class)) {
            sb.append("    ").append(c.getName()).append("\n");
        }
        sb.append("\n");
        sb.append("  Examples :\n");
        sb.append("    --command play --zone kitchen,room        Starts music in kitchen and room").append("\n");
        sb.append("    --command volume 25 --zone kitchen        Set volume to 25 in kitchen").append("\n");
        sb.append("    --command pause --zone all                Pause all found zones").append("\n");
        sb.append("    --command pause                           Pause all found zones").append("\n");
        systemHelper.outln(sb.toString());
        systemHelper.exit(0);
    }
}
