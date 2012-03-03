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

import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.io.IOException;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.HashMap;
import java.util.Set;

public class Discover extends Thread {
	static final int SSDP_PORT = 1900;
	static final String SSDP_ADDR = "239.255.255.250";

	static String query =
		"M-SEARCH * HTTP/1.1\r\n"+
		"HOST: 239.255.255.250:1900\r\n"+
		"MAN: \"ssdp:discover\"\r\n"+
		"MX: 1\r\n"+
		"ST: urn:schemas-upnp-org:service:AVTransport:1\r\n"+
		//"ST: ssdp:all\r\n"+
		"\r\n";

	InetAddress addr;
	MulticastSocket socket;
	Pattern pLocation;
	volatile boolean active;
	Discover.Listener callback;
	Object lock;
	HashMap<String,String> list;

	void send_query() throws IOException {
		DatagramPacket p;
		p = new DatagramPacket(
			query.getBytes(),query.length(),
			addr,SSDP_PORT);
		socket.send(p);
		socket.send(p);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		socket.send(p);
	}
	void handle_notify(DatagramPacket p) throws IOException {
		socket.receive(p);
		String s = new String(p.getData(), 0, p.getLength());
		// System.out.println(">>>>>" + s);
		Matcher m = pLocation.matcher(s);
		if (m.find(0)) {
			boolean notify = false;
			String a = m.group(1);
			synchronized (lock) {
				if (!list.containsKey(a)) {
					list.put(a,a);
					notify = true;
				}
			}
			if (notify && (callback != null))
				callback.found(a);
		}
	}
	public String[] getList() {
		synchronized (lock) {
			Set<String> set = list.keySet();
			String[] out = new String[set.size()];
			int n = 0;
			for (String s : set)
				out[n++] = s;
			return out;
		}
	}
	public void run() {
		DatagramPacket p =
			new DatagramPacket(new byte[1540], 1540);
		list = new HashMap<String,String>();
		lock = new Object();
		try {
			addr = InetAddress.getByName(SSDP_ADDR);
			socket = new MulticastSocket(SSDP_PORT);
			socket.joinGroup(addr);
			send_query();
		} catch (IOException x) {
			System.err.println("cannot create socket");
		}
		while (active) {
			try {
				handle_notify(p);
			} catch (IOException x) {
				/* done causes an exception when it closes the socket */
				if (active)
					System.err.println("io error");
			}
		}
	}
	public void done() {
		active = false;
		socket.close();	
	}
	public Discover() {
		init(null);
	}	
	public Discover(Discover.Listener cb) {
		init(cb);
	}
	void init(Discover.Listener cb) {
		setName("SONOS-THREAD-DISCOVER");
		active = true;
		this.callback = cb;
		pLocation = Pattern.compile("^LOCATION:\\s*http://(.*):1400/xml/device_description.xml$",Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
		start();
	}
	public static interface Listener {
		public void found(String host);
	}
}
