package cn.tqhweb.jweb.util;

public class CharKit {
	/**
	 * 判断是否是大写字母
	 * 
	 * @param c
	 * @return
	 */
	static public boolean isUppercase(char c) {
		return 'A' <= c && c <= 'Z';
	}

	/**
	 * 判断是否是小写字母
	 * 
	 * @param c
	 * @return
	 */
	static public boolean isLowercase(char c) {
		return 'a' <= c && c <= 'z';
	}

	/**
	 * 判断是否是字母
	 * 
	 * @param c
	 * @return
	 */
	static public boolean isLetter(char c) {
		return isUppercase(c) || isLowercase(c);
	}

	/**
	 * 判断是否是数字
	 * 
	 * @param c
	 * @return
	 */
	static public boolean isNumber(char c) {
		return '0' <= c && c <= '9';
	}

	/**
	 * 判断是否是单词字符
	 * 
	 * @param c
	 * @return
	 */
	static public boolean isWordCharacter(char c) {
		return isLetter(c) || c == '_';
	}
}
