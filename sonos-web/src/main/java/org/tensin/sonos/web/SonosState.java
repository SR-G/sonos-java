package org.tensin.sonos.web;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensin.sonos.ISonos;
import org.tensin.sonos.commands.IZoneCommand;
import org.tensin.sonos.commands.ZoneCommandDispatcher;
import org.tensin.sonos.commands.ZoneCommandExecutor;

import com.vaadin.data.util.IndexedContainer;

/**
 * The Class SonosState.
 */
public class SonosState {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SonosState.class);

    /** The Constant INSTANCE. */
    private static final SonosState INSTANCE = new SonosState();

    /**
     * Gets the single instance of SonosState.
     * 
     * @return single instance of SonosState
     */
    public static SonosState getInstance() {
        return INSTANCE;
    }

    /** The selected zone. */
    private String selectedZone;

    /** The zones data. */
    private IndexedContainer zonesData;

    /** The playlist data. */
    private IndexedContainer playlistData;

    /**
     * Gets the playlist data.
     * 
     * @return the playlist data
     */
    public IndexedContainer getPlaylistData() {
        if (playlistData == null) {
            playlistData = new IndexedContainer();
            playlistData.addContainerProperty("Title", String.class, "");
        }
        return playlistData;
    }

    /**
     * Gets the selected sonos.
     * 
     * @return the selected sonos
     */
    private ISonos getSelectedSonos() {
        final ZoneCommandExecutor executor = ZoneCommandDispatcher.getInstance().getZoneCommandExecutor(getSelectedZoneName());
        if (executor == null) {
            LOGGER.error("Can't find executor [" + getSelectedZoneName() + "] (not yed found on the network ?)");
            return null;
        } else {
            return executor.getSonosZone();
        }
    }

    /**
     * Gets the selected zone.
     * 
     * @return the selected zone
     */
    public String getSelectedZoneName() {
        return selectedZone;
    }

    /**
     * Gets the zones data.
     * 
     * @return the zones data
     */
    public IndexedContainer getZonesData() {
        if (zonesData == null) {
            zonesData = new IndexedContainer();
            zonesData.addContainerProperty("Name", String.class, "");
        }
        return zonesData;
    }

    /**
     * Send command.
     * 
     * @param command
     *            the command
     */
    public void sendCommand(final IZoneCommand command) {
        if (StringUtils.isEmpty(getSelectedZoneName())) {
            LOGGER.error("Selected zone name not set");
        } else {
            final ZoneCommandExecutor executor = ZoneCommandDispatcher.getInstance().getZoneCommandExecutor(getSelectedZoneName());
            if (executor == null) {
                LOGGER.error("Can't find executor [" + getSelectedZoneName() + "] (not yed found on the network ?)");
            } else {
                executor.addCommand(command);
            }
        }
    }

    /**
     * Sets the playlist data.
     * 
     * @param playlistData
     *            the new playlist data
     */
    public void setPlaylistData(final IndexedContainer playlistData) {
        this.playlistData = playlistData;
    }

    /**
     * Sets the selected zone.
     * 
     * @param selectedZone
     *            the new selected zone
     */
    public void setSelectedZone(final String selectedZone) {
        this.selectedZone = selectedZone;
    }

    /**
     * Sets the zones data.
     * 
     * @param zonesData
     *            the new zones data
     */
    public void setZonesData(final IndexedContainer zonesData) {
        this.zonesData = zonesData;
    }

}
