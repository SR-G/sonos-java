package org.tensin.sonos.commands;

/**
 * The Interface ICommand.
 */
public interface ICommand {

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription();

    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName();

    /**
     * Need args.
     * 
     * @return true, if successful
     */
    public boolean needArgs();

}
