package com.example.locationapp.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import com.example.locationapp.Utils.ConsumerForEverythingElse;
import com.example.locationapp.Utils.ConsumerLocation;
import com.example.locationapp.Utils.Utils;

public class ConnectionChangeReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent)
	{

		if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION))
		{
			if (Utils.isOnline())
			{
				Log.d("ConnectionChangeReceiver", "online");

			}
			else
			{
				Log.d("ConnectionChangeReceiver", "offline");
			}
			ConsumerLocation.getInstance().toggleNetworkState();
			ConsumerForEverythingElse.getInstance().toggleNetworkState();
		}

	}

}
