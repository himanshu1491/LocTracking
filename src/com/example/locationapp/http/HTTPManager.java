package com.example.locationapp.http;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.example.locationapp.Utils.Constants;
import com.example.locationapp.Utils.LocationSharedPreference;
import com.example.locationapp.Utils.Utils;

public class HTTPManager
{
	
	private static final String TAG="HTTPLOC";

	public static void post(RequestParams params, IResponse response)
	{
		HttpClient client = Utils.getClient();
		HttpPost post = new HttpPost(params.getUrl());
		post.setEntity(params.getEntity());
		post.setHeader("Content-type", "application/json");
		HttpResponse res = null;

		Log.d(TAG, "GOING TO EXECUTE " + params.getUrl());
		if (params.isAddToken())
		{
			addToken(post);
		}
		try
		{
			res = client.execute(post);
			if (res.getStatusLine().getStatusCode() != 200)
			{
				Log.w(TAG, "Request Failed: " + res.getStatusLine());
				response.onFailure(res.getStatusLine().getStatusCode());
				return;
			}
			String responseFromServer=EntityUtils.toString(res.getEntity());
			response.onSuccess(responseFromServer);
			Log.d(TAG, "SUCESS " + responseFromServer);
		}
		catch (ClientProtocolException e)
		{
			Log.d(TAG, "Failed " + e);
			e.printStackTrace();
			response.onFailure(1);
		}
		catch (IOException e)
		{
			Log.d(TAG, "Failed " + e);
			response.onFailure(1);
			e.printStackTrace();
		}
		finally
		{
			if (res != null)
			{
				try
				{
					res.getEntity().getContent().close();
				}
				catch (IllegalStateException e)
				{
					e.printStackTrace();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}

	}

	private static void addToken(HttpPost post)
	{
		String token = LocationSharedPreference.getInstance().getData(Constants.TOKEN, "");
		post.addHeader(Constants.AUTH_TOKEN, token);

	}

	public interface IResponse
	{
		public void onSuccess(String response);

		public void onFailure(int i);
	}

}
