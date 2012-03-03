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

import java.io.File;
import java.io.FileInputStream;

public class Util {
	public static byte[] loadFile(String name) {
		try {
			File f = new File(name);
			FileInputStream in = new FileInputStream(f);
			int sz = (int) f.length();
			byte[] data = new byte[sz];
			while (sz > 0) {
				int r = in.read(data, data.length - sz, sz);
				if (r <= 0)
					return null;
				sz -= r;
			}
			return data;
		} catch (Exception x) {
			return null;
		}
	}
}
