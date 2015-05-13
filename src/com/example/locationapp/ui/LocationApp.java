package com.example.locationapp.ui;

import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.example.locationapp.Utils.Constants;
import com.example.locationapp.Utils.LocationSharedPreference;
import com.example.locationapp.Utils.Utils;
import com.example.locationapp.data.Dealer;
import com.example.locationapp.database.LocationDB;
import com.example.locationapp.services.LocationService;

public class LocationApp extends Application
{

	Context context;

	private static LocationApp _instance;

	ConcurrentHashMap<String, Dealer> dealerMap = new ConcurrentHashMap<String, Dealer>();

	private LocationSharedPreference prefs;

	@Override
	public void onCreate()
	{
		super.onCreate();
		context = getApplicationContext();
		_instance = this;
		ComponentName service = startService(new Intent(this, LocationService.class));

		if (service != null && service.getClassName().equals(LocationService.class.getName()))
		{
			// Service started
			Log.i("LocationUpdate", "Connected");
		}
		else
		{
			Log.i("LocationUpdate", "Disconnected");
		}
		prefs = LocationSharedPreference.getInstance();
		Utils.registerGCMID(getApplicationContext());
		fillDealerMap();
		Log.d("dealer",getDealerDetails("23")+"");
	}

	public static LocationApp getInstance()
	{
		return _instance;
	}

	public void fillDealerMap()
	{
//		String dealerdetails = prefs.getData(Constants.DEALER_DETAILS, "");
//
//		if (!TextUtils.isEmpty(dealerdetails))
//		{
//			try
//			{
//				JSONObject object = new JSONObject(dealerdetails);
//
//				JSONArray array = object.getJSONArray(Constants.DEALER_DETAILS);
//
//				JSONObject dealer = null;
//				for (int i = 0; i < array.length(); i++)
//				{
//					dealer = array.getJSONObject(i);
//
//					Dealer dea = new Dealer(dealer);
//					putDealerDetailsInMap(dea);
//				}
//			}
//			catch (JSONException e)
//			{
//				e.printStackTrace();
//			}
//		}
		
		LocationDB.getInstance().getAllDealerData();
	}

	public Dealer getDealerDetails(String dealerid)
	{
		if (dealerMap.containsKey((dealerid)))
		{
			return dealerMap.get(dealerid);
		}
		return null;
	}

	public void putDealerDetailsInMap(Dealer dea)
	{
		dealerMap.put(dea.getId(), dea);
	}

	public void clearDealerData()
	{
		if (dealerMap != null && dealerMap.size() > 0)
		{
			dealerMap.clear();
		}

	}
}
