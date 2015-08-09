package com.example.locationapp.services;

import android.content.Context;

import com.google.android.gcm.GCMBroadcastReceiver;

public class GCMBroadcastReceiverL extends GCMBroadcastReceiver
{
	@Override
	protected String getGCMIntentServiceClassName(Context context)
	{
		// TODO Auto-generated method stub
		return "com.example.locationapp.services.GCMIntentService";
	}
}
