package cn.tqhweb.jweb.util;

import java.util.regex.Pattern;

public class Verification {
	private static Pattern emailPattern = Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
	private static Pattern phonePattern = Pattern.compile("^(13[0-9]|14[5|7]|15[0|1|2|3|4|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$");
	private static Pattern idPattern = Pattern.compile("(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)");
	private static Pattern domainPattern = Pattern.compile("[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+\\.?");

	// 邮箱号
	public static boolean isEmail(String email) {
		return emailPattern.matcher(email).matches();
	}

	// 手机号
	public static boolean isPhone(String phone) {
		return phonePattern.matcher(phone).matches();
	}

	// 身份证
	public static boolean isIDnumber(String id) {
		return idPattern.matcher(id).matches();
	}
	
	// 域名
	public static boolean isDomain(String doamin) {
		return domainPattern.matcher(doamin).matches();
	}
}
