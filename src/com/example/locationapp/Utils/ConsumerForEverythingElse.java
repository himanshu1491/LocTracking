package com.example.locationapp.Utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConsumerForEverythingElse<T extends Runnable> extends ConsumerBase<T>
{
	

	public final static ConsumerForEverythingElse<Runnable> _instance = new ConsumerForEverythingElse<Runnable>();

	private ConsumerForEverythingElse()
	{
		queue = (BlockingQueue<T>) new LinkedBlockingQueue<Runnable>();
		isNetworkAvalable = new AtomicBoolean(Utils.isOnline());
		TAG="ConsumerForEverything";
	}

	public static ConsumerForEverythingElse<Runnable> getInstance()
	{
		return _instance;
	}
}
