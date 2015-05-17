package com.example.locationapp.services;

import java.util.ArrayList;
import java.util.List;

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
import com.example.locationapp.Utils.Constants.GEOFENCESTATUS;
import com.example.locationapp.Utils.ConsumerForEverythingElse;
import com.example.locationapp.Utils.ConsumerLocation;
import com.example.locationapp.Utils.LocationSharedPreference;
import com.example.locationapp.Utils.LocationThreadPoolExecutor;
import com.example.locationapp.data.Dealer;
import com.example.locationapp.database.LocationDB;
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
		//String dealerdetails = prefs.getData(Constants.DEALER_DETAILS, "");

		if (LocationSharedPreference.getInstance().getData(Constants.SYSTEM_ON,false))
		{
			ConsumerLocation.getInstance().start();
			ConsumerForEverythingElse.getInstance().start();
			gpsTracker.startTracking();
			LocationThreadPoolExecutor.getInstance().execute(new Runnable()
			{
				
				@Override
				public void run()
				{
					LocationDB.getInstance().fetchAllLocationAndSendToServer();
					LocationDB.getInstance().fetchAllGeofencesAndSendToServer();
					LocationDB.getInstance().fetchAllPhotosAndSendToServer();
				}
			});
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
		if (!LocationSharedPreference.getInstance().getData(Constants.SYSTEM_ON,false)&&!TextUtils.isEmpty(dealerdetails))
		{
			ArrayList<GeoLocationStore> list=null;;
			try
			{
				JSONObject object = new JSONObject(dealerdetails);

				JSONArray array = object.getJSONArray(Constants.DEALER_DETAILS);
				
				JSONObject dealer = null;
				list=new ArrayList<GeoLocationStore>(array.length());
				for (int i = 0; i < array.length(); i++)
				{
					dealer = array.getJSONObject(i);
					String name = dealer.getString(Constants.DEALER_NAME);
					double lat = Double.parseDouble(dealer.getString(Constants.DEALER_LOC).split(",")[0]);
					double lng = Double.parseDouble(dealer.getString(Constants.DEALER_LOC).split(",")[1]);
					String address = dealer.getString(Constants.DEALER_ADD);
					String id = dealer.getString(Constants.DEALER_ID);
					int radius=dealer.optInt(Constants.DEALER_RADIUS,Constants.DEALER_RADIUS_VALUE);
					GeoLocationStore geostore = new GeoLocationStore(lat, lng, radius, id, Geofence.GEOFENCE_TRANSITION_DWELL);
					list.add(geostore);
					mGeoFenceManager.addGeoFenceToList(mGeoFenceManager.getGeoFence(geostore));
				}
				mGeoFenceManager.setUpGeofence();
				addGeoFenceToDB(list);
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			List<GeoLocationStore> list = LocationDB.getInstance().getAllGeofencesWhichNotEntered();
			if (list == null || list.isEmpty())
			{
				return;
			}

			for (GeoLocationStore store : list)
			{
				Log.d("geofences", "Going to add to geofence with id " + store.getRequestId());
				mGeoFenceManager.addGeoFenceToList(mGeoFenceManager.getGeoFence(store));
			}
			mGeoFenceManager.setUpGeofence();
		}
	}

	private void addGeoFenceToDB(final ArrayList<GeoLocationStore> list)
	{
		LocationThreadPoolExecutor.getInstance().execute(new Runnable()
		{
			
			@Override
			public void run()
			{
				for(GeoLocationStore geoLocationStore:list)
				{
					LocationDB.getInstance().insertIntoGeofenceTable(geoLocationStore,GEOFENCESTATUS.GEOFENCE_NOTENTERED);
				}
				
			}
		});
		
		
	}

	@Override
	public void onDisconnected(ConnectionResult connectionResult)
	{
	}

	@Override
	public void onLocationChanged(final Location location)
	{
		Log.i(TAG, "Location Changed" + location.toString() + location.getProvider());
	
		//inserting into DB first;
		LocationThreadPoolExecutor.getInstance().execute(new Runnable()
		{

			@Override
			public void run()
			{
				LocationDB.getInstance().insertIntoLocationTable(location);
				ConsumerLocation.getInstance().addToQueue(new SendLocationToServer(location, System.currentTimeMillis() / 1000));
			}
		});
		
	}

	private BroadcastReceiver mStartTrackingBroadCastReceiver = new BroadcastReceiver()
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{
			LocationSharedPreference.getInstance().saveData(Constants.SYSTEM_ON, false);
			LocationSharedPreference.getInstance().saveData(Constants.DEALER_DETAILS, intent.getStringExtra("message"));
		
			clearAllApplicationData();
			gpsTracker.stopTracking();
			insertIntoDealerDB(intent.getStringExtra("message"));
			LocationApp.getInstance().fillDealerMap();
			context.sendBroadcast(new Intent(Constants.REFRESH_DEALER_DATA));
			startTracking();

		}

		private void clearAllApplicationData()
		{
			LocationApp.getInstance().clearDealerData();
			LocationDB.getInstance().deleteAll();
			mGeoFenceManager.removeAllGeofence();
			//ConsumerForEverythingElse.getInstance().deleteAll();
			//ConsumerLocation.getInstance().deleteAll();
			
			
		}
	};

	public void insertIntoDealerDB(String dealerdetails)
	{
		JSONObject object = null;
		JSONArray array = null;
		try
		{
			object = new JSONObject(dealerdetails);

			array = object.getJSONArray(Constants.DEALER_DETAILS);

			JSONObject dealer = null;
			for (int i = 0; i < array.length(); i++)
			{
				dealer = array.getJSONObject(i);

				Dealer dea = new Dealer(dealer);
				LocationDB.getInstance().insertIntoDealerTable(dea);
			}
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
