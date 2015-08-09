package com.example.locationapp.data;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.locationapp.Utils.Constants.Mylocation;

import android.location.Location;

public class MyLocation
{

	String lat, lng, speed, altitute, accuracy,bearing;

	public MyLocation(Location loc)
	{
		lat = loc.getLatitude() + "";
		lng = loc.getLongitude() + "";
		speed = loc.getSpeed() + "";
		accuracy = loc.getAccuracy() + "";
		altitute = loc.getAltitude() + "";
		bearing=loc.getBearing()+"";
	}

	public MyLocation(JSONObject loc)
	{
		lat = loc.optString(Mylocation.LATITUTE);
		lng = loc.optString(Mylocation.LONGITUTE);
		speed = loc.optString(Mylocation.SPEED);
		altitute = loc.optString(Mylocation.ALTITUTE);
		accuracy = loc.optString(Mylocation.ACCURACY);
		bearing=loc.optString(Mylocation.BEARING);
	}

	public String toJsonString()
	{
		JSONObject object = new JSONObject();
		try
		{
			object.put(Mylocation.LATITUTE, lat);

			object.put(Mylocation.LONGITUTE, lng);
			object.put(Mylocation.SPEED, speed);
			object.put(Mylocation.ACCURACY, accuracy);
			object.put(Mylocation.ALTITUTE, altitute);
			object.put(Mylocation.BEARING, bearing);
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object.toString();
	}

	/**
	 * @return the lat
	 */
	public String getLatitude()
	{
		return lat;
	}

	/**
	 * @return the lng
	 */
	public String getLongitude()
	{
		return lng;
	}

	/**
	 * @return the speed
	 */
	public String getSpeed()
	{
		return speed;
	}

	/**
	 * @return the altitute
	 */
	public String getAltitute()
	{
		return altitute;
	}

	/**
	 * @return the accuracy
	 */
	public String getAccuracy()
	{
		return accuracy;
	}
}
