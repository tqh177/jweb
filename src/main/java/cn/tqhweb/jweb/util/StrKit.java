package cn.tqhweb.jweb.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class StrKit {
	/**
	 * 首字母大写
	 * 
	 * @param string
	 * @return
	 */
	static public String ucfirst(String string) {
		if (string.isEmpty()) {
			return string;
		}
		char[] chars = string.toCharArray();
		if (chars[0] >= 'a' && chars[0] <= 'z') {
			chars[0] -= 32;
		}
		return String.valueOf(chars);
	}

	/**
	 * 判断字符串数组是否全为非空字符串且非null
	 * 
	 * @param arr
	 * @return
	 */
	static public boolean all(String[] arr) {
		for (String string : arr) {
			if (string == null || string.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断字符串数组是否全为空字符串或null
	 * 
	 * @param arr
	 * @return
	 */
	static public boolean any(String[] arr) {
		for (String string : arr) {
			if (string != null && !string.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断source中是否包含dist中的任何一个字符
	 * 
	 * @param source
	 * @param dist
	 * @return
	 */
	static public boolean containOne(String source, String dist) {
		int len = source.length();
		for (int i = 0; i < len; i++) {
			if (dist.indexOf(source.charAt(i)) > -1) {
				return true;
			}
		}
		return false;
	}

	// 判断是否包含表情字符
	static public boolean containEmojiCharacter(CharSequence sequence) {
		int len = sequence.length();
		for (int i = 0; i < len; i++) {
			char codePoint = sequence.charAt(i);
			if (!((codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD)
					|| ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
					|| ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
					|| ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断是否是合格变量名
	 * 
	 * @param name
	 * @return
	 */
	static public boolean isValidVarName(String name) {
		if (name == null || name.isEmpty()) {
			return false;
		}
		return CharKit.isWordCharacter(name.charAt(0));
	}

	/**
	 * 分割字符串
	 * 
	 * @param str
	 * @param delim
	 * @return
	 */
	// static public String[] split(String str, String delim) {
	// List<String> list = new ArrayList<String>();
	// StringTokenizer tokenizer = new StringTokenizer(str, delim);
	// while (tokenizer.hasMoreTokens()) {
	// list.add(tokenizer.nextToken());
	// }
	// return list.toArray(new String[list.size()]);
	// }

	/**
	 * Stringbuilder替换字符串
	 * 
	 * @param builder
	 * @param find
	 * @param replace
	 */
	static public int replaceAll(StringBuilder builder, String find, String replace) {
		int i = builder.indexOf(find);
		int num = 0;
		while (i != -1) {
			builder.replace(i, find.length() + i, replace);
			i = builder.indexOf(find, i + 1);
			num++;
		}
		return num;
	}

	/**
	 * 替换字符串
	 * 
	 * @param string
	 * @param find
	 * @param replace
	 * @return
	 */
	static public String replaceAll(String string, String find, String replace) {
		StringBuilder builder = new StringBuilder(string);
		replaceAll(builder, find, replace);
		return builder.toString();
	}

	/**
	 * 重复字符串
	 * 
	 * @param string
	 * @param count
	 * @return
	 */
	static public String repeat(String string, int count) {
		char[] cs = new char[string.length() * count];
		for (int i = 0; i < count; i++) {
			System.arraycopy(string, 0, cs, string.length() * i, string.length());
		}
		return new String(cs);
	}

	/**
	 * 去除字符串两边的字符
	 * 
	 * @param str
	 * @param c
	 * @return
	 */
	static public String trim(String str, char c) {
		int beginIndex = 0;
		int endIndex = str.length();
		while ((beginIndex < endIndex) && (str.charAt(beginIndex) == c)) {
			beginIndex++;
		}
		while ((beginIndex < endIndex) && (str.charAt(endIndex - 1) == c)) {
			endIndex--;
		}
		return (beginIndex == endIndex) ? "" : str.substring(beginIndex, endIndex);
	}

	/**
	 * 去除字符串左边的字符
	 * 
	 * @param str
	 * @param c
	 * @return
	 */
	static public String ltrim(String str, char c) {
		int beginIndex = 0;
		int endIndex = str.length();
		while ((beginIndex < endIndex) && (str.charAt(beginIndex) == c)) {
			beginIndex++;
		}
		return (beginIndex == endIndex) ? "" : str.substring(beginIndex, endIndex);
	}

	/**
	 * 去除字符串右边的字符
	 * 
	 * @param str
	 * @param c
	 * @return
	 */
	static public String rtrim(String str, char c) {
		int beginIndex = 0;
		int endIndex = str.length();
		while ((beginIndex < endIndex) && (str.charAt(endIndex - 1) == c)) {
			endIndex--;
		}
		return (beginIndex == endIndex) ? "" : str.substring(beginIndex, endIndex);
	}

	/** 过滤html标签 */
	static public String htmlStripTags(String content) {
		StringBuilder builder = new StringBuilder();
		int len = content.length();
		for (int i = 0; i < len; i++) {
			char c = content.charAt(i);
			if (c == '<') {
				i = content.indexOf('>', i + 1);
				if (i == -1) {
					break;
				}
			} else {
				builder.append(c);
			}
		}
		return builder.toString();
	}

	/**
	 * 使用gzip进行压缩
	 */
	public static byte[] compress(String primStr) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = null;
		try {
			gzip = new GZIPOutputStream(out);
			gzip.write(primStr.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (gzip != null) {
				try {
					gzip.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return out.toByteArray();
	}

	/**
	 * 使用gzip进行解压缩
	 */
	public static String uncompress(byte[] compressed) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(compressed);
		GZIPInputStream ginzip = null;
		String decompressed = null;
		try {
			ginzip = new GZIPInputStream(in);
			byte[] buffer = new byte[1024];
			int offset = -1;
			while ((offset = ginzip.read(buffer)) != -1) {
				out.write(buffer, 0, offset);
			}
			decompressed = out.toString();
		} catch (IOException e) {
		} finally {
			if (ginzip != null) {
				try {
					ginzip.close();
				} catch (IOException e) {
				}
			}
			try {
				in.close();
			} catch (IOException e) {
			}
			try {
				out.close();
			} catch (IOException e) {
			}
		}
		return decompressed;
	}

	static public Integer toInt(String s) {
		try {
			return Integer.valueOf(s);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	static public Long toLong(String s) {
		try {
			return Long.valueOf(s);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	static public Boolean toBoolean(String s) {
		return Boolean.valueOf(s);
	}

	static public Float toFloat(String s) {
		try {
			return Float.valueOf(s);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	static public Double toDouble(String s) {
		try {
			return Double.valueOf(s);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	static public String md5(String text) {
		try {
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.update(text.getBytes("UTF8"));
			byte s[] = m.digest();
			StringBuilder result = new StringBuilder();
			for (int i = 0; i < s.length; i++) {
				result.append(Integer.toHexString((0x000000FF & s[i]) | 0xFFFFFF00).substring(6));
			}
			return result.toString();
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			return "";
		}
	}
}
