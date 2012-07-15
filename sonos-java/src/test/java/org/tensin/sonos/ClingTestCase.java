package org.tensin.sonos;

import org.junit.Test;
import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.model.message.header.STAllHeader;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.meta.RemoteService;
import org.teleal.cling.registry.Registry;
import org.teleal.cling.registry.RegistryListener;

/**
 * The Class ClingTestCase.
 */
public class ClingTestCase {

    /**
     * Test discovery.
     * 
     * @param args
     *            the args
     * @throws Exception
     *             the exception
     */
    @Test
    public void testDiscovery() throws Exception {

        // UPnP discovery is asynchronous, we need a callback
        RegistryListener listener = new RegistryListener() {

            @Override
            public void afterShutdown() {
                System.out.println("Shutdown of registry complete!");

            }

            @Override
            public void beforeShutdown(final Registry registry) {
                System.out.println("Before shutdown, the registry has devices: " + registry.getDevices().size());
            }

            @Override
            public void localDeviceAdded(final Registry registry, final LocalDevice device) {
                System.out.println("Local device added: " + device.getDisplayString());
            }

            @Override
            public void localDeviceRemoved(final Registry registry, final LocalDevice device) {
                System.out.println("Local device removed: " + device.getDisplayString());
            }

            @Override
            public void remoteDeviceAdded(final Registry registry, final RemoteDevice device) {
                System.out.println("Remote device available: " + device.getDisplayString());
            }

            @Override
            public void remoteDeviceDiscoveryFailed(final Registry registry, final RemoteDevice device, final Exception ex) {
                System.out.println("Discovery failed: " + device.getDisplayString() + " => " + ex);
            }

            @Override
            public void remoteDeviceDiscoveryStarted(final Registry registry, final RemoteDevice device) {
                for (RemoteService s : device.findServices()) {
                    System.out.println(s.toString());
                }
                System.out.println("Discovery started: " + device.getDisplayString() + " " + device.toString());
            }

            @Override
            public void remoteDeviceRemoved(final Registry registry, final RemoteDevice device) {
                System.out.println("Remote device removed: " + device.getDisplayString());
            }

            @Override
            public void remoteDeviceUpdated(final Registry registry, final RemoteDevice device) {
                System.out.println("Remote device updated: " + device.getDisplayString());
            }
        };

        // This will create necessary network resources for UPnP right away
        System.out.println("Starting Cling...");
        UpnpService upnpService = new UpnpServiceImpl(listener);

        // Send a search message to all devices and services, they should respond soon
        upnpService.getControlPoint().search(new STAllHeader());

        // Let's wait 10 seconds for them to respond
        System.out.println("Waiting 10 seconds before shutting down...");
        Thread.sleep(10000);

        // Release all resources and advertise BYEBYE to other UPnP devices
        System.out.println("Stopping Cling...");
        upnpService.shutdown();
    }

}
