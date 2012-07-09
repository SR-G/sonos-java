package org.tensin.sonos.commander;

import java.util.Collection;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.tensin.sonos.commands.IStandardCommand;
import org.tensin.sonos.commands.IZoneCommand;

/**
 * The Class AbstractCommander.
 */
public abstract class AbstractCommander {

    /** The init log. */
    private static boolean initLog;

    /** The command stack zone. */
    private Collection<IZoneCommand> commandStackZone;

    /** The command stack standard. */
    private Collection<IStandardCommand> commandStackStandard;

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
     * Inits the log.
     */
    protected void initLog() {
        if (!initLog) {
            BasicConfigurator.configure();
            Logger rootLogger = Logger.getRootLogger();
            rootLogger.setLevel(Level.INFO);
            initLog = true;
        }
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
     * Sets the command stack zone.
     * 
     * @param commandStackZone
     *            the new command stack zone
     */
    public void setCommandStackZone(final Collection<IZoneCommand> commandStackZone) {
        this.commandStackZone = commandStackZone;
    }

}
