package org.tensin.sonos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensin.sonos.model.Entry;

/**
 * The Class SonosIndexer.
 */
public class SonosIndexer implements ISonosIndexer {

    /**
     * The Class indexingThread.
     */
    private class IndexingThread implements Runnable {

        /** The active. */
        private final boolean active = true;

        /**
         * {@inheritDoc}
         * 
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            IndexWriter indexWriter = null;
            try {
                indexWriter = getIndexWriter();
            } catch (SonosException e) {
                LOGGER.error("Error while getting index writer", e);
            }
            Entry entry = null;
            while (active) {
                try {
                    entry = queue.take();
                    final Document doc = new Document();
                    doc.add(new Field("album", entry.getAlbum(), Field.Store.YES, Field.Index.ANALYZED));
                    doc.add(new Field("artist", entry.getCreator(), Field.Store.YES, Field.Index.ANALYZED));
                    doc.add(new Field("title", entry.getTitle(), Field.Store.YES, Field.Index.ANALYZED));
                    doc.add(new Field("albumartist", entry.getAlbumArtist(), Field.Store.YES, Field.Index.ANALYZED));
                    doc.add(new Field("id", entry.getId(), Field.Store.YES, Field.Index.ANALYZED));
                    doc.add(new Field("upnpclass", entry.getUpnpClass(), Field.Store.YES, Field.Index.ANALYZED));
                    // doc.add(new Field("zones", entry.getUpnpClass(), Field.Store.YES, Field.Index.ANALYZED));
                    indexWriter.addDocument(doc);
                    indexWriter.commit();
                } catch (CorruptIndexException e) {
                    LOGGER.error("Error while processing entry [" + entry.toString() + "]", e);
                } catch (LockObtainFailedException e) {
                    LOGGER.error("Error while processing entry [" + entry.toString() + "]", e);
                } catch (IOException e) {
                    LOGGER.error("Error while processing entry [" + entry.toString() + "]", e);
                } catch (InterruptedException e) {
                    LOGGER.error("Interrupted while processing entry", e);
                }
            }
        }

    }

    /** The Constant index. */
    private static final Directory index = new RAMDirectory();

    /** The Constant analyzer. */
    private static final StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);

    /** config. */
    private final static IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36, analyzer);

    /** The w. */
    private static IndexWriter w = null;

    /** The queue. */
    private static BlockingQueue<Entry> queue = new LinkedBlockingQueue<Entry>();

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SonosIndexer.class);

    /** The indexing thread. */
    private IndexingThread indexingThread;

    /** The instance. */
    private static SonosIndexer INSTANCE = new SonosIndexer();

    /**
     * Gets the single instance of SonosIndexer.
     * 
     * @return single instance of SonosIndexer
     */
    public static ISonosIndexer getInstance() {
        return INSTANCE;
    }

    /**
     * Instantiates a new sonos indexer.
     */
    public SonosIndexer() {

    }

    /**
     * Gets the index writer.
     * 
     * @return the index writer
     * @throws SonosException
     *             the sonos exception
     */
    private IndexWriter getIndexWriter() throws SonosException {
        if (w == null) {
            try {
                w = new IndexWriter(index, config);
            } catch (CorruptIndexException e) {
                throw new SonosException(e);
            } catch (LockObtainFailedException e) {
                throw new SonosException(e);
            } catch (IOException e) {
                throw new SonosException(e);
            }
        }
        return w;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonosIndexer#index(org.tensin.sonos.model.Entry)
     */
    @Override
    public void index(final Entry entry) throws SonosException {
        queue.offer(entry);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonosIndexer#init()
     */
    @Override
    public void init() {
        new Thread(indexingThread).start();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonosIndexer#search(java.lang.String)
     */
    @Override
    public Collection<Document> search(final String queryString) throws SonosException {
        final Collection<Document> results = new ArrayList<Document>();
        try {
            Query q = new QueryParser(Version.LUCENE_36, "title", analyzer).parse(queryString);

            int hitsPerPage = 1000;
            IndexReader reader = IndexReader.open(index);
            IndexSearcher searcher = new IndexSearcher(reader);
            TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
            searcher.search(q, collector);
            ScoreDoc[] hits = collector.topDocs().scoreDocs;

            System.out.println("Found " + hits.length + " hits.");
            for (int i = 0; i < hits.length; ++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                results.add(d);
                System.out.println((i + 1) + ". " + d.get("title"));
            }

            // searcher can only be closed when there
            // is no need to access the documents any more.
            searcher.close();
        } catch (ParseException e) {
            throw new SonosException(e);
        } catch (IOException e) {
            throw new SonosException(e);
        }

        return results;

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonosIndexer#shutdown()
     */
    @Override
    public void shutdown() {

    }
}
