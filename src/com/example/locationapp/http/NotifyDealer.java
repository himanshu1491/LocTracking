package com.example.locationapp.http;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.locationapp.Utils.Constants;
import com.example.locationapp.Utils.ConsumerForEverythingElse;
import com.example.locationapp.database.LocationDB;
import com.example.locationapp.http.HTTPManager.IResponse;
import com.example.locationapp.http.RequestParams.RequestBuilder;

public class NotifyDealer implements Runnable
{

	String dealerId;

	public NotifyDealer(String dealerID)
	{
		this.dealerId = dealerID;
	}

	@Override
	public void run()
	{
		String URL = Constants.HTTP_STRING + Constants.DEV_STAGING_HOST + Constants.URL_NOTIFY_DEALER;

		JSONObject body = new JSONObject();
		StringEntity entity = null;
		try
		{
			body.put(Constants.DEALER_ID, dealerId);
			entity = new StringEntity(body.toString());
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}

		RequestParams params = new RequestBuilder().AddToken(true).setUrl(URL).setEntity(entity).build();

		HTTPManager.post(params, new IResponse()
		{

			@Override
			public void onSuccess(String response)
			{
				//Delete from DataBase;
				LocationDB.getInstance().deleteFromGeofenceTable(dealerId);
				ConsumerForEverythingElse.getInstance().removeFromQueue(NotifyDealer.this);
				
			}

			@Override
			public void onFailure(int i)
			{
				ConsumerForEverythingElse.getInstance().toggleNetworkState();
			}
		});
	}

}
