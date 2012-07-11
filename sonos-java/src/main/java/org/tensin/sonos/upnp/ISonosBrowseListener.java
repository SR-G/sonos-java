package org.tensin.sonos.upnp;

/**
 * The listener interface for receiving sonos events.
 * The class that is interested in processing a sonos
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addSonosListener<code> method. When
 * the sonos event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see SonosEvent
 */
public interface ISonosBrowseListener {

    /**
     * Update done.
     * 
     * @param parent
     *            the parent
     */
    public void updateDone(String parent);

    /**
     * Update item.
     * 
     * @param parent
     *            the parent
     * @param index
     *            the index
     * @param item
     *            the item
     */
    public void updateItem(String parent, int index, SonosItem item);
}
