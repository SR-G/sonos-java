package org.tensin.sonos.upnp;

public interface SonosListener {

    public void updateDone(String parent);

    public void updateItem(String parent, int index, SonosItem item);
}
