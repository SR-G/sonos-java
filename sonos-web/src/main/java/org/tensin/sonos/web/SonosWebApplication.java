package org.tensin.sonos.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensin.sonos.web.vaadin.HelpWindow;
import org.tensin.sonos.web.vaadin.PanelCommands;
import org.tensin.sonos.web.vaadin.PanelControls;
import org.tensin.sonos.web.vaadin.PanelNowPlaying;
import org.tensin.sonos.web.vaadin.PanelPlaylist;
import org.tensin.sonos.web.vaadin.PanelZones;

import com.vaadin.Application;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Window;

/**
 * The Class SonosApplication.
 */
public class SonosWebApplication extends Application implements ClickListener, ValueChangeListener, ItemClickListener {

    /** serialVersionUID. */
    private static final long serialVersionUID = 505039976568993474L;

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SonosWebApplication.class);

    /** The help window. */
    private HelpWindow helpWindow = null;

    /** The main layout. */
    private GridLayout mainLayout;

    /** The main window. */
    private Window mainWindow;

    /**
     * {@inheritDoc}
     * 
     * @see com.vaadin.ui.Button.ClickListener#buttonClick(com.vaadin.ui.Button.ClickEvent)
     */
    @Override
    public void buttonClick(final ClickEvent event) {
        // TODO Auto-generated method stub

    }

    /**
     * Gets the help window.
     * 
     * @return the help window
     */
    private HelpWindow getHelpWindow() {
        if (helpWindow == null) {
            helpWindow = new HelpWindow();
        }
        return helpWindow;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.vaadin.Application#init()
     */
    @Override
    public void init() {
        LOGGER.info("Starting sonos web application");
        initLayout();
        initPanels();
    }

    /**
     * Inits the layout.
     */
    private void initLayout() {
        // Create the main window.
        mainWindow = new Window("SonosWeb");
        setMainWindow(mainWindow);

        mainLayout = new GridLayout(3, 3);
        mainWindow.setContent(mainLayout);

        mainLayout.setSizeFull();
        mainLayout.setRowExpandRatio(0, 2);
        mainLayout.setRowExpandRatio(1, 2);
        mainLayout.setRowExpandRatio(2, 3);

        mainLayout.setColumnExpandRatio(0, 5);
        mainLayout.setColumnExpandRatio(1, 4);
        mainLayout.setColumnExpandRatio(2, 5);

        // rootLayout.setWidth("1024px");
        // rootLayout.setHeight("800px");
    }

    /**
     * Inits the panels.
     */
    private void initPanels() {
        final PanelNowPlaying panelNowPlaying = new PanelNowPlaying();
        final PanelZones panelZones = new PanelZones();
        final PanelControls panelControls = new PanelControls();
        final PanelPlaylist panelPlaylist = new PanelPlaylist();
        final PanelCommands panelCommands = new PanelCommands();

        panelZones.init(panelPlaylist, panelNowPlaying);
        panelPlaylist.init(panelNowPlaying);
        panelNowPlaying.init();
        panelCommands.init();

        mainLayout.addComponent(panelNowPlaying, 0, 0, 0, 2);
        mainLayout.addComponent(panelZones, 1, 0, 1, 0);
        mainLayout.addComponent(panelControls, 1, 1, 1, 1);
        mainLayout.addComponent(panelPlaylist, 1, 2, 1, 2);
        mainLayout.addComponent(panelCommands, 2, 0, 2, 2);

        panelControls.addComponent(new Button("Help", new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                showHelpWindow();
            }
        }));

        // zonesList.setPageLength(Math.min(actualRowCount, myRowsPerPageLimit);

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
     * 
     * @param event
     *            the event
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

    /**
     * {@inheritDoc}
     * 
     * @see com.vaadin.event.ItemClickEvent.ItemClickListener#itemClick(com.vaadin.event.ItemClickEvent)
     */
    @Override
    public void itemClick(final ItemClickEvent event) {
        // TODO Auto-generated method stub

    }

    /**
     * Show help window.
     */
    private void showHelpWindow() {
        getMainWindow().addWindow(getHelpWindow());
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.vaadin.data.Property.ValueChangeListener#valueChange(com.vaadin.data.Property.ValueChangeEvent)
     */
    @Override
    public void valueChange(final ValueChangeEvent event) {
        // TODO Auto-generated method stub

    }
}
