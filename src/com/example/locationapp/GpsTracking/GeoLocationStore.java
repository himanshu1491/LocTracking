package com.example.locationapp.GpsTracking;

import com.google.android.gms.location.Geofence;

public class GeoLocationStore
{
	private double lat;

	private double lng;

	private float radius;

	private String requestId;

	private int transitionType;

	private long expirationDuration = Geofence.NEVER_EXPIRE;

	/**
	 * @param lat
	 * @param lng
	 * @param radius
	 * @param requestId
	 * @param transitionType
	 */
	public GeoLocationStore(double lat, double lng, float radius, String requestId, int transitionType)
	{
		this.lat = lat;
		this.lng = lng;
		this.radius = radius;
		this.requestId = requestId;
		this.transitionType = transitionType;
	}

	/**
	 * @return the lat
	 */
	public double getLat()
	{
		return lat;
	}

	/**
	 * @return the lng
	 */
	public double getLng()
	{
		return lng;
	}

	/**
	 * @return the radius
	 */
	public float getRadius()
	{
		return radius;
	}

	/**
	 * @return the requestId
	 */
	public String getRequestId()
	{
		return requestId;
	}

	/**
	 * @return the transitionType
	 */
	public int getTransitionType()
	{
		return transitionType;
	}

	/**
	 * @return the expirationDuration
	 */
	public long getExpirationDuration()
	{
		return expirationDuration;
	}

	/**
	 * @param expirationDuration
	 *            the expirationDuration to set
	 */
	public void setExpirationDuration(long expirationDuration)
	{
		this.expirationDuration = expirationDuration;
	}

}
