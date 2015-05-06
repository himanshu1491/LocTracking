package com.example.locationapp.GeoFence;

import java.util.ArrayList;

import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import com.example.locationapp.GpsTracking.GPSTracker;
import com.example.locationapp.GpsTracking.GeoLocationStore;
import com.example.locationapp.GpsTracking.GeofenceTransitionsIntentService;
import com.example.locationapp.ui.LocationApp;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationServices;

public class GeoFenceManager
{

	protected static final String TAG = "creating-and-monitoring-geofences";

	protected ArrayList<Geofence> mGeofenceList;

	public static final GeoFenceManager _instance = new GeoFenceManager();

	private GoogleApiClient mGoogleApiClient;

	private PendingIntent mGeofencePendingIntent;

	public static GeoFenceManager getInstance()
	{
		return _instance;
	}

	private GeoFenceManager()
	{
		mGeofenceList = new ArrayList<Geofence>();
		mGoogleApiClient = GPSTracker.getInstance().getGoogleApiClient();
	}

	public synchronized void setUpGeofence()
	{
		if (!mGoogleApiClient.isConnected())
		{
			Log.d("Geofence", "Error client not coonected");
		}
		LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, mGeofenceList, getGeofencePendingIntent()).setResultCallback(new ResultCallback<Status>()
		{

			@Override
			public void onResult(Status arg0)
			{
				Log.d(TAG, "status is " + arg0.toString());
			}
		});
	}

	private PendingIntent getGeofencePendingIntent()
	{
		// Reuse the PendingIntent if we already have it.
		if (mGeofencePendingIntent != null)
		{
			return mGeofencePendingIntent;
		}
		Intent intent = new Intent(LocationApp.getInstance().getApplicationContext(), GeofenceTransitionsIntentService.class);
		// We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
		// addGeofences() and removeGeofences().
		return PendingIntent.getService(LocationApp.getInstance().getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}

	public Geofence getGeoFence(GeoLocationStore store)
	{
		Geofence geofence = new Geofence.Builder().setCircularRegion(store.getLat(), store.getLng(), store.getRadius()).setTransitionTypes(store.getTransitionType())
				.setRequestId(store.getRequestId()).setExpirationDuration(store.getExpirationDuration()).setLoiteringDelay(30*1000).build();
		return geofence;
	}

	public synchronized void addGeoFenceToList(Geofence geofence)
	{
		mGeofenceList.add(geofence);
	}

}
