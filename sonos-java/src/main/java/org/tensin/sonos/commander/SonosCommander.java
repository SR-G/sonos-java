package org.tensin.sonos.commander;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tensin.sonos.SonosConstants;
import org.tensin.sonos.SonosException;
import org.tensin.sonos.commands.CommandFactory;
import org.tensin.sonos.commands.ICommand;
import org.tensin.sonos.commands.IStandardCommand;
import org.tensin.sonos.commands.IZoneCommand;
import org.tensin.sonos.commands.ZoneCommandDispatcher;
import org.tensin.sonos.helpers.CollectionHelper;
import org.tensin.sonos.helpers.SystemHelper;
import org.tensin.sonos.upnp.DiscoverFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

/**
 * The Class SonosCommander.
 */
public class SonosCommander extends AbstractCommander {

    /** The Constant LOGGER. */
    private static final Log LOGGER = LogFactory.getLog(SonosCommander.class);

    /**
     * Gets the system helper.
     * 
     * @return the system helper
     */
    public static SystemHelper getSystemHelper() {
        return systemHelper;
    }

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     * @throws SonosException
     *             the sonos exception
     */
    public static void main(final String args[]) throws SonosException {
        final SonosCommander a = new SonosCommander();
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

    /** The parameters. */
    @Parameter(description = "Additionnal command parameters")
    private List<String> parameters = new ArrayList<String>();

    /** The control port. */
    @Parameter(names = { "--control-port" }, description = "Control port (needed for SSDP discovery, default = 8009)")
    private String controlPort;

    /** The system helper. */
    private static SystemHelper systemHelper = new SystemHelper();

    /**
     * Builds the j commander from command line.
     * 
     * @param args
     *            the args
     * @return the j commander
     * @throws SonosException
     *             the sonos exception
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
        setCommandStackZone((Collection<IZoneCommand>) CommandFactory.createCommandStack(commandsAvailables, IZoneCommand.class));
        setCommandStackStandard((Collection<IStandardCommand>) CommandFactory.createCommandStack(commandsAvailables, IStandardCommand.class));
        if (!checkAllCommandsHaveBeenMapped(commandsAvailables)) {
            LOGGER.error("The following commands haven't been recognized : " + CollectionHelper.singleDump(commandsAvailables));
            usage(jCommander);
        }
        if ((getCommandStackZone().size() == 0) && (getCommandStackStandard().size() == 0)) {
            usage(jCommander);
        }
        LOGGER.debug("Standard commands to run : \n" + CollectionHelper.singleDump(getCommandStackStandard()));
        LOGGER.debug("Zone commands to run : \n" + CollectionHelper.singleDump(getCommandStackZone()));

        if (StringUtils.isNotBlank(controlPort)) {
            try {
                final int port = Integer.valueOf(controlPort).intValue();
                LOGGER.info("Using SSDP control port [" + port + "]");
                DiscoverFactory.setDiscoverControlPort(port);
            } catch (NumberFormatException e) {
                LOGGER.error("Can't convert as integer the provided SSDP control port [" + controlPort + "], will still use the default one ["
                        + SonosConstants.DEFAULT_SSDP_CONTROL_PORT + "]");
            }
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
     * Gets the command.
     * 
     * @return the command
     */
    public String getCommand() {
        return command;
    }

    /**
     * Gets the control port.
     * 
     * @return the control port
     */
    public String getControlPort() {
        return controlPort;
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
     * Sets the control port.
     * 
     * @param controlPort
     *            the new control port
     */
    public void setControlPort(final String controlPort) {
        this.controlPort = controlPort;
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
            final JavaCommander javaCommander = new JavaCommander();
            javaCommander.setCommandStackStandard(getCommandStackStandard());
            javaCommander.setCommandStackZone(getCommandStackZone());
            javaCommander.executeStandardCommands(); // sequential executions
            javaCommander.executeZoneCommands(zone, parameters); // should halt until all commands are executed or timeout elapsed
        } finally {
            ZoneCommandDispatcher.getInstance().logSummary();
            ZoneCommandDispatcher.getInstance().stopExecutors();
            systemHelper.exit(0);
        }
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
        boolean needArgs;
        String description;
        for (final ICommand c : CommandFactory.getAvailableCommands(ICommand.class)) {
            needArgs = c.needArgs();
            description = c.getDescription();

            sb.append("    ").append(StringUtils.rightPad(c.getName(), 30, " "));
            if (StringUtils.isNotEmpty(description)) {
                sb.append(" : ").append(description);
            }
            if (needArgs) {
                sb.append(" [additionnal parameters needed]");
            }
            sb.append("\n");
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
