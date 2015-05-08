package com.example.locationapp.services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.locationapp.GeoFence.GeoFenceManager;
import com.example.locationapp.GpsTracking.GPSTracker;
import com.example.locationapp.GpsTracking.GeoLocationStore;
import com.example.locationapp.GpsTracking.ILocationCallback;
import com.example.locationapp.Utils.Constants;
import com.example.locationapp.Utils.LocationSharedPreference;
import com.example.locationapp.Utils.LocationThreadPoolExecutor;
import com.example.locationapp.http.SendLocationToServer;
import com.example.locationapp.ui.LocationApp;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.Geofence;

public class LocationService extends Service implements ILocationCallback
{

	private static final String TAG = "LocationUpdate";

	private GPSTracker gpsTracker;

	private final LocationSharedPreference prefs = LocationSharedPreference.getInstance();

	private GeoFenceManager mGeoFenceManager = null;

	@Override
	public IBinder onBind(Intent intent)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		// TODO Auto-generated method stub
		return START_STICKY;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();

		IntentFilter filter = new IntentFilter(Constants.START_TRACKING);
		LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mStartTrackingBroadCastReceiver, filter);
		mGeoFenceManager = GeoFenceManager.getInstance();
		gpsTracker = GPSTracker.getInstance();
		gpsTracker.addListener(this);
		String dealerdetails = prefs.getData(Constants.DEALER_DETAILS, "");

		if (!TextUtils.isEmpty(dealerdetails))
		{
			gpsTracker.startTracking();
		}
	}

	private void startTracking()
	{
		gpsTracker.startTracking();
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}

	@Override
	public void onConnected()
	{
		gpsTracker.startRequestingLocationUpdate();
		// gpsTracker.addGeoFenceToList(gpsTracker.getGeoFence());
		// gpsTracker.addGeofence();

		String dealerdetails = prefs.getData(Constants.DEALER_DETAILS, "");

		addGeofence(dealerdetails);
	}

	private void addGeofence(String dealerdetails)
	{
		if (!TextUtils.isEmpty(dealerdetails))
		{
			try
			{
				JSONObject object = new JSONObject(dealerdetails);

				JSONArray array = object.getJSONArray(Constants.DEALER_DETAILS);

				JSONObject dealer = null;
				for (int i = 0; i < array.length(); i++)
				{
					dealer = array.getJSONObject(i);
					String name = dealer.getString(Constants.DEALER_NAME);
					double lat = Double.parseDouble(dealer.getString(Constants.DEALER_LOC).split(",")[0]);
					double lng = Double.parseDouble(dealer.getString(Constants.DEALER_LOC).split(",")[1]);
					String address = dealer.getString(Constants.DEALER_ADD);
					String id = dealer.getString(Constants.DEALER_ID);

					GeoLocationStore geostore = new GeoLocationStore(lat, lng, Constants.DEALER_RADIUS, id, Geofence.GEOFENCE_TRANSITION_DWELL);
					mGeoFenceManager.addGeoFenceToList(mGeoFenceManager.getGeoFence(geostore));
				}
				mGeoFenceManager.setUpGeofence();
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onDisconnected(ConnectionResult connectionResult)
	{
	}

	@Override
	public void onLocationChanged(Location location)
	{
		Log.i(TAG, "Location Changed" + location.toString() + location.getProvider());
		LocationThreadPoolExecutor.getInstance().execute(new SendLocationToServer(location));
	}

	private BroadcastReceiver mStartTrackingBroadCastReceiver = new BroadcastReceiver()
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{
			LocationSharedPreference.getInstance().saveData(Constants.DEALER_DETAILS, intent.getStringExtra("message"));
			LocationApp.getInstance().clearDealerData();
			mGeoFenceManager.removeAllGeofence();
			gpsTracker.stopTracking();
			LocationApp.getInstance().fillDealerMap();
			context.sendBroadcast(new Intent(Constants.REFRESH_DEALER_DATA));
			startTracking();

		}
	};

}
