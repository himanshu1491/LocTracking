package com.example.locationapp.Utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import android.util.Log;

import com.example.locationapp.http.SendLocationToServer;

public class ConsumerLocation extends Thread
{
//adb logcat | grep -ni -e  "ConnectionChangeReceiver" -e "ConsumerLocation" -e "LocationDB" 
	private static BlockingQueue<SendLocationToServer> queue=null;
	
	private AtomicBoolean isNetworkAvalable;
	
	private final String TAG="ConsumerLocation";
	
	public final static  ConsumerLocation _instance=new ConsumerLocation();

	private  ConsumerLocation()
	{
		queue = new LinkedBlockingQueue<SendLocationToServer>();
		isNetworkAvalable=new AtomicBoolean(Utils.isOnline());
	}
	
	public static ConsumerLocation getInstance()
	{
		return _instance;
	}

	public synchronized void addToQueue(SendLocationToServer runnable)
	{
		try
		{
			Log.d(TAG, "Added to queue"+runnable.timeStamp);
			queue.put(runnable);
			synchronized (ConsumerLocation.this)
			{
				notify();
			}
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	public void waitThisThread()
	{
		try
		{
			wait();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	public synchronized void removeFromQueue(SendLocationToServer runnable)
	{
		Log.d(TAG,"removing from queue"+runnable.timeStamp);
		queue.remove(runnable);
	}
	
	public void toggleNetworkState()
	{
		if(Utils.isOnline())
		{
			isNetworkAvalable.set(true);
			
			synchronized (ConsumerLocation.this)
			{
					notify();
			}			
		}
		else
		{
			isNetworkAvalable.set(false);
		}
	}
	
	@Override
	public void run()
	{
		while (true)
		{
			try
			{
				if(queue.isEmpty()||!isNetworkAvalable.get())
				{
					Log.d(TAG, "Going in wait condition");
					synchronized (ConsumerLocation.this)
					{
						wait();
					}
					Log.d(TAG,"Waking up");
					continue;
				}
				
				SendLocationToServer loc = queue.peek();
				if (loc != null)
				{
					loc.run();
				}
			}
			catch (InterruptedException e)
			{
				continue;
			}
			
		}
	}

	public void deleteAll()
	{
		queue.clear();
	}
	
}
