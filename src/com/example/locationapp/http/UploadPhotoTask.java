package com.example.locationapp.http;

import java.io.File;
import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.util.Base64;
import android.util.Log;

import com.example.locationapp.GpsTracking.GPSTracker;
import com.example.locationapp.Utils.Constants;
import com.example.locationapp.Utils.Constants.PODTYPE;
import com.example.locationapp.Utils.ConsumerForEverythingElse;
import com.example.locationapp.Utils.LocationSharedPreference;
import com.example.locationapp.Utils.Utils;
import com.example.locationapp.database.LocationDB;
import com.example.locationapp.http.HTTPManager.IResponse;
import com.example.locationapp.http.RequestParams.RequestBuilder;
import com.example.locationapp.http.StringLocEntity.ProgressListener;

public class UploadPhotoTask implements Runnable, ProgressListener
{

	String dealerId;
	
	String filePath;
	
	String PodType;
	
	String grId;
	
	String encodedImage="";

	public UploadPhotoTask( String PodType,String dealerId,String filePath,String grID)
	{
		this.dealerId = dealerId;
		this.filePath=filePath;
		this.PodType=PodType;
		this.grId=grID;
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
			if(PODTYPE.POD_REJECTED!=PodType)
			{
			File file = new File(filePath);
			
			//defensive check
			
			if(!file.exists())
			{
				Log.d("UploadPhoto","File does not exists");
				LocationDB.getInstance().deleteFromPhotoTable(filePath);
				ConsumerForEverythingElse.getInstance().removeFromQueue(UploadPhotoTask.this);
				return;
			}
			Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
			byte[] arr = Utils.bitmapToBytes(bitmap, CompressFormat.JPEG, 75);
			encodedImage = Base64.encodeToString(arr, Base64.DEFAULT);
			}
			JSONObject body = new JSONObject();
			body.put(Constants.DEALER_ID, dealerId);
			body.put("img", encodedImage);
			body.put(Constants.POD_TYPE, PodType);
			body.put(Constants.GRID, grId);
			Location location=GPSTracker.getInstance().getLastKnownLocation();
			Log.d("locattt",location+"");
			if(location!=null)
			{
				body.put(Constants.LOCATION, location.getLatitude() + "," + location.getLongitude());
			}
			body.put(Constants.STS, System.currentTimeMillis()/1000);
			StringLocEntity entity = new StringLocEntity(body.toString(), this);
			String URL = Constants.HTTP_STRING + Constants.DEV_STAGING_HOST + Constants.UPLOAD_PHOTO;

			RequestParams params = new RequestBuilder().AddToken(true).setEntity(entity).setUrl(URL).build();
			HTTPManager.post(params, new IResponse()
			{

				@Override
				public void onSuccess(String response)
				{
					Log.d("UploadPhotoSuccess", response + "Time Taken" + (System.currentTimeMillis() - startTime) / 1000 + "");
					LocationDB.getInstance().deleteFromPhotoTable(filePath);
					ConsumerForEverythingElse.getInstance().removeFromQueue(UploadPhotoTask.this);
					
				}

				@Override
				public void onFailure(int i)
				{
					ConsumerForEverythingElse.getInstance().toggleNetworkState();
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
