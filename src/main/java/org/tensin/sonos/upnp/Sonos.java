/*
 * Copyright (C) 2011 Brian Swetland
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tensin.sonos.upnp;

/* not thread-safe, not reentrant */
public class Sonos {
	private boolean trace_browse;
	private SoapRPC.Endpoint xport;
	private SoapRPC.Endpoint media;
	private SoapRPC.Endpoint render;
	private SoapRPC.Endpoint props;
	private SoapRPC rpc;
	private XMLSequence name, value;
	private SonosItem item;
	private String host;

	public Sonos(String host) {
		this.host = host;
		init(host);
	}

	void init(String host) {
		name = new XMLSequence();
		value = new XMLSequence();
		item = new SonosItem();
		item.title = new XMLSequence();
		item.artist = new XMLSequence();
		item.album = new XMLSequence();
		item.idURI = new XMLSequence();
		item.playURI = new XMLSequence();

		rpc = new SoapRPC(host, 1400);

		xport = new SoapRPC.Endpoint(
			"AVTransport:1",
			"/MediaRenderer/AVTransport/Control");
		media = new SoapRPC.Endpoint(
			"ContentDirectory:1",
			"/MediaServer/ContentDirectory/Control");
		render = new SoapRPC.Endpoint(
			"RenderingControl:1",
			"/MediaRenderer/RenderingControl/Control");
		props = new SoapRPC.Endpoint(
			"DeviceProperties:1",
			"/DeviceProperties/Control");
	}

	public void trace_io(boolean x) { rpc.trace_io = x; }
	public void trace_reply(boolean x) { rpc.trace_reply = x; }
	public void trace_browse(boolean x) { trace_browse = x; }

	public String getZoneName() {
		rpc.prepare(props,"GetZoneAttributes");
		XML xml = rpc.invoke();
		try {
			xml.open("u:GetZoneAttributesResponse");
			return xml.read("CurrentZoneName").toString();
			//xml.read("CurrentIcon").toString();
		} catch (XML.Oops x) {
			return null;
		}
	}

	/* volume controls */
	public int volume() {
		int n;
		rpc.prepare(render,"GetVolume");
		rpc.simpleTag("InstanceID",0);
		rpc.simpleTag("Channel", "Master"); // Master | LF | RF
		XML xml = rpc.invoke();
		try {
			xml.open("u:GetVolumeResponse");
			n = Integer.parseInt(xml.read("CurrentVolume").toString());
			if (n < 0)
				n = 0;
			if (n > 100)
				n = 100;
			return n;
		} catch (XML.Oops x) {
			return -1;
		}
	}
	public void volume(int vol) { // 0-100
		if ((vol < 0) || (vol > 100))
			return;
		rpc.prepare(render,"SetVolume");
		rpc.simpleTag("InstanceID",0);
		rpc.simpleTag("Channel","Master");
		rpc.simpleTag("DesiredVolume",vol);
		rpc.invoke();
	}

/* GetMediaInfo:
 * NrTracks and CurrentURI indicate active queue and size 
 */
/* CurrentTransportState STOPPED | PAUSED_PLAYBACK | PLAYING | */
	/* transport controls */
	public void getPosition() {
		rpc.prepare(xport,"GetMediaInfo");
		//rpc.prepare(xport,"GetPositionInfo");
		//rpc.prepare(xport,"GetTransportInfo");
		rpc.simpleTag("InstanceID",0);
		XML xml = rpc.invoke();
		xml.print(System.out,1024);
		xml.rewind();
	}
	public void play() {
		rpc.prepare(xport,"Play");
		rpc.simpleTag("InstanceID",0);
		rpc.simpleTag("Speed",1);
		rpc.invoke();
	}
	public void pause() {
		rpc.prepare(xport,"Pause");
		rpc.simpleTag("InstanceID",0);
		rpc.invoke();
	}
	public void stop() {
		rpc.prepare(xport,"Stop");
		rpc.simpleTag("InstanceID",0);
		rpc.invoke();
	}
	public void next() {
		rpc.prepare(xport,"Next");
		rpc.simpleTag("InstanceID",0);
		rpc.invoke();
	}
	public void prev() {
		rpc.prepare(xport,"Previous");
		rpc.simpleTag("InstanceID",0);
		rpc.invoke();
	}
	public void seekTrack(int nr) {
		if (nr < 1)
			return;	
		rpc.prepare(xport,"Seek");
		rpc.simpleTag("InstanceID",0);
		rpc.simpleTag("Unit","TRACK_NR");
		rpc.simpleTag("Target",nr);
		rpc.invoke();
		// does not start playing if not already in playback mode
	}

