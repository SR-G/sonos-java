package org.tensin.sonos;

import org.tensin.sonos.upnp.SonosListener;

/**
 * The Class SonosMock.
 */
public class SonosMock implements ISonos {

    /** The host. */
    private final String host;

    /**
     * Instantiates a new sonos mock.
     * 
     * @param host
     *            the host
     */
    public SonosMock(final String host) {
        this.host = host;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#add(java.lang.String)
     */
    @Override
    public void add(final String uri) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#browse(java.lang.String, org.tensin.sonos.upnp.SonosListener)
     */
    @Override
    public void browse(final String _id, final SonosListener cb) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#destroy(java.lang.String)
     */
    @Override
    public void destroy(final String id) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#getPosition()
     */
    @Override
    public void getPosition() {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#getTransportURI()
     */
    @Override
    public String getTransportURI() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#getZoneName()
     */
    @Override
    public String getZoneName() {
        return host;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#move(int, int)
     */
    @Override
    public void move(final int from, final int to) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#next()
     */
    @Override
    public void next() {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#pause()
     */
    @Override
    public void pause() {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#play()
     */
    @Override
    public void play() {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#playmode(boolean, boolean)
     */
    @Override
    public void playmode(final boolean shuffle, final boolean repeat) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#prev()
     */
    @Override
    public void prev() {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#remove(java.lang.String)
     */
    @Override
    public void remove(final String id) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#removeAll()
     */
    @Override
    public void removeAll() {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#save(java.lang.String, java.lang.String)
     */
    @Override
    public void save(final String name, final String uri) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#seekTrack(int)
     */
    @Override
    public void seekTrack(final int nr) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#setMute(boolean)
     */
    @Override
    public void setMute(final boolean mute) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#setTransportURI(java.lang.String)
     */
    @Override
    public void setTransportURI(final String uri) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#stop()
     */
    @Override
    public void stop() {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#trace_browse(boolean)
     */
    @Override
    public void trace_browse(final boolean x) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#trace_io(boolean)
     */
    @Override
    public void trace_io(final boolean x) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#trace_reply(boolean)
     */
    @Override
    public void trace_reply(final boolean x) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#volume()
     */
    @Override
    public int volume() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#volume(int)
     */
    @Override
    public void volume(final int vol) {
        // TODO Auto-generated method stub

    }

}
