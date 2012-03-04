package org.tensin.sonos.upnp;

/**
 * The Interface IDiscover.
 */
public interface IDiscover {

    /**
     * Done.
     */
    public abstract void done();

    /**
     * Gets the list.
     * 
     * @return the list
     */
    public abstract String[] getList();

    /**
     * Launch.
     */
    public abstract void launch();

}