package org.tensin.sonos.commander;

import java.util.List;

import org.tensin.sonos.SonosException;
import org.tensin.sonos.commands.ICommand;
import org.tensin.sonos.control.AVTransportListener;

/**
 * The Interface ISonosController.
 */
public interface ISonosController {

    /**
     * Execute.
     * 
     * @param command
     *            the command
     * @throws SonosException
     *             the sonos exception
     */
    void execute(final String command) throws SonosException;

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
    void execute(final String zoneNames, final String command) throws SonosException;
    
    /**
     * Execute.
     *
     * @param zoneNames the zone names
     * @param command the command
     * @param parameters the parameters
     * @throws SonosException the sonos exception
     */
    void execute(final String zoneNames, final String command, final List<String> parameters) throws SonosException;

    /**
     * Execute.
     *
     * @param destZones the dest zones
     * @param commands the commands
     * @throws SonosException the sonos exception
     */
    void execute(String destZones, ICommand... commands) throws SonosException;
    
    /**
     * Register listener.
     *
     * @param listener the listener
     * @throws SonosException the sonos exception
     */
    void registerListener(final String zones, final AVTransportListener listener) throws SonosException;
}