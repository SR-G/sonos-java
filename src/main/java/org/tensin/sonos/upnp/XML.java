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

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.io.PrintStream;

// TODO: &apos; -> '

public class XML {
	private static final boolean DEBUG = false;

	XMLSequence seq; /* entire buffer */
	XMLSequence tmp; /* for content return */
	char[] xml;

	/* offset and length of the name of the current tag */
	int tag_off;
	int tag_len;
	/* offset and length of the attr area of the current tag */
	int att_off;
	int att_len;
	/* true if the current tag is <...>, false if </...> */
	boolean isOpen;

	CharsetDecoder decoder;

	/* used for io operations */
	CharBuffer buf;

	public XML(int size) {
		decoder = cs.newDecoder();
		seq = new XMLSequence();
		tmp = new XMLSequence();
		xml = new char[size];
		buf = CharBuffer.wrap(xml);	
	}

	public void init(ByteBuffer in) {
		buf.clear();
		CoderResult cr = decoder.decode(in, buf, true);
		// TODO: error handling
		buf.flip();
		reset();
	}
	public void init(XMLSequence s) {
		buf.clear();
		buf.put(s.data, s.offset, s.count);
		buf.flip();
		reset();
	}
	void reset() {
		seq.init(xml, buf.arrayOffset(), buf.length());
		tmp.init(xml, 0, 0);
		nextTag();
	}
	public void rewind() {
		seq.pos = seq.offset;
		nextTag();
	}
	
	public XMLSequence getAttr(String name) {
		int nlen = name.length();
		int n;

		tmp.offset = att_off;
		tmp.count = att_len;
		tmp.pos = att_off;

		for (;;) {
			int off = tmp.space();
			int len = tmp.name();
			if (len < 0)
				break;
			if (DEBUG) System.err.println("ANAME: ["+new String(tmp.data,off,len)+"]");
			int voff = tmp.value();
			if (voff < 0)
				break;
			int vend = tmp.next('"');
			if (vend < 0)
				break;
			vend--;
			if (DEBUG) System.err.println("ATEXT: ["+new String(tmp.data,voff,vend-voff)+"]");

			if (nlen != len)
				continue;
			for (n = 0; n < len; n++)
				if (name.charAt(n) != tmp.data[off+n]) /* XXX yuck */
					break;
			if (nlen != n)
				continue;

			tmp.offset = voff;
			tmp.count = vend - voff;
			return tmp;
		}
		return null;
	}

	/* set sequence to the text between the end of the current tag
	 * and the beginning of the next tag.
	 */
	public XMLSequence getText() {
		char[] data = xml;
		int n;
		tmp.data = data;
		n = tmp.offset = (att_off + att_len);
		try {
			for (;;) {
				if (data[n] == '<')
					break;
				n++;
			}
			tmp.count = n - tmp.offset;
		} catch (ArrayIndexOutOfBoundsException x) {
			tmp.count = 0;
		}
		return tmp;
	}

	public void print(PrintStream out, int max) {
		char[] buf = new char[max];
		print(out, max, 0, buf);
	}
	void print(PrintStream out, int max, int indent, char[] buf) {
		XMLSequence s;
		int n;
		if (!isOpen) {
			out.println("ERROR");
			return;
		}
		for (n = 0; n < indent; n++)
			out.print(" ");
		out.print(str());
		s = getText();
		nextTag();
		if (isOpen) {
			out.print("\n");
			do {
				print(out, max, indent + 2, buf);
			} while (isOpen);
			for (n = 0; n < indent; n++)
				out.print(" ");
			out.println(str());
		} else {
			if (s.count > max) {
				s.count = max;
				n = s.unescape(buf, 0);
				out.println("" + new String(buf, 0, n) + "..." + str());
			} else {
				n = s.unescape(buf, 0);
				out.println("" + new String(buf, 0, n) + str());
			}
		}	
		nextTag();
	}

	public boolean more() {
		return isOpen;
	}

	public boolean tag_eq(CharSequence name) {
		if (name.length() != tag_len)
			return false;
		for (int n = 0; n < tag_len; n++)
			if (name.charAt(n) != seq.data[tag_off + n])  /* XXX yuck */
				return false;
		return true;
	}
	/* require <tag> and consume it */
	public void open(String name) throws XML.Oops {
		if (!isOpen || !tag_eq(name))
			throw new XML.Oops("expecting <"+name+"> but found " + str());
		nextTag();
	}

	/* require </tag> and consume it */
	public void close(String name) throws XML.Oops {
		if (isOpen || !tag_eq(name))
			throw new XML.Oops("expecting </"+name+"> but found " + str());
		nextTag();
	}

	/* require <tag> text </tag> and return text */
	public XMLSequence read(String name) throws XML.Oops {
		int start = att_off + att_len;
		open(name);
		tmp.adjust(start, tag_off - 2);
		close(name);
		if (DEBUG) System.err.println("VAL ["+tmp+"]");
		return tmp;
	}

	/* read the next  <name> value </name>  returns false if no open tag */
	public boolean tryRead(XMLSequence name, XMLSequence value) throws XML.Oops {
		if (!isOpen)
			return false;

		name.data = xml;
		name.offset = tag_off;
		name.count = tag_len;

		value.data = xml;
		value.offset = att_off + att_len;
		nextTag();
		value.count = tag_off - value.offset - 2;

		close(name);

		return true;
	}
	public void close(XMLSequence name) throws XML.Oops {
		if (isOpen)
			throw new XML.Oops("1expected </"+name+">, found "+str());
		if (!tag_eq(name))
			throw new XML.Oops("2expected </"+name+">, found "+str());
		nextTag();
	}

	public boolean tryRead(String name, XMLSequence value) throws XML.Oops {
		if (!isOpen || !tag_eq(name))
			return false;
		value.data = xml;
		value.offset = att_off + att_len;
		nextTag();
		value.count = tag_off - value.offset;
		close(name);
		return true;
	}

	/* eat the current tag and any children */
	public void consume() throws XML.Oops {
		tmp.offset = tag_off;
		tmp.count = tag_len;
		nextTag();
		while (isOpen)
			consume();
		close(tmp);
	}

	/* format current begin/end tag as a string. for error messages */
	String str() {
		if (isOpen)
			return "<" + new String(seq.data, tag_off, tag_len) + ">";
		else
			return "</" + new String(seq.data, tag_off, tag_len) + ">";
	}

	void nextTag() {
		/* can't deal with comments or directives */
		int off = seq.next('<');
		boolean opn = seq.isOpen();
		int len = seq.name();
		int att = seq.pos;
		int nxt = seq.next('>');

		/* don't advance if we're in a strange state */
		if ((off < 0) || (len < 0) || (nxt < 0))
			return;

		if (!opn)
			off++;

		tag_off = off;
		tag_len = len;
		isOpen = opn;

		att_off = att;
		att_len = nxt - att;
	
		if (DEBUG) {	
			if (opn) {
				System.err.println("TAG ["+new String(seq.data,tag_off,tag_len)+"]");
				System.err.println("ATR ["+new String(seq.data,att_off,att_len)+"]");
			} else {
				System.err.println("tag ["+new String(seq.data,tag_off,tag_len)+"]");
			}
		}
	}

	static Charset cs = Charset.forName("UTF-8");

	static public class Oops extends Exception {
		public Oops(String msg) {
			super(msg);
		}
	}
}
