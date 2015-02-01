package org.tensin.sonos.control;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.Service;

/**
 * The Class AbstractAudioInService.
 */
public abstract class AbstractAudioInService extends AbstractService {

    /**
     * Builds the audio in service.
     * 
     * @param upnpService
     *            the upnp service
     * @param dev
     *            the dev
     * @return the audio in service
     */
    public static AbstractAudioInService buildAudioInService(final UpnpService upnpService, final RemoteDevice dev) {
        final Service audioService = AbstractService.findService(dev, ZonePlayerConstants.SONOS_SERVICE_AUDIO_IN);
        if (audioService != null) {
            return new AudioInService(upnpService, audioService);
        } else {
            return new AudioInServiceUnavailable(upnpService, audioService);
        }
    }

    /**
     * Instantiates a new abstract audio in service.
     * 
     * @param upnpService
     *            the upnp service
     * @param service
     *            the service
     * @param type
     *            the type
     */
    public AbstractAudioInService(final UpnpService upnpService, final Service service, final String type) {
        super(upnpService, service, type);
    }

}
