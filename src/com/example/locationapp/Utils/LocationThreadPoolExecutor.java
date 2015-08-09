package com.example.locationapp.Utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LocationThreadPoolExecutor
{
	private static LocationThreadPoolExecutor _instance=new LocationThreadPoolExecutor();
	
	ThreadPoolExecutor executor;

	private LocationThreadPoolExecutor()
	{
		executor= new ThreadPoolExecutor(2, 2, 1000, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	}

	public static LocationThreadPoolExecutor getInstance()
	{
		return _instance;
	}
	
	public void execute(Runnable runnable)
	{
		executor.execute(runnable);
	}

	public void shutDownExecutor()
	{
		executor.shutdown();
	}
	
	public BlockingQueue<Runnable> getBlockingQueue()
	{
		return executor.getQueue();
	}
 	
}
