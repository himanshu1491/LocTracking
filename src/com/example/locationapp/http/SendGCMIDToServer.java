package com.example.locationapp.http;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

import com.example.locationapp.Utils.Constants;
import com.example.locationapp.Utils.LocationSharedPreference;
import com.example.locationapp.Utils.Utils;
import com.example.locationapp.http.HTTPManager.IResponse;
import com.example.locationapp.http.RequestParams.RequestBuilder;

/**
 * 
 * @author himanshu Used to send the GCM ID to server
 */
public class SendGCMIDToServer implements Runnable
{

	LocationSharedPreference pref = LocationSharedPreference.getInstance();

	@Override
	public void run()
	{
		if (TextUtils.isEmpty(pref.getData(Constants.GCM_ID, "")))
		{
			return;
		}
		if (pref.getData(Constants.SENDTOSERVER, false))
		{
			return;
		}

		String URL = Constants.HTTP_STRING + Constants.DEV_STAGING_HOST + Constants.URL_GCM;
		JSONObject body = new JSONObject();
		try
		{
			body.put("gcmId", Utils.getGCMID());
			RequestParams params = new RequestBuilder().setEntity(new StringEntity(body.toString())).setUrl(URL).build();
			HTTPManager.post(params, new IResponse()
			{

				@Override
				public void onSuccess(String response)
				{
					Log.d("GCMIDTOSERVER", response);
					pref.saveData(Constants.SENDTOSERVER, true);
				}

				@Override
				public void onFailure(int statusCode)
				{
					Log.d("GCMIDTOSERVER","Failed with status code"+statusCode);
				}
			});

		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}

	}

}
