package com.example.locationapp.Utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.example.locationapp.http.SendLocationToServer;

public class ConsumerLocation<T extends Runnable> extends ConsumerBase<T>
{
//adb logcat | grep -ni -e  "ConnectionChangeReceiver" -e "ConsumerLocation" -e "LocationDB" 
	
	public final static  ConsumerLocation<SendLocationToServer> _instance=new ConsumerLocation<SendLocationToServer>();

	private  ConsumerLocation()
	{
		queue = (BlockingQueue<T>) new LinkedBlockingQueue<SendLocationToServer>();
		isNetworkAvalable=new AtomicBoolean(Utils.isOnline());
		 TAG="ConsumerLocation";
	}
	
	public static ConsumerLocation<SendLocationToServer> getInstance()
	{
		return _instance;
	}
	
	
}
