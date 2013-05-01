package com.polycom.edge.webadmin.remote.rest.utils;

public class StrUtils {

	public StrUtils() {
		super();
	}

	public static Boolean isBlank(CharSequence cs) {
		// 标记字符长度，
		int strLen;
		// 字符串不存在或者长度为0
		if (cs == null || (strLen = cs.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			// 判断空格，回车，换行等，如果有一个不是上述字符，就返回false
			if (Character.isWhitespace(cs.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}

	// 这个是isNotBlank()
	public static boolean isNotBlank(CharSequence cs) {
		return !StrUtils.isBlank(cs);
	}
	
	public static boolean areNotBlank(CharSequence[] cs) {
		for(CharSequence c:cs){
			if(!StrUtils.isBlank(c)){
				continue;
			}
			return false;
		}
		return true;
	}
}
