package com.example.locationapp.GpsTracking;

import com.google.android.gms.common.ConnectionResult;

import android.location.Location;

public interface ILocationCallback
{

	public void onConnected();

	public void onDisconnected(ConnectionResult arg0);

	public void onLocationChanged(Location location);
}
