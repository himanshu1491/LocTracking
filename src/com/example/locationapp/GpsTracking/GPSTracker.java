package com.example.locationapp.GpsTracking;

import java.util.ArrayList;
import java.util.Collections;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.example.locationapp.Utils.Constants;
import com.example.locationapp.Utils.LocationSharedPreference;
import com.example.locationapp.ui.LocationApp;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class GPSTracker implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener
{

	private GoogleApiClient mGoogleApiClient;

	private static final String TAG = "LocationUpdate";

	private LocationRequest mLocationRequest;
	
	static ArrayList<ILocationCallback> listeners = new ArrayList<ILocationCallback>();

	public static final GPSTracker gpsTracter = new GPSTracker();
	
	LocationSharedPreference prefs=LocationSharedPreference.getInstance();

	

	private GPSTracker()
	{
		inititializeTracking();
	}

	public GoogleApiClient getGoogleApiClient()
	{
		return mGoogleApiClient;
	}

	public static GPSTracker getInstance()
	{
		return gpsTracter;
	}

	public void addListener(ILocationCallback callback)
	{
		listeners.add(callback);
	}

	private void inititializeTracking()
	{
		Collections.synchronizedCollection(listeners);
		mGoogleApiClient = new GoogleApiClient.Builder(LocationApp.getInstance().getApplicationContext()).addConnectionCallbacks(this).addOnConnectionFailedListener(this)
				.addApi(LocationServices.API).build();
		mLocationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(prefs.getData(Constants.LOCATION_SYNC_TIME,Constants.LOCATION_INTERVAL)*1000) // 1 seconds, in milliseconds
				.setFastestInterval(prefs.getData(Constants.LOCATION_SYNC_TIME,Constants.LOCATION_INTERVAL)*1000);
	}

	public void startTracking()
	{
		if (!mGoogleApiClient.isConnected())
		{
			mGoogleApiClient.connect();
		}
	}

	@Override
	public void onLocationChanged(Location location)
	{
		for (ILocationCallback callback : listeners)
		{
			callback.onLocationChanged(location);
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0)
	{
		for (ILocationCallback callback : listeners)
		{
			callback.onDisconnected(arg0);
		}
		Log.i(TAG, "Location services Failed.");
	}

	@Override
	public void onConnected(Bundle arg0)
	{
		Log.i(TAG, "Location services connected.");
		for (ILocationCallback callback : listeners)
		{
			callback.onConnected();
		}
	}

	/**
	 * Should be called after the GoogleAPIClient has been connected.
	 */
	public void startRequestingLocationUpdate()
	{
		LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
	}

	@Override
	public void onConnectionSuspended(int arg0)
	{
		mGoogleApiClient.connect();
		Log.i(TAG, "Location services Suspended.");
	}

	public void stopTracking()
	{
		if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
		{
			LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
			mGoogleApiClient.disconnect();
		}
	}

	public Location getLastKnownLocation()
	{
		Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
		return location;
	}

	public void removeListener(ILocationCallback callback)
	{
		if (listeners.contains(callback))
		{
			listeners.remove(callback);
		}
	}

}
