package org.tensin.sonos.web.vaadin;

import java.io.Serializable;

import org.tensin.sonos.model.Entry;
import org.tensin.sonos.model.MusicLibrary;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;

public class PanelCommands extends AbstractVaadinPanel {

    class SearchFilter implements Serializable {

        private final String term;
        private final Object propertyId;
        private final String searchName;

        public SearchFilter(final Object propertyId, final String searchTerm, final String name) {
            this.propertyId = propertyId;
            term = searchTerm;
            searchName = name;
        }

        // + getters
    }

    /** serialVersionUID */
    private static final long serialVersionUID = -8755064618320070798L;

    TextField searchField;

    public PanelCommands() {
        super("Commands");
        setSizeFull();

    }

    public void init() {

        // private NativeSelect fieldToSearch;
        // private CheckBox saveSearch;

        FormLayout formLayout = new FormLayout();
        setContent(formLayout);

        searchField = new TextField("Search term");
        Button searchButton = new Button("Search");
        searchButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                performSearch();
            }

        });

        addComponent(searchField);
        addComponent(searchButton);
    }

    private void performSearch() {
        String searchTerm = (String) searchField.getValue();
        String zoneName = sonosState.getSelectedZoneName();
        MusicLibrary library = sonosState.getMusicLibrary(zoneName);
        for (int i = 0; i < library.getSize(); i++) {
            Entry e = library.getEntryAt(i);

            if (e.getTitle().matches(searchTerm) || e.getCreator().matches(searchTerm)) {
                System.out.println(e.getTitle());
            }
        }
        // SearchFilter searchFilter = new SearchFilter(fieldToSearch.getValue(), searchTerm, (String) searchName.getValue());
        // app.search(searchFilter);
    }

}
