package cn.tqhweb.jweb.util;

public class ObjKit {
	/**
	 * 类型转换
	 * 
	 * @param object
	 * @param typeClass
	 * @return
	 */
	static public Object convert(Object object, Class<?> typeClass) {
		if (object == null) {
			return null;
		}
		if (typeClass.isInstance(object)) {
			return object;
		}
		String s = object.toString();
		if (Integer.class.equals(typeClass) || int.class.equals(typeClass)) {
			return StrKit.toInt(s);
		} else if (Long.class.equals(typeClass) || long.class.equals(typeClass)) {
			return StrKit.toLong(s);
		} else if (Boolean.class.equals(typeClass) || boolean.class.equals(typeClass)) {
			return StrKit.toBoolean(s);
		} else if (Double.class.equals(typeClass) || double.class.equals(typeClass)) {
			return StrKit.toDouble(s);
		}
		return null;
	}
	static public <T> T nonNullOrDefault(T obj, T defaultVal) {
		return obj == null ? defaultVal : obj;
	}
}
