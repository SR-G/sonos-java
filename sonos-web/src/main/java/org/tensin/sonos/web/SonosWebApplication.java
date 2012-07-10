package org.tensin.sonos.web;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensin.sonos.ISonos;
import org.tensin.sonos.SonosException;
import org.tensin.sonos.SonosFactory;
import org.tensin.sonos.commands.ZoneCommandDispatcher;
import org.tensin.sonos.upnp.DiscoverFactory;
import org.tensin.sonos.upnp.IDiscover;
import org.tensin.sonos.upnp.Listener;
import org.tensin.sonos.upnp.SonosItem;
import org.tensin.sonos.upnp.SonosListener;

import com.vaadin.Application;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Form;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

/**
 * The Class SonosApplication.
 */
public class SonosWebApplication extends Application implements ClickListener, ValueChangeListener, ItemClickListener {

    private static final class SonosPlaylistListener implements SonosListener {

        private IndexedContainer playlistData;

        public void setPlaylistData(final IndexedContainer playlistData) {
            this.playlistData = playlistData;
        }

        @Override
        public void updateDone(final String parent) {
            // TODO Auto-generated method stub

        }

        @Override
        public void updateItem(final String parent, final int index, final SonosItem item) {
            synchronized (playlistData) {
                Object id = playlistData.addItem();
                playlistData.getContainerProperty(id, "Title").setValue(item.title);
                System.out.println(">>>>>>>>>>>>>>>" + item.title);
                /*
                 * sonos_mkcontainer("", "object.container", "Artists", "A:ARTIST");
                 * sonos_mkcontainer("", "object.container", "Albums", "A:ALBUM");
                 * sonos_mkcontainer("", "object.container", "Genres", "A:GENRE");
                 * sonos_mkcontainer("", "object.container", "Composers", "A:COMPOSER");
                 * sonos_mkcontainer("", "object.container", "Imported Playlists", "A:PLAYLISTS");
                 * sonos_mkcontainer("", "object.container", "Folders", "S:");
                 * sonos_mkcontainer("", "object.container", "Radio", "R:");
                 * sonos_mkcontainer("", "object.container", "Line In", "AI:");
                 * sonos_mkcontainer("", "object.container", "Playlists", "SQ:");
                 */

            }
        }

    }

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
         * @see org.tensin.sonos.upnp.Listener#found(java.lang.String)
         */
        @Override
        public void found(final String host) {
            try {
                ISonos sonos = SonosFactory.build(host);
                sonos.refreshZoneAttributes();
                String name = sonos.getZoneName();

                if (StringUtils.isNotEmpty(name)) {
                    LOGGER.info("New zone found [" + name + "]");
                    ZoneCommandDispatcher.getInstance().registerZoneAsAvailable(sonos, name);
                    synchronized (zonesData) {
                        Object id = zonesData.addItem();
                        zonesData.getContainerProperty(id, "Name").setValue(name);
                    }
                }
            } catch (SonosException e) {
                LOGGER.error("Internal error while working on new found host [" + host + "]", e);
            }
        }
    }

    /** selectedSonos */
    private ISonos selectedSonos;

    /** The discover. */
    private IDiscover discover;

    /** serialVersionUID. */
    private static final long serialVersionUID = 505039976568993474L;

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SonosWebApplication.class);

    /** The contact list. */
    private final Table zonesList = new Table();

    /** The contact editor. */
    private final Form contactEditor = new Form();

    /** The bottom left corner. */
    private final HorizontalLayout bottomLeftCorner = new HorizontalLayout();

    /** The contact removal button. */
    private Button contactRemovalButton;

    /** The address book data. */
    private IndexedContainer zonesData;

    /** The playlist data. */
    private IndexedContainer playlistData;

    private final Table playList = new Table();

    private final SonosPlaylistListener playlistListener = new SonosPlaylistListener();

    @Override
    public void buttonClick(final ClickEvent event) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see com.vaadin.Application#init()
     */
    @Override
    public void init() {
        LOGGER.info("Starting sonos web application");

        try {
            initSonosCommander();
            initLayout();
            // initContactAddRemoveButtons();
            // initAddressList();
            // initFilteringControls();
        } catch (SonosException e) {
            LOGGER.error("Error while starting Sonos Web application", e);
        }
    }

    /**
     * Inits the layout.
     */
    private void initLayout() {
        // Create the main window.
        final Window main = new Window("SonosWeb");
        setMainWindow(main);

        final GridLayout rootLayout = new GridLayout(3, 3);
        main.setContent(rootLayout);

        rootLayout.setSizeFull();
        // rootLayout.setWidth("1024px");
        // rootLayout.setHeight("800px");

        Panel panelNowPlaying = new Panel("Now playing");
        Panel panelZones = new Panel("Zones");
        Panel panelControls = new Panel("Controls");
        Panel panelPlaylist = new Panel("Playlist");
        Panel panelCommands = new Panel("Commands");

        panelNowPlaying.setSizeFull();
        panelZones.setSizeFull();
        panelControls.setSizeFull();
        panelPlaylist.setSizeFull();
        panelCommands.setSizeFull();

        rootLayout.addComponent(panelNowPlaying, 0, 0, 0, 2);
        rootLayout.addComponent(panelZones, 1, 0, 1, 0);
        rootLayout.addComponent(panelControls, 1, 1, 1, 1);
        rootLayout.addComponent(panelPlaylist, 1, 2, 1, 2);
        rootLayout.addComponent(panelCommands, 2, 0, 2, 2);

        zonesList.setSizeFull();
        panelZones.addComponent(zonesList);
        initZonesList();

        playList.setSizeFull();
        panelPlaylist.addComponent(playList);

        Embedded image = new Embedded("Artwork");
        main.addComponent(image);
        panelNowPlaying.addComponent(image);

        panelControls.addComponent(new Button("STOP", new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                // Add new contact "John Doe" as the first item
                // Object id = ((IndexedContainer) contactList.getContainerDataSource()).addItemAt(0);
                selectedSonos.stop();
            }
        }));
        panelControls.addComponent(new Button("START", new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                // Add new contact "John Doe" as the first item
                // Object id = ((IndexedContainer) contactList.getContainerDataSource()).addItemAt(0);
                selectedSonos.play();
            }
        }));
        panelControls.addComponent(new Button("+5", new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                // Add new contact "John Doe" as the first item
                // Object id = ((IndexedContainer) contactList.getContainerDataSource()).addItemAt(0);
                int volume = selectedSonos.volume();
                selectedSonos.volume(volume + 5);
            }
        }));
        panelControls.addComponent(new Button("-5", new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                // Add new contact "John Doe" as the first item
                // Object id = ((IndexedContainer) contactList.getContainerDataSource()).addItemAt(0);
                int volume = selectedSonos.volume();
                selectedSonos.volume(volume - 5);
            }
        }));
        panelControls.addComponent(new Button("NEXT", new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                // Add new contact "John Doe" as the first item
                // Object id = ((IndexedContainer) contactList.getContainerDataSource()).addItemAt(0);
                selectedSonos.next();
            }
        }));
        panelControls.addComponent(new Button("PREV", new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                // Add new contact "John Doe" as the first item
                // Object id = ((IndexedContainer) contactList.getContainerDataSource()).addItemAt(0);
                selectedSonos.prev();
            }
        }));

        // HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
        // setMainWindow(new Window("Sonos", splitPanel));
        // VerticalLayout left = new VerticalLayout();
        // left.setSizeFull();
        // left.addComponent(contactList);
        // contactList.setSizeFull();
        // left.setExpandRatio(contactList, 1);
        // splitPanel.addComponent(left);
        // splitPanel.addComponent(contactEditor);
        // contactEditor.setCaption("Contact details editor");
        // contactEditor.setSizeFull();
        // contactEditor.getLayout().setMargin(true);
        // contactEditor.setImmediate(true);
        // bottomLeftCorner.setWidth("100%");
        // left.addComponent(bottomLeftCorner);
    }

    /**
     * Inits the contact add remove buttons.
     */
    // private void initContactAddRemoveButtons() {
    // // New item button
    // bottomLeftCorner.addComponent(new Button("+", new Button.ClickListener() {
    // @Override
    // public void buttonClick(final ClickEvent event) {
    // // Add new contact "John Doe" as the first item
    // Object id = ((IndexedContainer) contactList.getContainerDataSource()).addItemAt(0);
    // contactList.getItem(id).getItemProperty("First Name").setValue("John");
    // contactList.getItem(id).getItemProperty("Last Name").setValue("Doe");
    //
    // // Select the newly added item and scroll to the item
    // contactList.setValue(id);
    // contactList.setCurrentPageFirstItemId(id);
    // }
    // }));
    //
    // // Remove item button
    // contactRemovalButton = new Button("-", new Button.ClickListener() {
    // @Override
    // public void buttonClick(final ClickEvent event) {
    // contactList.removeItem(contactList.getValue());
    // contactList.select(null);
    // }
    // });
    // contactRemovalButton.setVisible(false);
    // bottomLeftCorner.addComponent(contactRemovalButton);
    // }
    //
    /**
     * Inits the filtering controls.
     */
    // private void initFilteringControls() {
    // for (final String pn : visibleCols) {
    // final TextField sf = new TextField();
    // bottomLeftCorner.addComponent(sf);
    // sf.setWidth("100%");
    // sf.setInputPrompt(pn);
    // sf.setImmediate(true);
    // bottomLeftCorner.setExpandRatio(sf, 1);
    // sf.addListener(new Property.ValueChangeListener() {
    // @Override
    // public void valueChange(final ValueChangeEvent event) {
    // addressBookData.removeContainerFilters(pn);
    // if ((sf.toString().length() > 0) && !pn.equals(sf.toString())) {
    // addressBookData.addContainerFilter(pn, sf.toString(), true, false);
    // }
    // getMainWindow().showNotification("" + addressBookData.size() + " matches found");
    // }
    // });
    // }
    // }

    private void initSonosCommander() throws SonosException {
        zonesData = new IndexedContainer();
        zonesData.addContainerProperty("Name", String.class, "");

        final ZonesDiscoveredListener zonesDiscoveredListener = new ZonesDiscoveredListener();
        discover = DiscoverFactory.build(zonesDiscoveredListener);
        discover.launch();
    }

    /**
     * Inits the address list.
     */
    private void initZonesList() {
        zonesList.setContainerDataSource(zonesData);
        zonesList.setVisibleColumns(new String[] { "Name" });
        zonesList.setSelectable(true);
        zonesList.setImmediate(true);
        zonesList.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(final ValueChangeEvent event) {
                Object id = zonesList.getValue();
                if (id != null) {
                    synchronized (zonesData) {
                        Item s = zonesData.getItem(id);
                        String zoneName = s.toString();
                        LOGGER.info("Zone selected [" + id + "], zone [" + zoneName + "]");

                        ISonos sonos = ZoneCommandDispatcher.getInstance().getZoneCommandExecutor(zoneName).getSonosZone();
                        if (sonos == null) {
                            LOGGER.error("Can't find sonos box [" + zoneName + "]");
                        } else {
                            selectedSonos = sonos;
                            playlistData = new IndexedContainer();
                            playlistData.addContainerProperty("Title", String.class, "");
                            playlistListener.setPlaylistData(playlistData);
                            selectedSonos.browse("Q:0", playlistListener);

                            // selectedSonos.browseMetadata("Q:0", playlistListener);

                            playList.setContainerDataSource(playlistData);
                            // sonos.browse(_id, cb);
                        }

                        // ICommand command = CommandFactory.createCommand("PLAY");
                        // ZoneCommandDispatcher.getInstance().dispatchCommand((IZoneCommand) command, s.toString());
                    }
                }

                // contactEditor.setItemDataSource(id == null ? null : zonesList.getItem(id));
                // contactRemovalButton.setVisible(id != null);
            }
        });
    }

    @Override
    public void itemClick(final ItemClickEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    public void valueChange(final ValueChangeEvent event) {
        // TODO Auto-generated method stub

    }
}
