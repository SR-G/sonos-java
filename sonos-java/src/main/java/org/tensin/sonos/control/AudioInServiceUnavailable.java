package org.tensin.sonos.control;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Service;

/**
 * The Class UnavailableService.
 */
public class AudioInServiceUnavailable extends AbstractAudioInService {

    /**
     * Instantiates a new audio in service unavailable.
     * 
     * @param upnpService
     *            the upnp service
     * @param service
     *            the service
     */
    public AudioInServiceUnavailable(final UpnpService upnpService, final Service service) {
        super(upnpService, service, ZonePlayerConstants.SONOS_SERVICE_AUDIO_IN);
    }

    /**
     * Instantiates a new unavailable service.
     * 
     * @param upnpService
     *            the upnp service
     * @param service
     *            the service
     * @param type
     *            the type
     */
    public AudioInServiceUnavailable(final UpnpService upnpService, final Service service, final String type) {
        super(upnpService, service, type);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.control.AbstractService#dispose()
     */
    @Override
    public void dispose() {
        super.dispose();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.control.AbstractService#execute(org.teleal.cling.model.action.ActionInvocation)
     */
    @Override
    protected void execute(final ActionInvocation message) {
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.control.AbstractService#executeImmediate(org.teleal.cling.model.action.ActionInvocation)
     */
    @Override
    protected void executeImmediate(final ActionInvocation message) {
    }
}
