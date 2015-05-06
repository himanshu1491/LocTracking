package com.example.locationapp.services;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.locationapp.Utils.Constants;
import com.example.locationapp.Utils.LocationSharedPreference;
import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService
{

	public GCMIntentService()
	{
		super(Constants.GCM_PUSH_ID);
	}

	@Override
	protected void onMessage(Context context, Intent intent)
	{
		Log.d("GCMID", "ONMESSAGE" + intent.getStringExtra("message"));
		Intent in = new Intent(Constants.START_TRACKING);
		in.putExtra("message", intent.getStringExtra("message"));
		LocalBroadcastManager.getInstance(context).sendBroadcast(in);
	}

	@Override
	protected void onError(Context context, String errorId)
	{
		Log.d("GCMID", errorId);

	}

	@Override
	protected void onRegistered(Context context, String registrationId)
	{
		Log.d("GCMID", registrationId);
		LocationSharedPreference.getInstance().saveData(Constants.GCM_ID, registrationId);

	}

	@Override
	protected void onUnregistered(Context context, String registrationId)
	{
		Log.d("GCMID", registrationId);

	}

}
