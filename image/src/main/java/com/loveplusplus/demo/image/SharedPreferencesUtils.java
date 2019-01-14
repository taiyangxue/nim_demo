package com.loveplusplus.demo.image;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtils {
	public static final String PREF_NAME="config";
	public static void setBoolean(Context context,String key,boolean value) {
		SharedPreferences sp = context.getSharedPreferences(PREF_NAME,
				context.MODE_PRIVATE);
		sp.edit().putBoolean(key, value).commit();
	}
	public static boolean getBoolean(Context context,String key,boolean defaultValue) {
		SharedPreferences sp = context.getSharedPreferences(PREF_NAME,
				context.MODE_PRIVATE);
		return sp.getBoolean(key, defaultValue);
	}
	public static void setString(Context context,String key,String value) {
		SharedPreferences sp = context.getSharedPreferences(PREF_NAME,
				context.MODE_PRIVATE);
		sp.edit().putString(key, value).commit();
	}
	public static String getString(Context context,String key,String defaultValue) {
		SharedPreferences sp = context.getSharedPreferences(PREF_NAME,
				context.MODE_PRIVATE);
		return sp.getString(key, defaultValue);
	}
	public static void setInt(Context context,String key,int value) {
		SharedPreferences sp = context.getSharedPreferences(PREF_NAME,
				context.MODE_PRIVATE);
		sp.edit().putInt(key, value).commit();
	}
	public static int getInt(Context context,String key,int defaultValue) {
		SharedPreferences sp = context.getSharedPreferences(PREF_NAME,
				context.MODE_PRIVATE);
		return sp.getInt(key, defaultValue);
	}
}
