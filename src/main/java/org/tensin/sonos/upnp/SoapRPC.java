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
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

class SoapRPC {
	static Charset cs = Charset.forName("UTF-8");

	static final int MAXIO = 256*1024;
	public boolean trace_io;
	public boolean trace_reply;

	/* actual host to communicate with */
	InetAddress addr;
	int port;

	/* XML object for reply */
	XML xml;

	/* io buffer */
	ByteBuffer bb;

	/* assembly buffers for rpc header and message */
	StringBuilder hdr;
	StringBuilder msg;

	CharsetEncoder encoder;

	/* hold remote information while assembling message */
	String xmethod, xservice, xpath;

	public SoapRPC(String host, int port) {
		init(host,port);
	}

	void init(String host, int port) {
		try {
			addr = InetAddress.getByName(host);
		} catch (Exception x) {
		}
		this.port = port;

		encoder = cs.newEncoder();

		bb = ByteBuffer.wrap(new byte[MAXIO]);
		xml = new XML(MAXIO);
		hdr = new StringBuilder(2048);
		msg = new StringBuilder(8192);
	}


	void call() {
		CharBuffer hdrbuf;
		CharBuffer msgbuf;
		CoderResult cr;
		int off, r;
		byte[] buf;
		try {
			Socket s = new Socket(addr,port);
			OutputStream out = s.getOutputStream();
			InputStream in = s.getInputStream();

			if (trace_io) {
				System.err.println("--------- xmit -----------");
				System.err.print(hdr);
				System.err.print(msg);
			}

 			buf = bb.array();

			/* to keep things simple, the headers must fit in one pass */
			bb.clear();
			encoder.reset();
			hdrbuf = CharBuffer.wrap(hdr);
			cr = encoder.encode(hdrbuf, bb, false);
			if (cr != CoderResult.UNDERFLOW)
				throw new Exception("encoder failed (1)");	

			msgbuf = CharBuffer.wrap(msg);
			do {
				cr = encoder.encode(msgbuf, bb, true);
				if (cr.isError())
					throw new Exception("encoder failed (2)");
				out.write(buf, 0, bb.position());
				bb.clear();
			} while (cr == CoderResult.OVERFLOW);


			/* read reply */
			for (off = 0;;off += r) {
				r = in.read(buf, off, buf.length-off);
				if (r <= 0)
					break;
			}
			bb.limit(off);

			s.close();
			if (trace_io) {
				System.err.println("--------- recv -----------");
				System.err.println(new String(buf, 0, off));
				System.err.println("--------- done -----------");
			}
		} catch (Exception x) {
			System.out.println("OOPS: " + x.getMessage());
			x.printStackTrace();
		}
	}

	public void prepare(Endpoint ept, String method) {
		xmethod = method;
		xservice = ept.service;
		xpath = ept.path;

		msg.setLength(0);
		hdr.setLength(0);

		/* setup message envelope/prefix */	
		msg.append("<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"><s:Body><u:");
		msg.append(xmethod);
		msg.append(" xmlns:u=\"urn:schemas-upnp-org:service:");
		msg.append(xservice);
		msg.append("\">\n");
	}

	public XML invoke() {
		if (xmethod == null)
			throw new RuntimeException("cannot invoke before prepare");

		/* close the envelope */
		msg.append("</u:");
		msg.append(xmethod);
		msg.append("></s:Body></s:Envelope>\n");

		/* build HTTP headers */	
		hdr.append("POST ");
		hdr.append(xpath);
		hdr.append(" HTTP/1.0\r\n"+"CONNECTION: close\r\n"+
			"Content-Type: text/xml; charset=\"utf-8\"\r\n"+
			"Content-Length: ");
		hdr.append(msg.length());
		hdr.append("\r\n"+"SOAPACTION: \"urn:schemas-upnp-org:service:");
		hdr.append(xservice);
		hdr.append("#");
		hdr.append(xmethod);
		hdr.append("\"\r\n\r\n");

		xmethod = null;

		call();

		xml.init(bb);
		try {
			if (trace_reply) {
				System.out.println("--------- reply -----------");
				xml.print(System.out,64);
				xml.rewind();
			}
			xml.open("s:Envelope");
			xml.open("s:Body");
			return xml;
		} catch (XML.Oops x) {
			System.err.println("OOPS " + x);
			x.printStackTrace();
			return null;
		}
	}

	public SoapRPC openTag(String name) {
		msg.append('<');
		msg.append(name);
		msg.append('>');
		return this;
	}
	public SoapRPC closeTag(String name) {
		msg.append('<');
		msg.append('/');
		msg.append(name);
		msg.append('>');
		return this;
	}
	public SoapRPC simpleTag(String name, int value) {
		openTag(name);
		msg.append(value);
		closeTag(name);
		return this;
	}
	public SoapRPC simpleTag(String name, String value) {
		openTag(name);
		encode(value);
		closeTag(name);
		return this;
	}
	public SoapRPC encode(CharSequence csq) {
		int n, max = csq.length();
		char c;
		for (n = 0; n < max; n++) {
			switch((c = csq.charAt(n))) {
			case '<':
				msg.append("&lt;");
				break;
			case '>':
				msg.append("&gt;");
				break;
			case '&':
				msg.append("&amp;");
				break;
			case '"':
				msg.append("&quot;");
				break;
			case '\'':
				msg.append("&apos;");
				break;
			default:
				msg.append(c);
			}
		}
		return this;
	}
	public static class Endpoint {
		String service,path;
		public Endpoint(String service, String path) {
			this.service = service;
			this.path = path;
		}
	}
}

