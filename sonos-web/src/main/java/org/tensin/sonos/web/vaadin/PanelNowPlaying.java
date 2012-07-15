package org.tensin.sonos.web.vaadin;

import org.tensin.sonos.SonosConstants;
import org.tensin.sonos.commands.CommandList;
import org.tensin.sonos.upnp.ISonosBrowseListener;
import org.tensin.sonos.upnp.SonosItem;
import org.tensin.sonos.web.SonosState;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.Resource;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Panel;

/**
 * The Class PanelNowPlaying.
 */
public class PanelNowPlaying extends Panel {

    /**
     * The listener interface for receiving sonosPlaylist events.
     * The class that is interested in processing a sonosPlaylist
     * event implements this interface, and the object created
     * with that class is registered with a component using the
     * component's <code>addSonosPlaylistListener<code> method. When
     * the sonosPlaylist event occurs, that object's appropriate
     * method is invoked.
     * 
     * @see SonosPlaylistEvent
     */
    private final class SonosNowPlayingListener implements ISonosBrowseListener {

        /**
         * {@inheritDoc}
         * 
         * @see org.tensin.sonos.upnp.ISonosBrowseListener#updateDone(java.lang.String)
         */
        @Override
        public void updateDone(final String parent) {
            // playList.setContainerDataSource(playlistData);
            // playList.setPageLength(playlistData.size());
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.tensin.sonos.upnp.ISonosBrowseListener#updateItem(java.lang.String, int, org.tensin.sonos.upnp.SonosItem)
         */
        @Override
        public void updateItem(final String parent, final int index, final SonosItem item) {
            String url = "http://" + item.host + ":" + SonosConstants.SONOS_DEFAULT_RPC_PORT;
            if (!item.albumArtURI.toString().startsWith("/")) {
                url += "/";
            }
            url += item.albumArtURI.toString();

            System.out.println(url);

            Resource resource = new ExternalResource(url);
            image.setSource(resource);
            // SonosState.getInstance().get
        }
    }

    /** serialVersionUID. */
    private static final long serialVersionUID = -8755064618320070798L;

    /** The now playing listener. */
    private final SonosNowPlayingListener nowPlayingListener = new SonosNowPlayingListener();

    /** The image. */
    private Embedded image;

    /**
     * Instantiates a new panel now playing.
     */
    public PanelNowPlaying() {
        super("Now playing");
        setSizeFull();
    }

    /**
     * Fire event zone changed.
     */
    public void fireEventZoneChanged() {
        // playList.setContainerDataSource(null);
        // SonosState.getInstance().setPlaylistData(null);
        // playlistListener.setPlaylistData(SonosState.getInstance().getPlaylistData());

        CommandList command = new CommandList();
        command.setListener(nowPlayingListener);
        command.setArgs("AI:");

        SonosState.getInstance().sendCommand(command);
    }

    /**
     * Inits the.
     * 
     */
    public void init() {
        image = new Embedded("Artwork");
        image.setType(Embedded.TYPE_IMAGE);
        image.setImmediate(true);
        image.setWidth("100%");
        addComponent(image);
    }
}
