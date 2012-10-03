package org.tensin.sonos.web.vaadin;

import java.io.Serializable;
import java.util.Collection;

import org.apache.lucene.document.Document;
import org.tensin.sonos.ISonosIndexer;
import org.tensin.sonos.SonosException;
import org.tensin.sonos.SonosIndexer;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;

/**
 * The Class PanelCommands.
 */
public class PanelCommands extends AbstractVaadinPanel {

    /**
     * The Class SearchFilter.
     */
    class SearchFilter implements Serializable {

        /** The term. */
        private final String term;

        /** The property id. */
        private final Object propertyId;

        /** The search name. */
        private final String searchName;

        /**
         * Instantiates a new search filter.
         * 
         * @param propertyId
         *            the property id
         * @param searchTerm
         *            the search term
         * @param name
         *            the name
         */
        public SearchFilter(final Object propertyId, final String searchTerm, final String name) {
            this.propertyId = propertyId;
            term = searchTerm;
            searchName = name;
        }

        // + getters
    }

    /** serialVersionUID. */
    private static final long serialVersionUID = -8755064618320070798L;

    /** The search field. */
    TextField searchField;

    /** The sonos indexer. */
    private final ISonosIndexer sonosIndexer = SonosIndexer.getInstance();

    /**
     * Instantiates a new panel commands.
     */
    public PanelCommands() {
        super("Commands");
        setSizeFull();

    }

    /**
     * Inits the.
     */
    public void init() {

        // private NativeSelect fieldToSearch;
        // private CheckBox saveSearch;

        final FormLayout formLayout = new FormLayout();
        setContent(formLayout);

        searchField = new TextField("Search term");
        final Button searchButton = new Button("Search");
        searchButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                try {
                    performSearch();
                } catch (final SonosException e) {
                    e.printStackTrace();
                }
            }

        });

        addComponent(searchField);
        addComponent(searchButton);
    }

    /**
     * Perform search.
     * 
     * @throws SonosException
     *             the sonos exception
     */
    private void performSearch() throws SonosException {
        final String queryString = (String) searchField.getValue();

        final Collection<Document> results = sonosIndexer.search(queryString);

        // String zoneName = sonosState.getSelectedZoneName();
        // MusicLibrary library = sonosState.getMusicLibrary(zoneName);
        // for (int i = 0; i < library.getSize(); i++) {
        // Entry e = library.getEntryAt(i);
        //
        // if (e.getTitle().matches(searchTerm) || e.getCreator().matches(searchTerm)) {
        // System.out.println(e.getTitle());
        // }
        // }
        // SearchFilter searchFilter = new SearchFilter(fieldToSearch.getValue(), searchTerm, (String) searchName.getValue());
        // app.search(searchFilter);
    }

}
