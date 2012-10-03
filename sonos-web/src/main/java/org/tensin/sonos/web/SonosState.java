package org.tensin.sonos.web;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensin.sonos.commands.IZoneCommand;
import org.tensin.sonos.commands.ZoneCommandDispatcher;
import org.tensin.sonos.commands.ZoneCommandExecutor;
import org.tensin.sonos.control.ZonePlayer;
import org.tensin.sonos.model.MusicLibrary;

import com.vaadin.data.util.IndexedContainer;

/**
 * The Class SonosState.
 */
public class SonosState implements ISonosState {

    /** The zone command dispatcher. */
    private final ZoneCommandDispatcher zoneCommandDispatcher = new ZoneCommandDispatcher();

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SonosState.class);

    /** The libraries. */
    private final Map<String, MusicLibrary> libraries = new HashMap<String, MusicLibrary>();

    /** The selected zone. */
    private String selectedZone;

    /** The zones data. */
    private IndexedContainer zonesData;

    /** The playlist data. */
    private IndexedContainer playlistData;

    /** The instance. */
    private static ISonosState INSTANCE = new SonosState();

    private static boolean indexed;

    /**
     * Gets the single instance of SonosState.
     * 
     * @return single instance of SonosState
     */
    public static ISonosState getInstance() {
        return INSTANCE;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.web.ISonosState#getMusicLibrary(java.lang.String)
     */
    @Override
    public MusicLibrary getMusicLibrary(final String zoneName) {
        return libraries.get(zoneName);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.web.ISonosState#getPlaylistData()
     */
    @Override
    public IndexedContainer getPlaylistData() {
        if (playlistData == null) {
            playlistData = new IndexedContainer();
            playlistData.addContainerProperty("Title", String.class, "");
        }
        return playlistData;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.web.ISonosState#getSelectedSonos()
     */
    @Override
    public ZonePlayer getSelectedSonos() {
        final ZoneCommandExecutor executor = zoneCommandDispatcher.getZoneCommandExecutor(getSelectedZoneName());
        if (executor == null) {
            LOGGER.error("Can't find executor [" + getSelectedZoneName() + "] (not yed found on the network ?)");
            return null;
        } else {
            return executor.getSonosZone();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.web.ISonosState#getSelectedZoneName()
     */
    @Override
    public String getSelectedZoneName() {
        return selectedZone;
    }

    @Override
    public ZoneCommandDispatcher getZoneCommandDispatcher() {
        return zoneCommandDispatcher;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.web.ISonosState#getZonesData()
     */
    @Override
    public IndexedContainer getZonesData() {
        if (zonesData == null) {
            zonesData = new IndexedContainer();
            zonesData.addContainerProperty("Name", String.class, "");
        }
        return zonesData;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.web.ISonosState#loadMusicLibrary(org.tensin.sonos.control.ZonePlayer, java.lang.String)
     */
    @Override
    public void loadMusicLibrary(final ZonePlayer zone, final String name) {
        if (!indexed) {
            final MusicLibrary library = new MusicLibrary(zone, "A:TRACKS");
            libraries.put(name, library);
            indexed = true;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.web.ISonosState#sendCommand(org.tensin.sonos.commands.IZoneCommand)
     */
    @Override
    public void sendCommand(final IZoneCommand command) {
        if (StringUtils.isEmpty(getSelectedZoneName())) {
            LOGGER.error("Selected zone name not set");
        } else {
            final ZoneCommandExecutor executor = zoneCommandDispatcher.getZoneCommandExecutor(getSelectedZoneName());
            if (executor == null) {
                LOGGER.error("Can't find executor [" + getSelectedZoneName() + "] (not yed found on the network ?)");
            } else {
                executor.addCommand(command);
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.web.ISonosState#setPlaylistData(com.vaadin.data.util.IndexedContainer)
     */
    @Override
    public void setPlaylistData(final IndexedContainer playlistData) {
        this.playlistData = playlistData;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.web.ISonosState#setSelectedZone(java.lang.String)
     */
    @Override
    public void setSelectedZone(final String selectedZone) {
        this.selectedZone = selectedZone;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.web.ISonosState#setZonesData(com.vaadin.data.util.IndexedContainer)
     */
    @Override
    public void setZonesData(final IndexedContainer zonesData) {
        this.zonesData = zonesData;
    }
}
