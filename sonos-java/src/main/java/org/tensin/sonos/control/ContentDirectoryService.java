/*
 * Copyright 2007 David Wheeler
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tensin.sonos.control;

import java.util.List;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.meta.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensin.sonos.SonosException;
import org.tensin.sonos.model.Entry;
import org.tensin.sonos.xml.ResultParser;
import org.xml.sax.SAXException;

/**
 * Allows the listing and searching of the audio content of a zone player.
 * 
 * @author David WHEELER
 * @author Serge SIMON
 * 
 */
public class ContentDirectoryService extends AbstractService {

    /**
     * The Class AsyncBrowser.
     */
    private final class AsyncBrowser implements Runnable, BrowseHandle {

        /** The type. */
        private final String type;

        /** The callback. */
        private final EntryCallback callback;

        /** The is cancelled. */
        private boolean isCancelled = false;

        /**
         * Instantiates a new async browser.
         * 
         * @param type
         *            the type
         * @param callback
         *            the callback
         */
        protected AsyncBrowser(final String type, final EntryCallback callback) {
            this.type = type;
            this.callback = callback;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.tensin.sonos.control.BrowseHandle#cancel()
         */
        @Override
        public void cancel() {
            isCancelled = true;
        }

        /**
         * {@inheritDoc}
         * 
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            int startAt = 0;
            boolean completedSuccessfully = false;
            try {
                SonosActionInvocation response = getEntriesImpl(startAt, DEFAULT_REQUEST_COUNT, type, DEFAULT_BROWSE_TYPE, DEFAULT_FILTER_STRING,
                        DEFAULT_SORT_CRITERIA);
                int totalCount = response.getOutputAsInteger("TotalMatches");

                startAt = response.getOutputAsInteger("NumberReturned");
                callback.updateCount(this, totalCount);
                if (!isCancelled) {
                    callback.addEntries(this, ResultParser.getEntriesFromStringResult(response.getOutputAsString("Result")));
                }
                while (!isCancelled && (startAt < totalCount)) {
                    response = getEntriesImpl(startAt, DEFAULT_REQUEST_COUNT, type, DEFAULT_BROWSE_TYPE, DEFAULT_FILTER_STRING, DEFAULT_SORT_CRITERIA);
                    startAt += Integer.parseInt(response.getOutputAsString("NumberReturned"));
                    if (!isCancelled) {
                        callback.addEntries(this, ResultParser.getEntriesFromStringResult(response.getOutputAsString("Result")));
                    }
                }
                completedSuccessfully = !isCancelled;
            } catch (SAXException e) {
                LOGGER.error("Error while getting content directory", e);
            } finally {
                callback.retrievalComplete(this, completedSuccessfully);
            }

        }
    }

    /**
     * The Enum BrowseType.
     */
    public static enum BrowseType {

        /** The Browse direct children. */
        BrowseDirectChildren,
        /** The Browse metadata. */
        BrowseMetadata;
    }

    /** The Constant DEFAULT_REQUEST_COUNT. */
    public static final int DEFAULT_REQUEST_COUNT = 1024;

    /** The Constant DEFAULT_SORT_CRITERIA. */
    public static final String DEFAULT_SORT_CRITERIA = "";

    /** The Constant DEFAULT_FILTER_STRING. */
    public static final String DEFAULT_FILTER_STRING = "dc:title,res,dc:creator,upnp:artist,upnp:album";

    /** The Constant DEFAULT_BROWSE_TYPE. */
    public static final BrowseType DEFAULT_BROWSE_TYPE = BrowseType.BrowseDirectChildren;

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ContentDirectoryService.class);

    // private final ServiceEventHandler serviceEventHandler = new ServiceEventHandler() {
    // public void handleStateVariableEvent(final String variable, final String value) {
    // LOGGER.info("ContentDirectory Event: " + variable + "=" + value);
    // /*
    // * Expected Event Variables:
    // * SystemUpdateID
    // * ContainerUpdateID
    // * ShareListRefreshState [NOTRUN|RUNNING|DONE]
    // * ShareIndexInProgress
    // * ShareIndexLastError
    // * UserRadioUpdateID
    // * MasterRadioUpdateID
    // * SavedQueuesUpdateID
    // * ShareListUpdateID
    // */
    // // TODO implement
    //
    // }
    // };

