package com.example.locationapp.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.example.locationapp.ui.LocationApp;

public class LocationSharedPreference
{

	static final LocationSharedPreference locationSharedPref = new LocationSharedPreference();

	private SharedPreferences sharedPreferences;

	private Editor editor;

	private LocationSharedPreference()
	{
		sharedPreferences = LocationApp.getInstance().getSharedPreferences(Constants.LOCATION_SHARED_PREF, Activity.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
		editor = sharedPreferences.edit();
	}

	public static LocationSharedPreference getInstance()
	{
		return locationSharedPref;
	}

	public synchronized boolean saveData(String key, String value)
	{
		editor.putString(key, value);
		return editor.commit();
	}

	public synchronized boolean saveData(String key, boolean value)
	{
		editor.putBoolean(key, value);
		return editor.commit();
	}

	public synchronized boolean saveData(String key, long value)
	{
		editor.putLong(key, value);
		return editor.commit();
	}

	public synchronized boolean saveData(String key, float value)
	{
		editor.putFloat(key, value);
		return editor.commit();
	}

	public synchronized boolean saveData(String key, int value)
	{
		editor.putInt(key, value);
		return editor.commit();
	}

	public synchronized boolean removeData(String key)
	{
		editor.remove(key);
		return editor.commit();
	}

	public synchronized Boolean getData(String key, boolean defaultValue)
	{
		return sharedPreferences.getBoolean(key, defaultValue);
	}

	public synchronized String getData(String key, String defaultValue)
	{
		return sharedPreferences.getString(key, defaultValue);
	}

	public synchronized float getData(String key, float defaultValue)
	{

		return sharedPreferences.getFloat(key, defaultValue);
	}

	public synchronized int getData(String key, int defaultValue)
	{
		return sharedPreferences.getInt(key, defaultValue);
	}

	public synchronized long getData(String key, long defaultValue)
	{
		return sharedPreferences.getLong(key, defaultValue);
	}

	public synchronized void deleteAllData()
	{
		editor.clear();
		editor.commit();
	}

	public SharedPreferences getPref()
	{
		return sharedPreferences;
	}

}