	/* queue management */
	public void save(String name, String uri) {
		rpc.prepare(xport,"SaveQueue");
		rpc.simpleTag("InstanceID",0);
		rpc.simpleTag("Title",name); /* not unique */
		rpc.simpleTag("ObjectID",uri); /* "" for new */
		XML xml = rpc.invoke();
		/* saved queues are named SQ:# */
		xml.print(System.out,1024);
		xml.rewind();
	}
	public void setTransportURI(String uri) {
		rpc.prepare(xport,"SetAVTransportURI");
		rpc.simpleTag("InstanceID",0);
		rpc.simpleTag("CurrentURI",uri);
		rpc.simpleTag("CurrentURIMetaData","");
		rpc.invoke();
	}
	public String getTransportURI() {
		rpc.prepare(xport,"GetMediaInfo");
		rpc.simpleTag("InstanceID",0);
		XML xml = rpc.invoke();
		try {
			xml.open("u:GetMediaInfoResponse");
			xml.read("NrTracks");
			xml.read("MediaDuration");
			return xml.read("CurrentURI").toString();
		} catch (XML.Oops x) {
			return null;
		}
	}
	public void add(String uri) {
		rpc.prepare(xport,"AddURIToQueue");
		rpc.simpleTag("InstanceID",0);
		rpc.simpleTag("EnqueuedURI",uri);
		rpc.simpleTag("EnqueuedURIMetaData","");
		rpc.simpleTag("DesiredFirstTrackNumberEnqueued",0);
		rpc.simpleTag("EnqueueAsNext",0); // 0 = append, 1+ = insert
		rpc.invoke();
	}
	public void remove(String id) {
		rpc.prepare(xport,"RemoveTrackFromQueue");
		rpc.simpleTag("InstanceID",0);
		rpc.simpleTag("ObjectID",id);
		rpc.invoke();
	}
	public void removeAll() {
		rpc.prepare(xport,"RemoveAllTracksFromQueue");
		rpc.simpleTag("InstanceID",0);
		rpc.invoke();
	}
	public void move(int from, int to) {
		if ((from < 1) || (to < 1))
			return;
		rpc.prepare(xport,"ReorderTracksInQueue");
		rpc.simpleTag("InstanceID",0);
		rpc.simpleTag("StartingIndex",from);
		rpc.simpleTag("NumberOfTracks",1);
		rpc.simpleTag("InsertBefore",to);
		rpc.invoke();
	}

	/* can be used to delete saved queues (SQ:*) */
	public void destroy(String id) {
		rpc.prepare(media,"DestroyObject");
		rpc.simpleTag("ObjectID", id);
		rpc.invoke();
	}

	/* content service calls */
	public void browse(String _id, SonosListener cb) {
		int total, count, updateid;
		int n = 0;
		XML xml;

		do {
			rpc.prepare(media,"Browse");
			rpc.simpleTag("ObjectID",_id);
			rpc.simpleTag("BrowseFlag","BrowseDirectChildren"); // BrowseMetadata
			rpc.simpleTag("Filter","*");
			rpc.simpleTag("StartingIndex", n);
			rpc.simpleTag("RequestedCount",100);
			rpc.simpleTag("SortCriteria","");

			xml = rpc.invoke();
			try {
				xml.open("u:BrowseResponse");
				value.init(xml.read("Result"));

				// Eww, toString()? really? surely there's
				// a non-allocating Int parser somewhere
				// in the bloat that is java standard libraries?
				count = Integer.parseInt(xml.read("NumberReturned").toString());
				total = Integer.parseInt(xml.read("TotalMatches").toString());
				updateid = Integer.parseInt(xml.read("UpdateID").toString());

				/* descend in to the contained results */
				value.unescape();
				xml.init(value);
				n = processBrowseResults(xml,n,_id,cb);
			} catch (Exception x) {
				System.err.println("OOPS " + x);
				x.printStackTrace();
				break;
			}
		} while (n < total);
		cb.updateDone(_id);
	}
	int processBrowseResults(XML result, int n, String _id, SonosListener cb) throws XML.Oops {
		SonosItem item = this.item;
		if (trace_browse) {
			System.out.println("--------- list -----------");
			result.print(System.out,1024);
			result.rewind();
		}
		result.open("DIDL-Lite");
		while (result.more()) {
			String thing;
			n++;
			item.reset();
			item.idURI.init(result.getAttr("id"));
			try { 
				result.open("item");
				thing = "item";	
			} catch (XML.Oops x) {
				result.open("container"); // yuck!
				thing = "container";
			}
			while (result.tryRead(name,value)) {
				if (name.eq("dc:title")) {
					item.title.init(value.unescape());
					continue;
				}
				if (name.eq("dc:creator")) {
					item.artist.init(value.unescape());
					continue;
				}
				if (name.eq("upnp:album")) {
					item.album.init(value.unescape());
					continue;
				}
				if (name.eq("res")) {
					item.playURI.init(value.unescape());
					continue;
				}
				if (name.eq("upnp:class")) {
					/* object.item.... vs object.container... */
					if (value.charAt(7) == 'i')
						item.flags |= item.SONG;
				}
			}
			cb.updateItem(_id, n, item);
			result.close(thing);
		}
		return n;
	}
}
