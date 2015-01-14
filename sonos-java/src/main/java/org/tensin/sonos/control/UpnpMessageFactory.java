package org.tensin.sonos.control;

import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.Service;

/**
 * A factory for creating UpnpMessage objects.
 */
public class UpnpMessageFactory {

    /**
     * Gets the new instance.
     *
     * @return the new instance
     */
    public static UpnpMessageFactory getNewInstance() {
        UpnpMessageFactory result = new UpnpMessageFactory();
        return result;
    }

    /**
     * Instantiates a new upnp message factory.
     */
    private UpnpMessageFactory() {
        super();
    }

    /**
     * Gets the message.
     *
     * @param service the service
     * @param actionName the action name
     * @return the message
     */
    public SonosActionInvocation getMessage(final Service service, final String actionName) {
        final Action action = service.getAction(actionName);
        SonosActionInvocation setTargetInvocation = new SonosActionInvocation(action);
        return setTargetInvocation;
    }

}
