package org.tensin.sonos.upnp;

/**
 * The Interface Listener.
 */
public interface ISonosVolumeListener {

    /**
     * Found.
     * 
     * @param zoneName
     *            the zone name
     * @param currentVolume
     *            the current volume
     */
    public void volumeDone(final String zoneName, final int currentVolume);
}