package org.tensin.sonos.commands;

import org.tensin.sonos.ISonos;
import org.tensin.sonos.SonosFactory;
import org.tensin.sonos.upnp.DiscoverFactory;
import org.tensin.sonos.upnp.IDiscover;
import org.tensin.sonos.upnp.SonosException;

/**
 * The Class CommandDiscover.
 */
public class CommandDiscover implements IStandardCommand {

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.IStandardCommand#execute()
     */
    @Override
    public void execute() throws SonosException {
        IDiscover d = DiscoverFactory.build();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException x) {
        }
        d.done();
        String[] list = d.getList();
        for (String element : list) {
            ISonos s = SonosFactory.build(element);
            String name = s.getZoneName();
            if (name != null) {
                System.out.println(element + " - " + name);
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.ICommand#getName()
     */
    @Override
    public String getName() {
        return "discover";
    }

}
