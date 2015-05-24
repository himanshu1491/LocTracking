package com.example.locationapp.http;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import android.location.Location;
import android.util.Log;

import com.example.locationapp.Utils.Constants;
import com.example.locationapp.Utils.ConsumerLocation;
import com.example.locationapp.data.MyLocation;
import com.example.locationapp.database.LocationDB;
import com.example.locationapp.http.HTTPManager.IResponse;
import com.example.locationapp.http.RequestParams.RequestBuilder;

public class SendLocationToServer implements Runnable
{
	private MyLocation location;
	public  long timeStamp;

	public SendLocationToServer(MyLocation loc,long timeStamp)
	{
		this.location = loc;
		this.timeStamp=timeStamp;
	}

	@Override
	public void run()
	{
		String URL = Constants.HTTP_STRING + Constants.DEV_STAGING_HOST + Constants.UPDATE_LOC;

		JSONObject body = new JSONObject();
		try
		{
			body.put(Constants.LOCATION, location.toJsonString());
			body.put(Constants.STS, timeStamp);

			StringEntity entity = new StringEntity(body.toString());

			RequestParams params = new RequestBuilder().setEntity(entity).AddToken(true).setUrl(URL).build();

			HTTPManager.post(params, new IResponse()
			{

				@Override
				public void onSuccess(String response)
				{
					Log.d("SendLocToServer", "Thread is " + Thread.currentThread() + "res is >>>" + response);
					LocationDB.getInstance().deleteFromLocationTable(location);
					ConsumerLocation.getInstance().removeFromQueue(SendLocationToServer.this);
				}

				@Override
				public void onFailure(int i)
				{
					Log.w("SendLocToServer", "Request Failed: " + i);
					ConsumerLocation.getInstance().toggleNetworkState();

				}
			});

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
}
