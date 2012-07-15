package org.tensin.sonos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

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
import org.tensin.sonos.model.Entry;

/**
 * The Class SonosIndexer.
 */
public class SonosIndexer {

    /** The Constant index. */
    private static final Directory index = new RAMDirectory();

    /** The Constant analyzer. */
    private static final StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);

    /** config. */
    private final static IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36, analyzer);

    /** The w. */
    private static IndexWriter w = null;

    /**
     * Gets the index writer.
     * 
     * @return the index writer
     * @throws SonosException
     *             the sonos exception
     */
    private static IndexWriter getIndexWriter() throws SonosException {
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
     * Index.
     * 
     * @param entry
     *            the entry
     * @throws SonosException
     *             the sonos exception
     */
    public static void index(final Entry entry) throws SonosException {
        try {
            final IndexWriter indexWriter = getIndexWriter();
            final Document doc = new Document();
            doc.add(new Field("album", entry.getAlbum(), Field.Store.YES, Field.Index.ANALYZED));
            doc.add(new Field("artist", entry.getCreator(), Field.Store.YES, Field.Index.ANALYZED));
            doc.add(new Field("title", entry.getTitle(), Field.Store.YES, Field.Index.ANALYZED));
            indexWriter.addDocument(doc);
            indexWriter.commit();
        } catch (CorruptIndexException e) {
            throw new SonosException(e);
        } catch (LockObtainFailedException e) {
            throw new SonosException(e);
        } catch (IOException e) {
            throw new SonosException(e);
        } finally {
        }
    }

    /**
     * Search.
     * 
     * @param queryString
     *            the query string
     * @return the collection
     * @throws SonosException
     *             the sonos exception
     */
    public static Collection<Document> search(final String queryString) throws SonosException {
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
}
