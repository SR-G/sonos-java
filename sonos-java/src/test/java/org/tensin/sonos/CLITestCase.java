package org.tensin.sonos;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tensin.sonos.commands.ZoneCommandDispatcher;
import org.tensin.sonos.commands.ZoneCommandExecutor;
import org.tensin.sonos.upnp.DiscoverFactory;
import org.tensin.sonos.upnp.DiscoverMock;
import org.tensin.sonos.upnp.SonosException;

import com.beust.jcommander.ParameterException;

/**
 * The Class CLITestCase.
 */
public class CLITestCase {

    /**
     * Sets the up.
     */
    @Before
    public void setUp() {
        // SystemHelper mockedHelper = mock(SystemHelper.class);
        // doNothing().when(mockedHelper);
        SystemHelper systemHelper = new SystemHelper();
        SystemHelper spy = spy(systemHelper);
        doNothing().when(spy).exit(0);
        SonosCommander.setSystemHelper(spy);

        SonosFactory.setiSonosClass(SonosMock.class);
        DiscoverFactory.setiDiscoverClass(DiscoverMock.class);
    }

    /**
     * Tear down.
     */
    @After
    public void tearDown() {
        ZoneCommandDispatcher.resetInstance();
    }

    /**
     * Test next.
     * 
     * @throws SonosException
     *             the sonos exception
     */
    @Test
    public void testDiscover() throws SonosException {
        SonosCommander.main(new String[] { "--command", "discover" });
    }

    /**
     * Test list.
     * (1) id: A: / Attributes
     * (2) id: S: / Music Shares
     * (3) id: Q: / Queues
     * (4) id: SQ: / Saved Queues
     * (5) id: R: / Internet Radio
     * (6) id: EN: / Entire Network
     * 
     * @throws SonosException
     *             the sonos exception
     */
    @Test
    public void testList() throws SonosException {
        SonosCommander.main(new String[] { "--command", "list", "A:", "--zone", "salon" });
    }

    /**
     * Test next.
     * 
     * @throws SonosException
     *             the sonos exception
     */
    @Test
    public void testMute() throws SonosException {
        SonosCommander.main(new String[] { "--command", "mute", "--zone", "salon" });

        ZoneCommandExecutor executor = ZoneCommandDispatcher.getInstance().getZoneCommandExecutor("SALON");
        Assert.assertTrue(executor != null);
        Assert.assertEquals(1, executor.getExecutedCommandsCount());

        executor = ZoneCommandDispatcher.getInstance().getZoneCommandExecutor("CHAMBRE");
        Assert.assertTrue(executor != null);
        Assert.assertEquals(0, executor.getExecutedCommandsCount());
    }

    /**
     * Test next.
     * 
     * @throws SonosException
     *             the sonos exception
     */
    @Test
    public void testNext() throws SonosException {
        SonosCommander.main(new String[] { "--command", "next", "--zone", "chambre" });
    }

    /**
     * Test cli.
     * 
     * @throws SonosException
     *             the sonos exception
     */
    @Test
    public void testPause() throws SonosException {
        SonosCommander.main(new String[] { "--command", "pause", "--zone", "ALL" });
    }

    /**
     * Test cli.
     * 
     * @throws SonosException
     *             the sonos exception
     */
    @Test
    public void testPlay() throws SonosException {
        SonosCommander.main(new String[] { "--command", "play", "--zone", "salon" });
    }

    /**
     * Test cli.
     * 
     * @throws SonosException
     *             the sonos exception
     */
    @Test
    public void testUnknownCommand() throws SonosException {
        try {
            SonosCommander.main(new String[] { "--zzz" });
            Assert.fail("Expected exception not thrown");
        } catch (ParameterException e) {
        }
    }

    /**
     * Test cli.
     * 
     * @throws SonosException
     *             the sonos exception
     */
    @Test
    public void testUnknownOption() throws SonosException {
        SonosCommander.main(new String[] { "--command", "dis" });
    }

    /**
     * Test next.
     * 
     * @throws SonosException
     *             the sonos exception
     */
    @Test
    public void testUnknownZone() throws SonosException {
        SonosCommander.main(new String[] { "--command", "mute", "--zone", "zzzzzzzzzz" });
    }

    /**
     * Test next.
     * 
     * @throws SonosException
     *             the sonos exception
     */
    @Test
    public void testUnmute() throws SonosException {
        SonosCommander.main(new String[] { "--command", "unmute", "--zone", "salon" });
    }

    /**
     * Test cli.
     * 
     * @throws SonosException
     *             the sonos exception
     */
    @Test
    public void testUsage() throws SonosException {
        SonosCommander.main(new String[] { "--usage" });
    }

    /**
     * Test next.
     * 
     * @throws SonosException
     *             the sonos exception
     */
    @Test
    public void testVolume() throws SonosException {
        SonosCommander.main(new String[] { "--command", "volume", "--zone", "chambre" });
    }

    /**
     * Test next.
     * 
     * @throws SonosException
     *             the sonos exception
     */
    @Test
    public void testVolumeDown() throws SonosException {
        SonosCommander.main(new String[] { "--command", "down", "--zone", "chambre" });
    }

    /**
     * Test next.
     * 
     * @throws SonosException
     *             the sonos exception
     */
    @Test
    public void testVolumeSet() throws SonosException {
        SonosCommander.main(new String[] { "--command", "volume", "25", "--zone", "chambre" });
    }

    /**
     * Test next.
     * 
     * @throws SonosException
     *             the sonos exception
     */
    @Test
    public void testVolumeUp() throws SonosException {
        SonosCommander.main(new String[] { "--command", "up", "--zone", "chambre" });
    }

}
