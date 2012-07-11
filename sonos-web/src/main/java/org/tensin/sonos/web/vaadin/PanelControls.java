package org.tensin.sonos.web.vaadin;

import org.tensin.sonos.commands.CommandMuteOff;
import org.tensin.sonos.commands.CommandMuteOn;
import org.tensin.sonos.commands.CommandNext;
import org.tensin.sonos.commands.CommandPlay;
import org.tensin.sonos.commands.CommandPrev;
import org.tensin.sonos.commands.CommandStop;
import org.tensin.sonos.commands.CommandVolume;
import org.tensin.sonos.upnp.ISonosVolumeListener;
import org.tensin.sonos.web.SonosState;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Panel;

/**
 * The Class PanelControls.
 */
public class PanelControls extends Panel {

    /** serialVersionUID. */
    private static final long serialVersionUID = -8755064618320070798L;

    /**
     * Instantiates a new panel controls.
     */
    public PanelControls() {
        super("Controls");
        setSizeFull();
        initPanels();
    }

    /**
     * Inits the panels.
     */
    @SuppressWarnings("serial")
    private void initPanels() {
        addComponent(new Button("STOP", new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                SonosState.getInstance().sendCommand(new CommandStop());
            }
        }));
        addComponent(new Button("START", new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                SonosState.getInstance().sendCommand(new CommandPlay());
            }
        }));
        addComponent(new Button("+5", new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                final CommandVolume commandVolume = new CommandVolume();
                commandVolume.setListener(new ISonosVolumeListener() {

                    @Override
                    public void volumeDone(final String zoneName, final int currentVolume) {
                        final CommandVolume newVolume = new CommandVolume();
                        newVolume.setArgs(String.valueOf(currentVolume + 5));
                        SonosState.getInstance().sendCommand(newVolume);
                    }

                });
                SonosState.getInstance().sendCommand(commandVolume);
            }
        }));
        addComponent(new Button("-5", new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                final CommandVolume commandVolume = new CommandVolume();
                commandVolume.setListener(new ISonosVolumeListener() {

                    @Override
                    public void volumeDone(final String zoneName, final int currentVolume) {
                        final CommandVolume newVolume = new CommandVolume();
                        newVolume.setArgs(String.valueOf(currentVolume - 5));
                        SonosState.getInstance().sendCommand(newVolume);
                    }

                });
                SonosState.getInstance().sendCommand(commandVolume);
            }
        }));
        addComponent(new Button("NEXT", new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                SonosState.getInstance().sendCommand(new CommandNext());
            }
        }));
        addComponent(new Button("PREV", new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                SonosState.getInstance().sendCommand(new CommandPrev());
            }
        }));
        addComponent(new Button("MUTE ON", new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                SonosState.getInstance().sendCommand(new CommandMuteOn());
            }
        }));
        addComponent(new Button("MUTE OFF", new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                SonosState.getInstance().sendCommand(new CommandMuteOff());
            }
        }));
    }
}
