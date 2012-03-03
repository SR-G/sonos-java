package org.tensin.sonos;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.tensin.sonos.upnp.SonosException;

/**
 * The Class CLITestCase.
 */
public class CLITestCase {

    @Before
    public void setUp() {
        SystemHelper mockedHelper = mock(SystemHelper.class);
        doNothing().when(mockedHelper).exit(0);
        app.setSystemHelper(mockedHelper);
    }

    /**
     * Test next.
     * 
     * @throws SonosException
     *             the sonos exception
     */
    @Test
    public void testDiscover() throws SonosException {
        app.main(new String[] { "--command", "discover" });
    }

    /**
     * Test next.
     * 
     * @throws SonosException
     *             the sonos exception
     */
    @Test
    public void testNext() throws SonosException {
        app.main(new String[] { "--command", "next", "--zone", "chambre" });
    }

    /**
     * Test cli.
     * 
     * @throws SonosException
     *             the sonos exception
     */
    @Test
    public void testPause() throws SonosException {
        app.main(new String[] { "--command", "pause", "--zone", "ALL" });
    }

    /**
     * Test cli.
     * 
     * @throws SonosException
     *             the sonos exception
     */
    @Test
    public void testPlay() throws SonosException {
        app.main(new String[] { "--command", "play", "--zone", "chambre" });
    }

    /**
     * Test cli.
     * 
     * @throws SonosException
     *             the sonos exception
     */
    @Test
    public void testUnknownCommand() throws SonosException {
        app.main(new String[] { "--zzz" });
    }

    /**
     * Test cli.
     * 
     * @throws SonosException
     *             the sonos exception
     */
    @Test
    public void testUnknownOption() throws SonosException {
        app.main(new String[] { "--command", "dis" });
    }

    /**
     * Test cli.
     * 
     * @throws SonosException
     *             the sonos exception
     */
    @Test
    public void testUsage() throws SonosException {
        app.main(new String[] { "--usage" });
    }

    /**
     * Test next.
     * 
     * @throws SonosException
     *             the sonos exception
     */
    @Test
    public void testVolume() throws SonosException {
        app.main(new String[] { "--command", "volume", "--zone", "chambre" });
    }

    /**
     * Test next.
     * 
     * @throws SonosException
     *             the sonos exception
     */
    @Test
    public void testVolumeDown() throws SonosException {
        app.main(new String[] { "--command", "down", "--zone", "chambre" });
    }

    /**
     * Test next.
     * 
     * @throws SonosException
     *             the sonos exception
     */
    @Test
    public void testVolumeMute() throws SonosException {
        app.main(new String[] { "--command", "mute", "--zone", "chambre" });
    }

    /**
     * Test next.
     * 
     * @throws SonosException
     *             the sonos exception
     */
    @Test
    public void testVolumeSet() throws SonosException {
        app.main(new String[] { "--command", "volume", "25", "--zone", "chambre" });
    }

    /**
     * Test next.
     * 
     * @throws SonosException
     *             the sonos exception
     */
    @Test
    public void testVolumeUnmute() throws SonosException {
        app.main(new String[] { "--command", "unmute", "--zone", "chambre" });
    }

    /**
     * Test next.
     * 
     * @throws SonosException
     *             the sonos exception
     */
    @Test
    public void testVolumeUp() throws SonosException {
        app.main(new String[] { "--command", "up", "--zone", "chambre" });
    }

}
