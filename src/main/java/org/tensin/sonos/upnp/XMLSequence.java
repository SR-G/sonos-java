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

class XMLSequence implements CharSequence {
	char[] data;
	int offset;
	int count;
	int pos;

	public XMLSequence() { }

	public void init(char[] data, int start, int end) {
		this.data = data;
		offset = start;
		count = end - start;
		pos = offset;
	}
	public void init(XMLSequence s) {
		data = s.data;
		offset = s.offset;
		count = s.count;
		pos = offset;
	}
	void adjust(int start, int end) {
		offset = start;
		count = end - start;
		pos = offset;
	}
	boolean eq(XMLSequence other) {
		int count = this.count;
		if (count != other.count)
			return false;
		char[] a = this.data;
		int ao = this.offset;
		char[] b = other.data;
		int bo = other.offset;
		while (count-- > 0)
			if (a[ao++] != b[bo++])
				return false;
		return true;
	}
	boolean eq(String s) {
		int len = s.length();
		if (len != count)
			return false;
		for (int n = 0; n < len; n++)
			if (s.charAt(n) != data[offset+n])
				return false;
		return true;
	}
	/* modifies the sequence in-place, escaping basic entities */
	public XMLSequence unescape() {
		count = unescape(data, offset) - offset;
		return this;
	}

	/* copies the sequence into a char[] + offset, escaping basic entities */
	public int unescape(char[] out, int off) {
		char[] in = data;
		int n = offset;
		int max = n + count;

		while (n < max) {
			char c = in[n++];
			if (c != '&') {
				out[off++] = c;
				continue;
			}
			int e = n;
			while (n < max) {
				if (in[n++] != ';')
					continue;
				switch(in[e]) {
				case 'l': // lt
					out[off++] = '<';
					break;
				case 'g': // gt
					out[off++] = '>';
					break;
				case 'q': // quot
					out[off++] = '"';
					break;
				case 'a': // amp | apos
					if (in[e+1] == 'm')
						out[off++] = '&';
					else if (in[e+1] == 'p')
						out[off++] = '\'';
					break;
				}
				break;
			}
		}
		return off;
	}
	
	public CharSequence copy() {
		XMLSequence s = new XMLSequence();
		s.init(data, offset, offset + count);
		return s;
	}
	public void trim() {
		while (count > 0) {
			char c = data[offset];
			if ((c==' ')||(c=='\r')||(c=='\n')||(c=='\t')) {
				offset++;
				count--;
				continue;
			}
			c = data[offset + count - 1];
			if ((c==' ')||(c=='\r')||(c=='\n')||(c=='\t')) {
				count--;
				continue;
			}
			break;
		}
		pos = offset;
	}

	/* CharSequence interface */
	public int length() {
		return count;
	}
	public char charAt(int index) {
		//System.err.print("["+data[offset+index]+"]");
		return data[offset + index];
	}
	public CharSequence subSequence(int start, int end) {
		//System.err.println("[subSequence("+start+","+end+")]");
		XMLSequence x = new XMLSequence();
		x.init(data, offset + start, offset + end);
		return x;
	}
	public String toString() {
		if (data == null)
			return "";
		return new String(data, offset, count);
	}

	/* Parsing Tools */

	/* returns next offset after a match with c, or -1 if no match */
	int next(char c) {
		char[] x = data;
		int n = pos;
		int end = offset + count;
		while (n < end) {
			if (x[n++] == c) {
				pos = n;
				return n;
			}
		}
		return -1;
	}

	/* advance pos beyond whitespace, returns updated position */
	int space() {
		char[] x = data;
		int n = pos;
		int end = offset + count;
		while (n < end) {
			switch (x[n]) {
			case ' ':
			case '\t':
			case '\r':
			case '\n':
				n++;
				break;
			default:
				pos = n;
				return n;
			}
		}
		pos = n;
		return n;
	}

	/* matches an XML element or attribute name, returns length (-1==failure), pos will be after */
	int name() {
		char[] x = data;
		int n = pos;
		int end = offset + count;
		if (n >= end)
			return -1;
		try {
			if (xName0[x[n]] == 0)
				return -1;
		} catch (ArrayIndexOutOfBoundsException e) {
			return -1;
		}
		while (++n < end) {
			try {
				if(xNameX[x[n]] == 1)
					continue;
			} catch (ArrayIndexOutOfBoundsException e) { }
			break;
		}
		end = n - pos;
		pos = n;
		return end;
	}

	/* match =" returning position or -1 if no match */
	int value() {
		char[] x = data;
		int n = pos;
		int end = offset + count;
		if (n >= end)
			return -1;
		if (x[n++] != '=')
			return -1;
		if (n >= end)
			return -1;
		if (x[n++] != '"')
			return -1;
		pos = n;
		return n;
	}

	/* returns false (and advances) if data[pos] == '/', otherwise true */
	boolean isOpen() {
		if (data[pos] == '/') {
			pos++;
			return false;
		} else {
			return true;
		}
	}

	static char xName0[];
	static char xNameX[];

	static {
		xName0 = new char[127];
		xNameX = new char[127];
		int n;
		for (n = 'a'; n <= 'z'; n++) {
			xName0[n] = 1;
			xNameX[n] = 1;
		}
		for (n = 'A'; n <= 'Z'; n++) {
			xName0[n] = 1;
			xNameX[n] = 1;
		}
		xName0['_'] = 1;
		xName0[':'] = 1;

		for (n = '0'; n <= '9'; n++) {
			xNameX[n] = 1;
		}
		xNameX['_'] = 1;
		xNameX[':'] = 1;
		xNameX['-'] = 1;
		xNameX['.'] = 1;
	}
}
