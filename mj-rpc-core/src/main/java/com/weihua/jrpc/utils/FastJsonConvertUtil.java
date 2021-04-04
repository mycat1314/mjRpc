package com.weihua.jrpc.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FastJsonConvertUtil {


	private static final SerializerFeature[] featuresWithNullValue = { SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullBooleanAsFalse,
	        SerializerFeature.WriteNullListAsEmpty, SerializerFeature.WriteNullNumberAsZero, SerializerFeature.WriteNullStringAsEmpty };


	public static <T> T convertJSONToObject(String data, Class<T> clzss) {
		try {
			T t = JSON.parseObject(data, clzss);
			return t;
		} catch (Exception e) {
			log.error("convertJSONToObject Exception", e);
			return null;
		}
	}
	

	public static <T> T convertJSONToObject(JSONObject data, Class<T> clzss) {
		try {
			T t = JSONObject.toJavaObject(data, clzss);
			return t;
		} catch (Exception e) {
			log.error("convertJSONToObject Exception", e);
			return null;
		}
	}


	public static <T> List<T> convertJSONToArray(String data, Class<T> clzss) {
		try {
			List<T> t = JSON.parseArray(data, clzss);
			return t;
		} catch (Exception e) {
			log.error("convertJSONToArray Exception", e);
			return null;
		}
	}

	public static <T> List<T> convertJSONToArray(List<JSONObject> data, Class<T> clzss) {
		try {
			List<T> t = new ArrayList<T>();
			for (JSONObject jsonObject : data) {
				t.add(convertJSONToObject(jsonObject, clzss));
			}
			return t;
		} catch (Exception e) {
			log.error("convertJSONToArray Exception", e);
			return null;
		}
	}

	public static String convertObjectToJSON(Object obj) {
		try {
			String text = JSON.toJSONString(obj);
			return text;
		} catch (Exception e) {
			log.error("convertObjectToJSON Exception", e);
			return null;
		}
	}
	

	public static JSONObject convertObjectToJSONObject(Object obj){
		try {
			JSONObject jsonObject = (JSONObject) JSONObject.toJSON(obj);
			return jsonObject;
		} catch (Exception e) {
			log.error("convertObjectToJSONObject Exception", e);
			return null;
		}		
	}

	public static String convertObjectToJSONWithNullValue(Object obj) {
		try {
			String text = JSON.toJSONString(obj, featuresWithNullValue);
			return text;
		} catch (Exception e) {
			log.error("convertObjectToJSONWithNullValue Exception", e);
			return null;
		}
	}
}
