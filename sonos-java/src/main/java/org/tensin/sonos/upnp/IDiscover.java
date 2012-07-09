package org.tensin.sonos.upnp;

/**
 * The Interface IDiscover.
 */
public interface IDiscover {

    /**
     * Done.
     */
    public void done();

    /**
     * Gets the list.
     * 
     * @return the list
     */
    public String[] getList();

    /**
     * Launch.
     */
    public void launch();

    /**
     * Sets the ssdp control port.
     * 
     * @param ssdpControlPort
     *            the new ssdp control port
     */
    public void setSsdpControlPort(final int ssdpControlPort);

}