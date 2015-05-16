package com.example.locationapp.http;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import android.location.Location;
import android.util.Log;

import com.example.locationapp.Utils.Constants;
import com.example.locationapp.http.HTTPManager.IResponse;
import com.example.locationapp.http.RequestParams.RequestBuilder;

public class SendLocationToServer implements Runnable
{
	Location location;

	public SendLocationToServer(Location loc)
	{
		this.location = loc;
	}

	@Override
	public void run()
	{
		String URL = Constants.HTTP_STRING + Constants.DEV_STAGING_HOST + Constants.UPDATE_LOC;

		JSONObject body = new JSONObject();
		try
		{
			body.put(Constants.LOCATION, location.getLatitude() + "," + location.getLongitude());
			body.put(Constants.STS, System.currentTimeMillis()/1000);

			StringEntity entity = new StringEntity(body.toString());

			RequestParams params = new RequestBuilder().setEntity(entity).AddToken(true).setUrl(URL).build();

			HTTPManager.post(params, new IResponse()
			{

				@Override
				public void onSuccess(String response)
				{
					Log.d("SendLocToServer", "Thread is " + Thread.currentThread() + "res is >>>" + response);

				}

				@Override
				public void onFailure(int i)
				{
					Log.w("SendLocToServer", "Request Failed: " + i);

				}
			});

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}
}
