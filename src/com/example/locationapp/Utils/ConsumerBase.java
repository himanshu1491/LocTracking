package com.example.locationapp.Utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import android.util.Log;

public abstract class ConsumerBase<T extends Runnable> extends Thread
{

	protected  BlockingQueue<T> queue=null;
	
	protected AtomicBoolean isNetworkAvalable;
	
	protected  String TAG="ConsumerBase";

	public synchronized void addToQueue(T runnable)
	{
		try
		{
			Log.d(TAG, "Added to queue"+runnable);
			queue.put(runnable);
			synchronized (ConsumerBase.this)
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
	
	public synchronized void removeFromQueue(T runnable)
	{
		Log.d(TAG,"removing from queue"+runnable);
		queue.remove(runnable);
	}
	
	public void toggleNetworkState()
	{
		if(Utils.isOnline())
		{
			isNetworkAvalable.set(true);
			
			synchronized (ConsumerBase.this)
			{
					Log.d(TAG,"calling notify");
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
					synchronized (ConsumerBase.this)
					{
						wait();
					}
					Log.d(TAG,"Waking up");
					continue;
				}
				
				T loc = queue.peek();
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
