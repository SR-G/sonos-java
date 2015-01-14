package org.tensin.sonos.helpers;

import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.ServiceType;
import org.tensin.sonos.SonosConstants;

/**
 * The Class RemoteDeviceHelper.
 */
public class RemoteDeviceHelper {

    /**
     * Dump remote device.
     * 
     * @param device
     *            the device
     * @return the string
     */
    public static String dumpRemoteDevice(final RemoteDevice device) {
        final StringBuilder sb = new StringBuilder();
        sb.append(device.toString()).append(SonosConstants.NEWLINE);

        sb.append("  Details :").append(SonosConstants.NEWLINE);
        sb.append("    Name [").append(device.getDetails().getFriendlyName()).append("]").append(SonosConstants.NEWLINE);
        sb.append("    Serial Number [").append(device.getDetails().getSerialNumber()).append("]").append(SonosConstants.NEWLINE);
        sb.append("    UPC [").append(device.getDetails().getUpc()).append("]").append(SonosConstants.NEWLINE);
        sb.append("    Base URL [").append(device.getDetails().getBaseURL()).append("]").append(SonosConstants.NEWLINE);
        sb.append("    URI [").append(device.getDetails().getPresentationURI()).append("]").append(SonosConstants.NEWLINE);
        sb.append("    Manufacturer :").append(SonosConstants.NEWLINE);
        sb.append("      Manufacturer [").append(device.getDetails().getManufacturerDetails().getManufacturer()).append("]").append(SonosConstants.NEWLINE);
        sb.append("      Manufacturer URI [").append(device.getDetails().getManufacturerDetails().getManufacturerURI()).append("]")
                .append(SonosConstants.NEWLINE);
        sb.append("    Model :").append(SonosConstants.NEWLINE);
        sb.append("      Name [").append(device.getDetails().getModelDetails().getModelName()).append("]").append(SonosConstants.NEWLINE);
        sb.append("      Description [").append(device.getDetails().getModelDetails().getModelDescription()).append("]").append(SonosConstants.NEWLINE);
        sb.append("      Model Number [").append(device.getDetails().getModelDetails().getModelNumber()).append("]").append(SonosConstants.NEWLINE);
        sb.append("      Model URI [").append(device.getDetails().getModelDetails().getModelURI()).append("]").append(SonosConstants.NEWLINE);

        sb.append("  Identity :").append(SonosConstants.NEWLINE);
        sb.append("    UDN [").append(device.getIdentity().getUdn()).append("]").append(SonosConstants.NEWLINE);
        sb.append("    Descriptor URL [").append(device.getIdentity().getDescriptorURL()).append("]").append(SonosConstants.NEWLINE);
        sb.append("    Discovered on local address [").append(device.getIdentity().getDiscoveredOnLocalAddress()).append("]").append(SonosConstants.NEWLINE);
        sb.append("    Interface MAC addresss [").append(device.getIdentity().getInterfaceMacAddress()).append("]").append(SonosConstants.NEWLINE);
        sb.append("    Max age (seconds) [").append(device.getIdentity().getMaxAgeSeconds()).append("]").append(SonosConstants.NEWLINE);
        sb.append("    WoL bytes [").append(device.getIdentity().getWakeOnLANBytes()).append("]").append(SonosConstants.NEWLINE);

        sb.append("  Identity [").append(device.getVersion().toString()).append("]").append(SonosConstants.NEWLINE);

        sb.append("  Embedded devices :").append(SonosConstants.NEWLINE);
        for (final RemoteDevice remoteDevice : device.findEmbeddedDevices()) {
            sb.append("    - ").append(remoteDevice.toString()).append(SonosConstants.NEWLINE);
        }

        sb.append("  Services :").append(SonosConstants.NEWLINE);
        for (final Service service : device.findServices()) {
            // sb.append("    - ").append(service.toString()).append(SonosConstants.NEWLINE);
            sb.append("    - ").append(service.getServiceId()).append(SonosConstants.NEWLINE);
            sb.append("        Actions :").append(SonosConstants.NEWLINE);
            for (final Action action : service.getActions()) {
                sb.append("          - ").append(action.getName()).append(" (").append(CollectionHelper.singleDump(action.getArguments())).append(")")
                        .append(SonosConstants.NEWLINE);
            }
        }

        sb.append("  Services types:").append(SonosConstants.NEWLINE);
        for (final ServiceType serviceType : device.findServiceTypes()) {
            sb.append("    - ").append(serviceType.toString()).append(SonosConstants.NEWLINE);
        }

        return sb.toString();
    }

}
