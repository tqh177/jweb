package cn.tqhweb.jweb.util.json;

import com.alibaba.fastjson.serializer.SerializerFeature;

public interface JSON {
	public static String stringify(Object object) {
 		return FastJSON.toJSONString(object,SerializerFeature.DisableCircularReferenceDetect);
	}
	public static Object parse(String text) {
		return FastJSON.parse(text);
	}
}
