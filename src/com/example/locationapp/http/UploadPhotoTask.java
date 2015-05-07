package com.example.locationapp.http;

import java.io.File;
import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.example.locationapp.Utils.Constants;
import com.example.locationapp.Utils.LocationSharedPreference;
import com.example.locationapp.Utils.Utils;
import com.example.locationapp.http.HTTPManager.IResponse;
import com.example.locationapp.http.RequestParams.RequestBuilder;
import com.example.locationapp.http.StringLocEntity.ProgressListener;

public class UploadPhotoTask implements Runnable, ProgressListener
{

	String dealerId;
	
	String filePath;

	public UploadPhotoTask(String dealerId,String filePath)
	{
		this.dealerId = dealerId;
		this.filePath=filePath;
	}

	@Override
	public void run()
	{
		sendViaHttpClient();
	};

	public void sendViaHttpClient()
	{
		final Long startTime = System.currentTimeMillis();
		try
		{
			File file = new File(filePath);
			Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
			byte[] arr = Utils.bitmapToBytes(bitmap, CompressFormat.JPEG, 75);
			String encodedImage = Base64.encodeToString(arr, Base64.DEFAULT);
			JSONObject body = new JSONObject();
			body.put("dealerId", dealerId);
			body.put("img", encodedImage);
			StringLocEntity entity = new StringLocEntity(body.toString(), this);
			String URL = Constants.HTTP_STRING + Constants.DEV_STAGING_HOST + Constants.UPLOAD_PHOTO;

			RequestParams params = new RequestBuilder().AddToken(true).setEntity(entity).setUrl(URL).build();
			HTTPManager.post(params, new IResponse()
			{

				@Override
				public void onSuccess(String response)
				{
					Log.d("UploadPhotoSuccess", response + "Time Taken" + (System.currentTimeMillis() - startTime) / 1000 + "");

				}

				@Override
				public void onFailure(int i)
				{

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

	@Override
	public void progress(long num)
	{
		Log.w("UploadPhotoSuccess", "Progress:" + num + "");
	}

}
