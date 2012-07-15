package org.tensin.sonos.web;

import org.tensin.sonos.commands.IZoneCommand;
import org.tensin.sonos.commands.ZoneCommandDispatcher;
import org.tensin.sonos.control.ZonePlayer;
import org.tensin.sonos.model.MusicLibrary;

import com.vaadin.data.util.IndexedContainer;

/**
 * The Interface ISonosState.
 */
public interface ISonosState {

    /**
     * Gets the music library.
     * 
     * @param zoneName
     *            the zone name
     * @return the music library
     */
    public abstract MusicLibrary getMusicLibrary(final String zoneName);

    /**
     * Gets the playlist data.
     * 
     * @return the playlist data
     */
    public abstract IndexedContainer getPlaylistData();

    /**
     * Gets the selected sonos.
     * 
     * @return the selected sonos
     */
    public abstract ZonePlayer getSelectedSonos();

    /**
     * Gets the selected zone.
     * 
     * @return the selected zone
     */
    public abstract String getSelectedZoneName();

    /**
     * Gets the zone command dispatcher.
     * 
     * @return the zone command dispatcher
     */
    public abstract ZoneCommandDispatcher getZoneCommandDispatcher();

    /**
     * Gets the zones data.
     * 
     * @return the zones data
     */
    public abstract IndexedContainer getZonesData();

    /**
     * Load music library.
     * 
     * @param zone
     *            the zone
     * @param name
     *            the name
     */
    public abstract void loadMusicLibrary(final ZonePlayer zone, final String name);

    /**
     * Send command.
     * 
     * @param command
     *            the command
     */
    public abstract void sendCommand(final IZoneCommand command);

    /**
     * Sets the playlist data.
     * 
     * @param playlistData
     *            the new playlist data
     */
    public abstract void setPlaylistData(final IndexedContainer playlistData);

    /**
     * Sets the selected zone.
     * 
     * @param selectedZone
     *            the new selected zone
     */
    public abstract void setSelectedZone(final String selectedZone);

    /**
     * Sets the zones data.
     * 
     * @param zonesData
     *            the new zones data
     */
    public abstract void setZonesData(final IndexedContainer zonesData);

}