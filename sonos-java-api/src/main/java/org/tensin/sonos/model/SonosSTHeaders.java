package org.tensin.sonos.model;


import org.fourthline.cling.model.message.header.InvalidHeaderException;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.types.NotificationSubtype;
import org.tensin.sonos.control.ZonePlayerConstants;

/**
 * The Class SonosSTHeaders.
 */
public class SonosSTHeaders extends UpnpHeader<NotificationSubtype> {

    /** {@inheritDoc}
     * @see org.teleal.cling.model.message.header.UpnpHeader#getString()
     */
    @Override
    public String getString() {
        return ZonePlayerConstants.SONOS_SERVICE_AV_TRANSPORT;
    }

    // urn:schemas-upnp-org:service:AVTransport:1

    /** {@inheritDoc}
     * @see org.teleal.cling.model.message.header.UpnpHeader#setString(java.lang.String)
     */
    @Override
    public void setString(final String s) throws InvalidHeaderException {
    }
}
