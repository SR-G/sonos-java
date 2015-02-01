package org.tensin.sonos.tts.renderer.impl;

import org.tensin.sonos.SonosException;
import org.tensin.sonos.commander.DaemonController;
import org.tensin.sonos.commander.ISonosController;

/**
 * The Class AbstractSpeechRenderer.
 */
public abstract class AbstractSpeechRenderer {

    /** The sonos controller. */
    private DaemonController sonosController = DaemonController.buldController();
    
    /**
     * Gets the sonos controller.
     *
     * @return the sonos controller
     */
    public ISonosController getSonosController() {
        return sonosController;
    }

    /**
     * Inits the.
     *
     * @throws SonosException the sonos exception
     */
    public void init() throws SonosException {
        sonosController.start();
    }
    
    /**
     * Shutdown.
     *
     * @throws SonosException the sonos exception
     */
    public void shutdown() throws SonosException {
        if (sonosController != null) {
            sonosController.stop();
        }
    }
}