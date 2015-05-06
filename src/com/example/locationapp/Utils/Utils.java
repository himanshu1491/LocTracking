package com.example.locationapp.Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.example.locationapp.data.Dealer;
import com.example.locationapp.http.SendGCMIDToServer;
import com.google.android.gcm.GCMRegistrar;

public class Utils
{

	private static CloseableHttpClient client = null;

	public static synchronized HttpClient getClient()
	{
		if (client != null)
		{
			return client;
		}
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(10);
		client = HttpClients.custom().setConnectionManager(cm).build();
		return client;

	}

	public static String getResponseString(HttpResponse response)
	{
		try
		{
			return EntityUtils.toString(response.getEntity());
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}

	public static void registerGCMID(Context context)
	{
		try
		{
			GCMRegistrar.checkDevice(context);
			GCMRegistrar.checkManifest(context);

			if (TextUtils.isEmpty(GCMRegistrar.getRegistrationId(context)))
			{
				GCMRegistrar.register(context, Constants.GCM_PUSH_ID);
			}
			else
			{
				Log.d("GCMID", "Already Registered...>>>" + GCMRegistrar.getRegistrationId(context));

			}
			sendGCMIDToServer(context);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void sendGCMIDToServer(Context context)
	{
		LocationThreadPoolExecutor.getInstance().execute(new SendGCMIDToServer());
	}

	public static ArrayList<Dealer> getDealerData()
	{
		ArrayList<Dealer> dealerdata = new ArrayList<Dealer>();

		for (int i = 0; i < 5; i++)
		{
			dealerdata.add(new Dealer("Hi>>>" + i + "", 23.56, 77.35, "234", "gurgoan"));
		}
		return dealerdata;
	}

	public static Intent getNativeCameraAppIntent(boolean getFullSizedCaptureResult, File destination)
	{
		Intent pickIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (getFullSizedCaptureResult)
		{
			LocationSharedPreference.getInstance().saveData(Constants.FILE_PATH_CAMERA, destination.toString());
			pickIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destination));
		}
		return pickIntent;
	}

	public static File createNewFile(String prefix)
	{
		File selectedDir = new File(Utils.getFileParent());
		if (!selectedDir.exists())
		{
			if (!selectedDir.mkdirs())
			{
				return null;
			}
		}
		String fileName = prefix + Utils.getOriginalFile();
		File selectedFile = new File(selectedDir.getPath() + File.separator + fileName);
		return selectedFile;
	}

	private static String getOriginalFile()
	{
		String timeStamp = Long.toString(System.currentTimeMillis());
		String orgFileName = "IMG_" + timeStamp + ".jpg";
		return orgFileName;
	}

	private static String getFileParent()
	{
		StringBuilder path = new StringBuilder(Constants.MEDIA_DIRECTORY_ROOT);
		return path.toString();
	}

	public static String getGCMID()
	{
		return LocationSharedPreference.getInstance().getData(Constants.GCM_ID, null);
	}

	public static boolean compressImage(String srcFilePath, String destFilePath)
	{
		InputStream src;
		Bitmap tempBmp = null;
		tempBmp = scaleDownBitmap(srcFilePath, Constants.MAX_DIMENSION_MEDIUM_FULL_SIZE_PX, Constants.MAX_DIMENSION_MEDIUM_FULL_SIZE_PX, Bitmap.Config.RGB_565, true, false);
		try
		{
			if (tempBmp != null)
			{
				byte[] fileBytes = Utils.bitmapToBytes(tempBmp, Bitmap.CompressFormat.JPEG, 75);
				tempBmp.recycle();
				src = new ByteArrayInputStream(fileBytes);
			}
			else
			{
				src = new FileInputStream(new File(srcFilePath));
			}

			FileOutputStream dest = new FileOutputStream(new File(destFilePath));

			byte[] buffer = new byte[Constants.MAX_BUFFER_SIZE_KB * 1024];
			int len;

			while ((len = src.read(buffer)) > 0)
			{
				dest.write(buffer, 0, len);
			}
			dest.flush();
			dest.getFD().sync();
			src.close();
			dest.close();
			return true;
		}
		catch (FileNotFoundException e)
		{
			Log.e("Utils", "File not found while copying", e);
			return false;
		}
		catch (IOException e)
		{
			Log.e("Utils", "Error while reading/writing/closing file", e);
			return false;
		}
		catch (Exception ex)
		{
			Log.e("Utils", "WTF Error while reading/writing/closing file", ex);
			return false;
		}
	}

	public static Bitmap scaleDownBitmap(String filename, int reqWidth, int reqHeight, Bitmap.Config config, boolean finResMoreThanReq, boolean scaleUp)
	{
		Bitmap unscaledBitmap = decodeSampledBitmapFromFile(filename, reqWidth, reqHeight, config);

		if (unscaledBitmap == null)
		{
			return null;
		}

		Bitmap small = createScaledBitmap(unscaledBitmap, reqWidth, reqHeight, config, true, finResMoreThanReq, scaleUp);

		if (unscaledBitmap != small)
		{
			unscaledBitmap.recycle();
		}

		return small;

	}

	public static Bitmap decodeSampledBitmapFromFile(String filename, int reqWidth, int reqHeight, Bitmap.Config con)
	{
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		decodeFile(filename, options);

		options.inPreferredConfig = con;

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// If we're running on Honeycomb or newer, try to use inBitmap
		// if (Utils.hasHoneycomb())
		// {
		// addInBitmapOptions(options, cache);
		// }

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		Bitmap result = null;
		try
		{
			result = decodeFile(filename, options);
		}
		catch (IllegalArgumentException e)
		{
			result = decodeFile(filename, options);
		}
		catch (Exception e)
		{
			Log.e("abc", "Exception in decoding Bitmap from file: ", e);
		}
		return result;
	}

	public static Bitmap decodeFile(String path, BitmapFactory.Options opt)
	{
		Bitmap b = null;
		try
		{
			b = BitmapFactory.decodeFile(path, opt);
		}
		catch (OutOfMemoryError e)
		{
			Log.wtf("abc", "Out of Memory");

			System.gc();

			try
			{
				b = BitmapFactory.decodeFile(path, opt);
			}
			catch (OutOfMemoryError ex)
			{
				Log.wtf("abc", "Out of Memory even after System.gc");
			}
			catch (Exception exc)
			{
				Log.e("abc", "Exception in decodeFile : ", exc);
			}
		}
		catch (Exception e)
		{
			Log.e("abc", "Exception in decodeFile : ", e);
		}
		return b;
	}

	private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
	{
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth)
		{

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth)
			{
				inSampleSize *= 2;
			}

		}
		return inSampleSize;
	}

	public static Bitmap createScaledBitmap(Bitmap unscaledBitmap, int reqWidth, int reqHeight, Bitmap.Config config, boolean filter, boolean finResMore, boolean scaleUp)
	{
		if (unscaledBitmap == null)
		{
			return null;
		}

		if (scaleUp || reqHeight < unscaledBitmap.getHeight() && reqWidth < unscaledBitmap.getWidth())
		{
			Rect srcRect = new Rect(0, 0, unscaledBitmap.getWidth(), unscaledBitmap.getHeight());

			Rect reqRect = calculateReqRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), reqWidth, reqHeight, finResMore);

			Bitmap scaledBitmap = createBitmap(reqRect.width(), reqRect.height(), config);

			if (scaledBitmap == null)
			{
				return null;
			}

			Canvas canvas = new Canvas(scaledBitmap);
			Paint p = new Paint();
			p.setFilterBitmap(filter);
			canvas.drawBitmap(unscaledBitmap, srcRect, reqRect, p);
			return scaledBitmap;
		}
		else
		{
			return unscaledBitmap;
		}
	}

	private static Rect calculateReqRect(int srcWidth, int srcHeight, int reqWidth, int reqHeight, boolean finResMore)
	{
		final float srcAspect = (float) srcWidth / (float) srcHeight;
		final float dstAspect = (float) reqWidth / (float) reqHeight;

		if (finResMore)
		{
			if (srcAspect > dstAspect)
			{
				return new Rect(0, 0, (int) (reqHeight * srcAspect), reqHeight);
			}
			else
			{
				return new Rect(0, 0, reqWidth, (int) (reqWidth / srcAspect));
			}
		}
		else
		{
			if (srcAspect > dstAspect)
			{
				return new Rect(0, 0, reqWidth, (int) (reqWidth / srcAspect));
			}
			else
			{
				return new Rect(0, 0, (int) (reqHeight * srcAspect), reqHeight);
			}
		}
	}

	public static Bitmap createBitmap(int width, int height, Config con)
	{
		Bitmap b = null;
		try
		{
			b = Bitmap.createBitmap(width, height, con);
		}
		catch (OutOfMemoryError e)
		{
			Log.wtf("abc", "Out of Memory");

			System.gc();

			try
			{
				b = Bitmap.createBitmap(width, height, con);
			}
			catch (OutOfMemoryError ex)
			{
				Log.wtf("abc", "Out of Memory even after System.gc");
			}
			catch (Exception exc)
			{
				Log.e("abc", " Exception in createBitmap : ", exc);
			}
		}
		catch (Exception e)
		{
			Log.e("abc", "Exception in createBitmap : ", e);
		}
		return b;
	}

	public static byte[] bitmapToBytes(Bitmap bitmap, Bitmap.CompressFormat format, int quality)
	{
		if (bitmap == null)
		{
			byte[] b = new byte[] { 0 };
			return b;
		}
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		bitmap.compress(format, quality, bao);
		return bao.toByteArray();
	}

	public static String getGeofenceSampleData() throws JSONException
	{

		JSONObject root = new JSONObject();
		JSONArray array = new JSONArray();
		try
		{
			JSONObject data = new JSONObject();
			data.put(Constants.DEALER_ADD, "asd");
			data.put(Constants.DEALER_ID, "23");

			data.put(Constants.DEALER_LOC, "28.481713, 77.049392");
			data.put(Constants.DEALER_NAME, "Himanshu");
			array.put(data);

			data = new JSONObject();
			data.put(Constants.DEALER_ADD, "asddfsf");
			data.put(Constants.DEALER_ID, "24");

			data.put(Constants.DEALER_LOC, "28.467279, 77.059110");
			data.put(Constants.DEALER_NAME, "Himanshu Khandelwal");
			array.put(data);

			root.put(Constants.DEALER_DETAILS, array);
		}
		catch (Exception e)
		{

		}
		return root.toString();

	}
}
