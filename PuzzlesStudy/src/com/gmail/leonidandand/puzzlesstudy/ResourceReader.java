package com.gmail.leonidandand.puzzlesstudy;
import android.content.res.Resources;


public class ResourceReader {
	private static Resources resources;

	public static void init(Resources res) {
		resources = res;
	}
	
	public static int colorById(int id) {
		return resources.getColor(id);
	}
	
	public static int integerById(int id) {
		return resources.getInteger(id);
	}
}
