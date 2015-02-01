package org.tensin.sonos;

import org.tensin.sonos.model.Entry;

/**
 * The Interface ISonosIndexer.
 */
public interface ISonosIndexer {

    /**
     * Index.
     * 
     * @param entry
     *            the entry
     * @throws SonosException
     *             the sonos exception
     */
    void index(final Entry entry) throws SonosException;

    /**
     * Inits the.
     */
    void init();

    /**
     * Search.
     *
     * @return the collection
     */
    // Collection<Document> search(final String queryString) throws SonosException;

    /**
     * Shutdown.
     */
    void shutdown();
}