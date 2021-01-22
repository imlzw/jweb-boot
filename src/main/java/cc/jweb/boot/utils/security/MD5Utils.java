/*
 * Copyright (c) 2020-2021 imlzw@vip.qq.com jweb.cc.
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

package cc.jweb.boot.utils.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {

	private MD5Utils() {}
	
	public static String getMD5String(String plainText) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();

			int i;

			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			// 32位加密
			return buf.toString();
			// 16位的加密
			// return buf.toString().substring(8, 24);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getMD5StringByRL(String str) {
		if (str == null) {
			str = getMD5String("123456");
		}
		if (str.length() != 32) {
			str = getMD5String(str);
		}
		StringBuilder sb = new StringBuilder();
		String[] array = new String[4];
		for (int i = 0, j = 0; j < 4; i += 8, j++) {
			String s = str.substring(i, i + 8);
			if (j % 2 == 1) {
				array[j] = s.toLowerCase();
			} else
				array[j] = s.toUpperCase();
		}
		for (int i = 0; i < 4; i++) {
			sb.append(array[4 - i - 1]);
		}
		return getMD5String(sb.toString());
	}
	
}
