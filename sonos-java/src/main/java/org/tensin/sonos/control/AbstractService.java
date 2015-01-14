/*
 * Copyright 2007 David Wheeler
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tensin.sonos.control;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.action.ActionArgumentValue;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UDAServiceId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract class that wraps a UpnpService. Intended to be subclassed to have
 * functionality added.
 * 
 * @author David WHEELER
 * @author Serge SIMON
 * 
 */
public abstract class AbstractService {

    // private class EventingRefreshTask extends TimerTask {
    // private final ThreadChangingEventHandlerWrapper handler;
    //
    // protected EventingRefreshTask(final ThreadChangingEventHandlerWrapper handler) {
    // this.handler = handler;
    // }
    //
    // @Override
    // public void run() {
    // try {
    // refreshServiceEventing(DEFAULT_EVENT_PERIOD, handler);
    // } catch (IOException e) {
    // LOGGER.warn("Could not refresh eventing: ", e);
    // }
    // }
    // }

    /**
     * A ServiceEventHandler that adapts the event to occur in the SonosController thread.
     * 
     * @author David WHEELER
     * @author Serge SIMON
     * @author Serge SIMON
     * 
     */
    // private static final class ThreadChangingEventHandlerWrapper implements ServiceEventHandler {
    //
    // private final ServiceEventHandler handler;
    //
    // public ThreadChangingEventHandlerWrapper(final ServiceEventHandler handler) {
    // this.handler = handler;
    // }
    //
    // public ServiceEventHandler getWrappedHandler() {
    // return handler;
    // }
    //
    // public void handleStateVariableEvent(final String varName, final String newValue) {
    // SonosController.getInstance().getControllerExecutor().execute(new Runnable() {
    // @Override
    // public void run() {
    // handler.handleStateVariableEvent(varName, newValue);
    // }
    // });
    // }
    // }
    //
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractService.class);

    /**
     * The default length of time in seconds to register events for.
     */
    protected static final int DEFAULT_EVENT_PERIOD = 3600; // 1 hour

    /**
     * Find service.
     * 
     * @param device
     *            the device
     * @param serviceName
     *            the service name
     * @return the service
     */
    public static Service findService(final RemoteDevice device, final String serviceName) {
        UDAServiceId uadServiceId = UDAServiceId.valueOf(serviceName.replace(":1", ""));
        return device.findService(uadServiceId);
    }

    /** The service. */
    protected final Service service;

    /** The upnp service. */
    protected UpnpService upnpService = null;

    // private static final Timer timer = new Timer("ServiceEventRefresher", true);

    // private final List<EventingRefreshTask> tasks = new ArrayList<EventingRefreshTask>();

    /** The message factory. */
    protected final UpnpMessageFactory messageFactory;

    /**
     * Instantiates a new abstract service.
     * 
     * @param upnpService
     *            the upnp service
     * @param service
     *            the service
     * @param type
     *            the type
     */
    public AbstractService(final UpnpService upnpService, final Service service, final String type) {
        if (!service.getServiceType().toString().equals(type)) {
            throw new IllegalArgumentException("service must be " + type + ", not " + service.getServiceType());
        }

        this.upnpService = upnpService;
        this.service = service;
        messageFactory = UpnpMessageFactory.getNewInstance();
    }

    /**
     * Dispose.
     */
    public void dispose() {
        // LOGGER.info("Unregistering event listeners for " + getClass());
        // List<EventingRefreshTask> tasksCopy = new ArrayList<EventingRefreshTask>(tasks);
        // for (EventingRefreshTask task : tasksCopy) {
        // unregisterServiceEventing(task.handler);
        // }
    }

    /**
     * Execute.
     * 
     * @param message
     *            the message
     */
    protected void execute(final ActionInvocation message) {

        ActionCallback callback = new ActionCallback(message) {

            @Override
            public void failure(final ActionInvocation invocation, final UpnpResponse operation, final String defaultMsg) {
                LOGGER.error(defaultMsg);
            }

            @Override
            public void success(final ActionInvocation invocation) {
                ActionArgumentValue[] output = invocation.getOutput();
                // assertEquals(output.length, 0);
            }
        };

        getUpnpService().getControlPoint().execute(callback);

    }

    /**
     * Execute immediate.
     * 
     * @param message
     *            the message
     */
    protected void executeImmediate(final ActionInvocation message) {
        new ActionCallback.Default(message, getUpnpService().getControlPoint()).run();
    }

    /**
     * Gets the service.
     * 
     * @return the UpnpService wrapped by this class.
     */
    public Service getService() {
        return service;
    }

    /**
     * Gets the upnp service.
     * 
     * @return the UpnpService wrapped by this class.
     */
    public UpnpService getUpnpService() {
        return upnpService;
    }

    /**
     * Adds or refreshes the registration of service event notifications to the
     * given handler
     * 
     * @param duration
     *            the requested length of time for event notifications
     * @param handler
     *            the object that handles the notifications
     * @return the length of time that the handler has been registered for.
     * @
     *             if the device could not be contacted for some reason.
     */
    // private int refreshServiceEventing(final int duration, final ServiceEventHandler handler)  {
    // ServicesEventing eventing = ServicesEventing.getInstance();
    // int i = eventing.register(service, handler, duration);
    // LOGGER.info("Registered " + getClass() + " for eventing for " + i + "s");
    // return i;
    // }

    /**
     * Registers this service as a ServiceEventListener on its UpnpService. This
     * should be cleaned up by calling unregisterServiceEventing()
     * 
     * @param handler
     * @return true if the registration process completed successfully. if false
     *         is returned, registration will still be attempted periodically
     *         until unregisterServiceEventing() is called.
     */
    // protected boolean registerServiceEventing(final ServiceEventHandler handler) {
    // ThreadChangingEventHandlerWrapper handlerWrapper = new ThreadChangingEventHandlerWrapper(handler);
    // try {
    // refreshServiceEventing(DEFAULT_EVENT_PERIOD, handlerWrapper);
    // EventingRefreshTask task = new EventingRefreshTask(handlerWrapper);
    // tasks.add(task);
    // // schedule to refresh 1 minute less than the event period (in milis, not seconds)
    // timer.schedule(task, (DEFAULT_EVENT_PERIOD - 60) * 1000, (DEFAULT_EVENT_PERIOD - 60) * 1000);
    // return true;
    // } catch (IOException e) {
    // LOGGER.warn("Could not register service eventing: ", e);
    // return false;
    // }
    // }

    /**
     * Removes the given handler from Service Event registration. It will recieve
     * no further events.
     * 
     * @param handler
     * @
     */
    // protected void unregisterServiceEventing(final ServiceEventHandler handler) {
    // ServicesEventing eventing = ServicesEventing.getInstance();
    // for (ListIterator<EventingRefreshTask> i = tasks.listIterator(); i.hasNext();) {
    // EventingRefreshTask task = i.next();
    // if (task.handler.getWrappedHandler() == handler) {
    // task.cancel();
    // i.remove();
    // try {
    // eventing.unRegister(service, task.handler);
    // } catch (IOException e) {
    // LOGGER.error("Could not unregister eventing from " + service, e);
    // }
    // }
    // }
    // }
}
