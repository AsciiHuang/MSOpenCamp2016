package com.ascii.localcacheproject;

import android.content.Context;
import android.content.SharedPreferences;

public class CacheUtils {

	public static void setCacheTime(Context context, long time) {
		SharedPreferences settings = context.getSharedPreferences("Preference", 0);
		settings.edit().putLong("CacheTime", time).commit();
	}

	public static void setCacheData(Context context, String data) {
		SharedPreferences settings = context.getSharedPreferences("Preference", 0);
		settings.edit().putString("CacheData", data).commit();
	}

	public static long getCacheTime(Context context) {
		SharedPreferences settings = context.getSharedPreferences("Preference", 0);
		long cacheTime = settings.getLong("CacheTime", 0);
		return cacheTime;
	}

	public static String getCacheData(Context context) {
		SharedPreferences settings = context.getSharedPreferences("Preference", 0);
		String cacheData = settings.getString("CacheData", "");
		return cacheData;
	}

}
