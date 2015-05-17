package com.example.locationapp.Utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import android.util.Log;

public class ConsumerForEverythingElse extends Thread
{
	private static BlockingQueue<Runnable> queue = null;

	private AtomicBoolean isNetworkAvalable;

	private final String TAG = "ConsumerForEverything";

	public final static ConsumerForEverythingElse _instance = new ConsumerForEverythingElse();

	private ConsumerForEverythingElse()
	{
		queue = new LinkedBlockingQueue<Runnable>();
		isNetworkAvalable = new AtomicBoolean(Utils.isOnline());
	}

	public static ConsumerForEverythingElse getInstance()
	{
		return _instance;
	}

	public synchronized void addToQueue(Runnable runnable)
	{
		try
		{
			Log.d(TAG, "Added to queue" + runnable.hashCode());
			queue.put(runnable);
			synchronized (ConsumerForEverythingElse.this)
			{
				notify();
			}
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	public synchronized void removeFromQueue(Runnable runnable)
	{
		Log.d(TAG,"removing from queue"+runnable.hashCode());
		queue.remove(runnable);
	}
	
	public void toggleNetworkState()
	{
		if (Utils.isOnline())
		{
			isNetworkAvalable.set(true);

			synchronized (ConsumerForEverythingElse.this)
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
				if (queue.isEmpty() || !isNetworkAvalable.get())
				{
					Log.d(TAG, "Going in wait condition");
					synchronized (ConsumerForEverythingElse.this)
					{
						wait();
					}
					Log.d(TAG, "Waking up");
					continue;
				}

				Runnable runnable = queue.peek();
				if (runnable != null)
				{
					runnable.run();
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
