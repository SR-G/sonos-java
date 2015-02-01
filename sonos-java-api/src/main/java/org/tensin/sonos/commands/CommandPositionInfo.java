package org.tensin.sonos.commands;

import org.tensin.sonos.SonosException;
import org.tensin.sonos.control.ZonePlayer;
import org.tensin.sonos.model.PositionInfo;

/**
 * The Class CommandGetXPort.
 */
public class CommandPositionInfo extends AbstractCommand implements IZoneCommand {

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.IZoneCommand#execute(org.tensin.sonos.ISonos)
     */
    @Override
    public void execute(final ZonePlayer sonos) throws SonosException {
        final PositionInfo result = sonos.getMediaRendererDevice().getAvTransportService().getPositionInfo();
        setResult(result);
//        final List<Entry> queueContent = sonos.getMediaServerDevice().getContentDirectoryService().getQueue(0, 999);
//        System.out.println(queueContent.get(0).toString());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.ICommand#getDescription()
     */
    @Override
    public String getDescription() {
        return "";
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.commands.ICommand#getName()
     */
    @Override
    public String getName() {
        return "getPositionInfo";
    }

}