    /**
     * Instantiates a new content directory service.
     * 
     * @param upnpService
     *            the upnp service
     * @param service
     *            the service
     */
    protected ContentDirectoryService(final UpnpService upnpService, final Service service) {
        super(upnpService, service, ZonePlayerConstants.SONOS_SERVICE_CONTENT_DIRECTORY);
        // registerServiceEventing(serviceEventHandler);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.control.AbstractService#dispose()
     */
    @Override
    public void dispose() {
        super.dispose();
        // unregisterServiceEventing(serviceEventHandler);
    }

    /**
     * Retrieves a list of Album entries from the device.
     * 
     * @param startAt
     *            the index of the first entry to be returned.
     * @param length
     *            the maximum number of entries to returned.
     * @return a List of Entries of maximum size <code>length</code>, or null if the request fails.
     * @throws SonosException
     */
    public List<Entry> getAlbums(final int startAt, final int length) throws SonosException {
        return getEntries(startAt, length, "A:ALBUM");
    }

    /**
     * Retrieves all entries of the given type asyncronously, via the given callback.
     * 
     * @param callback
     *            the callback
     * @param type
     *            the type
     * @return A unique handle to this search, allowing cancellation
     */
    public BrowseHandle getAllEntriesAsync(final EntryCallback callback, final String type) {
        AsyncBrowser handle = new AsyncBrowser(type, callback);

        new Thread(handle).start();

        // SonosController.getInstance().getWorkerExecutor().execute(handle);

        return handle;
    }

    /**
     * Retrieves a list of Artist entries from the device.
     * 
     * @param startAt
     *            the index of the first entry to be returned.
     * @param length
     *            the maximum number of entries to returned.
     * @return a List of Entries of maximum size <code>length</code>, or null if the request fails.
     * @throws SonosException
     */
    public List<Entry> getArtists(final int startAt, final int length) throws SonosException {
        return getEntries(startAt, length, "A:ARTIST");
    }

    /**
     * Retrieves a list of entries from the device.
     * 
     * @param startAt
     *            the index of the first entry to be returned.
     * @param length
     *            the maximum number of entries to returned.
     * @param type
     *            the type of entries to be retrieved eg "A:ARTIST" or "Q:".
     * @return a List of Entries of maximum size <code>length</code>, or null if the request fails.
     * @throws SonosException
     */
    public List<Entry> getEntries(final int startAt, final int length, final String type) throws SonosException {
        return getEntries(startAt, length, type, DEFAULT_BROWSE_TYPE);
    }

    /**
     * Retrieves a list of entries from the device.
     * 
     * @param startAt
     *            the index of the first entry to be returned.
     * @param length
     *            the maximum number of entries to returned.
     * @param type
     *            the type of entries to be retrieved eg "A:ARTIST" or "Q:".
     * @param browseType
     *            the desired browse type
     * @return a List of Entries of maximum size <code>length</code>, or null if the request fails.
     * @throws SonosException
     */
    public List<Entry> getEntries(final int startAt, final int length, final String type, final BrowseType browseType) throws SonosException {
        return getEntries(startAt, length, type, browseType, DEFAULT_FILTER_STRING, DEFAULT_SORT_CRITERIA);
    }

    /**
     * Retrieves a list of entries from the device.
     * 
     * @param startAt
     *            the index of the first entry to be returned.
     * @param length
     *            the maximum number of entries to returned.
     * @param type
     *            the type of entries to be retrieved eg "A:ARTIST" or "Q:".
     * @param browseType
     *            the desired browse type
     * @param filterString
     *            the comma-seperated list of fields to have returned
     * @param sortCriteria
     *            the sort criteria, or empty string to use default sort criteria
     * @return a List of Entries of maximum size <code>length</code>, or null if the request fails.
     * @throws SonosException
     */
    public List<Entry> getEntries(final int startAt, final int length, final String type, final BrowseType browseType, final String filterString,
            final String sortCriteria) throws SonosException {
        try {
            SonosActionInvocation response = getEntriesImpl(startAt, length, type, browseType, filterString, sortCriteria);

            // LOGGER.debug("response value types: " + response.getOutActionArgumentNames());
            LOGGER.info("Returned " + response.getOutputAsString("NumberReturned") + " of " + response.getOutputAsString("TotalMatches") + " results.");
            String result = response.getOutputAsString("Result");
            LOGGER.debug(result);
            return ResultParser.getEntriesFromStringResult(result);
        } catch (SAXException e) {
            throw new SonosException(e);
        }
    }

    /**
     * Performs a getEntries request, returning the response.
     * 
     * @param startAt
     *            the index of the first entry to be returned.
     * @param length
     *            the maximum number of entries to returned.
     * @param type
     *            the type of entries to be retrieved eg "A:ARTIST" or "Q:".
     * @param browseType
     *            either "BrowseMetadata" or "BrowseDirectChildren"
     * @param filter
     *            a filter on the returned results
     * @param sortCriteria
     *            how to sort the returned results
     * @return the ActionResponse retured from the Sonos unit
     *         @ * if a network error prevents communications
     */
    protected SonosActionInvocation getEntriesImpl(final int startAt, final int length, final String type, final BrowseType browseType, final String filter,
            final String sortCriteria) {
        SonosActionInvocation browseAction = messageFactory.getMessage(getService(), "Browse");
        browseAction.setInput("ObjectID", type);
        browseAction.setInput("BrowseFlag", String.valueOf(browseType));
        browseAction.setInput("Filter", filter);
        browseAction.setInput("StartingIndex", String.valueOf(startAt));
        browseAction.setInput("RequestedCount", String.valueOf(length));
        browseAction.setInput("SortCriteria", sortCriteria);
        executeImmediate(browseAction);
        return browseAction;
    }

    /**
     * Retrieves a list of root level entries from the device.
     * 
     * @param startAt
     *            the index of the first entry to be returned.
     * @param length
     *            the maximum number of entries to returned.
     * @return a List of Entries of maximum size <code>length</code>, or null if the request fails.
     * @throws SonosException
     */
    public List<Entry> getFolderEntries(final int startAt, final int length) throws SonosException {
        return getEntries(startAt, length, "A:");
    }

    /**
     * Retrieves a list of Track entries from the device, representing the current queue.
     * 
     * @param startAt
     *            the index of the first entry to be returned.
     * @param length
     *            the maximum number of entries to returned.
     * @return a List of Entries of maximum size <code>length</code>, or null if the request fails.
     * @throws SonosException
     */
    public List<Entry> getQueue(final int startAt, final int length) throws SonosException {
        return getEntries(startAt, length, "Q:0");
    }

    /**
     * Gets the search capabilites.
     * 
     * @return the search capabilites
     */
    public String getSearchCapabilites() {
        SonosActionInvocation browseAction = messageFactory.getMessage(getService(), "GetSortCapabilities");
        executeImmediate(browseAction);
        return browseAction.getOutputAsString("SortCaps");

    }

    /**
     * Gets the single entry.
     * 
     * @param type
     *            the type
     * @return the entry given by type, or null if it could not be retrieved
     * @throws SonosException
     */
    public Entry getSingleEntry(final String type) throws SonosException {
        try {
            SonosActionInvocation response = getEntriesImpl(0, 1, type, BrowseType.BrowseMetadata, DEFAULT_FILTER_STRING, DEFAULT_SORT_CRITERIA);
            // LOGGER.debug("response value types: " + response.getOutActionArgumentNames());
            LOGGER.info("Returned " + response.getOutputAsString("NumberReturned") + " of " + response.getOutputAsString("TotalMatches") + " results.");
            String result = response.getOutputAsString("Result");
            LOGGER.debug(result);
            List<Entry> entries = ResultParser.getEntriesFromStringResult(result);
            if (entries.size() > 0) {
                return entries.get(0);
            } else {
                return null;
            }
        } catch (SAXException e) {
            throw new SonosException(e);
        }

    }

    /**
     * Retrieves a list of Track entries from the device.
     * 
     * @param startAt
     *            the index of the first entry to be returned.
     * @param length
     *            the maximum number of entries to returned.
     * @return a List of Entries of maximum size <code>length</code>, or null if the request fails.
     * @throws SonosException
     */
    public List<Entry> getTracks(final int startAt, final int length) throws SonosException {
        return getEntries(startAt, length, "A:TRACKS");
    }

}
