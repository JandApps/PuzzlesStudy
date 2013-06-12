package com.gmail.leonidandand.puzzlesstudy;

import java.util.HashMap;
import java.util.Map;

public class SaverLoader {
	private static Map<String, Object> storage = new HashMap<String, Object>();
	
	public static void save(String key, Object value) {
		storage.put(key.toLowerCase(), value);
	}
	
	public static Object load(String key) {
		return storage.get(key.toLowerCase());
	}
}
