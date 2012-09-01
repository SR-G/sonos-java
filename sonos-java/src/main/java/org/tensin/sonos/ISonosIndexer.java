package org.tensin.sonos;

import java.util.Collection;

import org.apache.lucene.document.Document;
import org.tensin.sonos.model.Entry;

public interface ISonosIndexer {

    /**
     * Index.
     * 
     * @param entry
     *            the entry
     * @throws SonosException
     *             the sonos exception
     */
    public abstract void index(final Entry entry) throws SonosException;

    /**
     * Inits the.
     */
    public abstract void init();

    /**
     * Search.
     * 
     * @param queryString
     *            the query string
     * @return the collection
     * @throws SonosException
     *             the sonos exception
     */
    public abstract Collection<Document> search(final String queryString) throws SonosException;

    /**
     * Shutdown.
     */
    public abstract void shutdown();

}