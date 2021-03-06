package com.example.locationapp.database;

import static com.example.locationapp.Utils.Constants.DatabaseConstants.DEALER_DATA;
import static com.example.locationapp.Utils.Constants.DatabaseConstants._ID;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.support.v4.database.DatabaseUtilsCompat;
import android.util.Log;

import com.example.locationapp.GpsTracking.GeoLocationStore;
import com.example.locationapp.Utils.Constants;
import com.example.locationapp.Utils.Constants.DatabaseConstants;
import com.example.locationapp.Utils.Constants.GEOFENCESTATUS;
import com.example.locationapp.Utils.ConsumerForEverythingElse;
import com.example.locationapp.Utils.ConsumerLocation;
import com.example.locationapp.data.Dealer;
import com.example.locationapp.data.MyLocation;
import com.example.locationapp.http.NotifyDealer;
import com.example.locationapp.http.SendLocationToServer;
import com.example.locationapp.http.UploadPhotoTask;
import com.example.locationapp.ui.LocationApp;

public class LocationDB extends SQLiteOpenHelper
{

	public static volatile LocationDB _instance;

	private SQLiteDatabase mmDb = null;
	
	private static final String TAG="LocationDB";

	private LocationDB()
	{
		super(LocationApp.getInstance().getApplicationContext(), DatabaseConstants.NAME, null, DatabaseConstants.VERSION);
		mmDb = getWritableDatabase();
	}

	public  static LocationDB getInstance()
	{
		if (_instance == null)
		{
			synchronized (LocationDB.class)
			{
				if (_instance == null)
				{
					_instance = new LocationDB();
				}
			}
		}
		return _instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{	
		mmDb = db;
		String sql = DatabaseConstants.CREATE_TABLE +  DatabaseConstants.DEALER_TABLE  +  " ( "  + _ID + " PRIMARY KEY ," + DEALER_DATA + " TEXT "   +  ")" ;
		mmDb.execSQL(sql);
		sql=DatabaseConstants.CREATE_TABLE + DatabaseConstants.LOCATION_TABLE + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + DatabaseConstants.LOCATION_COORDINATE + " TEXT, "+ DatabaseConstants.STS + " TEXT, " + DatabaseConstants.LOCATION_SERVER_SYNCED_STATUS + " INTEGER DEFAULT 0" + ")" ;
		mmDb.execSQL(sql);
		sql=DatabaseConstants.CREATE_TABLE + DatabaseConstants.GEOFENCE_TABLE +" ( " + DatabaseConstants.GEOFENCE_DEALER_ID + " STRING PRIMARY KEY , " 
			+ DatabaseConstants.GEOFENCE_COORDINATE + " STRING ,"
			+ DatabaseConstants.GEOFENCE_EXPIRATION_TIME +" STRING, "
			+ DatabaseConstants.GEOFENCE_LOITERRING_TIME +" STRING, "
			+ DatabaseConstants.GEOFENCE_RADIUS + " INTEGER, "
			+ DatabaseConstants.GEOFENCE_TRANSISTION_TYPE + " INTEGER DEFAULT 4, "//GEOFENCE_TRANSITION_DWELL
			+ DatabaseConstants.GEOFENCE_STATUS +" INTEGER"
			+" ) ";
		mmDb.execSQL(sql);
		sql=DatabaseConstants.CREATE_TABLE + DatabaseConstants.PHOTO_TABLE +" ( "+ DatabaseConstants.PHOTO_FILE_PATH+ " STRING PRIMARY KEY, " + DatabaseConstants.PHOTO_DEALER_ID + " STRING ,"
				+ DatabaseConstants.PHOTO_POD_TYPE + " STRING , " + DatabaseConstants.STS+ " STRING ," + Constants.GRID + " STRING "+ ")";
		mmDb.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		
	}

	public void insertIntoDealerTable(Dealer dealer)
	{
		ContentValues cv = new ContentValues();
		cv.put(_ID, dealer.getId());
		cv.put(DEALER_DATA, dealer.toJSONString());
		long val=mmDb.insertWithOnConflict(DatabaseConstants.DEALER_TABLE, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
		Log.d(TAG,"Inserted with dealer table  row id = " +val+"");
	}
	
	public void updateDealerData(String dealerId, int status)
	{
		ContentValues cv = new ContentValues();
		cv.put(DatabaseConstants.DEALER_STATUS, status);
		mmDb.update(DatabaseConstants.DEALER_TABLE, cv, DatabaseConstants.DEALER_STATUS + " =? ", new String[] { dealerId });
	}

	public void deleteFromDealerTable(Dealer dealer)
	{
		mmDb.delete(DatabaseConstants.DEALER_TABLE, _ID + " = ", new String[] { dealer.getId() });
	}

	public void getAllDealerData()
	{
		String sql = "SELECT * FROM " + DatabaseConstants.DEALER_TABLE;
		try
		{
			Cursor cursor = mmDb.rawQuery(sql, null);
			if (cursor.moveToFirst())
			{
				do
				{
					String dealerData = cursor.getString(cursor.getColumnIndex(DEALER_DATA));
					LocationApp.getInstance().putDealerDetailsInMap(new Dealer(new JSONObject(dealerData)));
				}
				while (cursor.moveToNext());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void deleteAllDealerData()
	{
		long val=mmDb.delete(DatabaseConstants.DEALER_TABLE, null, null);
		Log.d(TAG,"deletion  form  Dealer table total rows  "+val+"");
	}
	
	public void insertIntoLocationTable(MyLocation loc)
	{
		ContentValues cv = new ContentValues();
		cv.put(DatabaseConstants.LOCATION_COORDINATE, loc.toJsonString());
		cv.put(DatabaseConstants.STS, System.currentTimeMillis() / 1000);
		cv.put(DatabaseConstants.LOCATION_SERVER_SYNCED_STATUS, 0);
		long val=mmDb.insertWithOnConflict(DatabaseConstants.LOCATION_TABLE, null, cv,SQLiteDatabase.CONFLICT_REPLACE);
		Log.d(TAG,"insert into Location table with row id "+val+"");
	}
	
	public void deleteFromLocationTable(MyLocation loc)
	{
		String cordicate = loc.toJsonString();
		long val=mmDb.delete(DatabaseConstants.LOCATION_TABLE, DatabaseConstants.LOCATION_COORDINATE + "=  ?", new String[] { cordicate });
		Log.d(TAG,"delete form location table with val" + val+"");
	}

	public void deleteAllLocationTable()
	{
		long val=mmDb.delete(DatabaseConstants.LOCATION_TABLE, null, null);
		Log.d(TAG,"deletion  form  Location table total rows  "+val+"");
	}
	
	public void insertIntoGeofenceTable(GeoLocationStore geoLocationStore,int status)
	{
		ContentValues cv=new ContentValues();
		cv.put(DatabaseConstants.GEOFENCE_COORDINATE, geoLocationStore.getLat()+","+geoLocationStore.getLng()+"");
		cv.put(DatabaseConstants.GEOFENCE_DEALER_ID, geoLocationStore.getRequestId());
		cv.put(DatabaseConstants.GEOFENCE_EXPIRATION_TIME, geoLocationStore.getExpirationDuration());
		cv.put(DatabaseConstants.GEOFENCE_LOITERRING_TIME,geoLocationStore.getLoiterringTime());
		cv.put(DatabaseConstants.GEOFENCE_RADIUS,geoLocationStore.getRadius());
		cv.put(DatabaseConstants.GEOFENCE_TRANSISTION_TYPE,geoLocationStore.getTransitionType());
		cv.put(DatabaseConstants.GEOFENCE_STATUS,status);
		long val=mmDb.insertWithOnConflict(DatabaseConstants.GEOFENCE_TABLE, null, cv,SQLiteDatabase.CONFLICT_REPLACE);
		Log.d(TAG,"insert into Geofence table with row id "+val+"");
	}
	
	public void updateGeoFenceTable(String dealerId,int status)
	{
		ContentValues cv=new ContentValues();
		cv.put(DatabaseConstants.GEOFENCE_STATUS,status);
		long val=mmDb.update(DatabaseConstants.GEOFENCE_TABLE, cv, DatabaseConstants.GEOFENCE_DEALER_ID + " = ?", new String[]{dealerId});
		Log.d(TAG,"update  into Geofence table with row id "+val+" and delaer id is "+dealerId);
	}
	
	public List<GeoLocationStore> getAllGeofencesWhichNotEntered()
	{
		List<GeoLocationStore> list=new ArrayList<GeoLocationStore>();
		
		String sql="SELECT * FROM "+ DatabaseConstants.GEOFENCE_TABLE + " WHERE  " + DatabaseConstants.GEOFENCE_STATUS  + " <> " + GEOFENCESTATUS.GEOFENCE_ENTERED;
		
		
		try
		{
			Cursor cursor = mmDb.rawQuery(sql, null);
			if (cursor.moveToFirst())
			{
				do
				{
					double lat = Double.parseDouble(cursor.getString(cursor.getColumnIndex(DatabaseConstants.GEOFENCE_COORDINATE)).split(",")[0]);
					double lng=Double.parseDouble(cursor.getString(cursor.getColumnIndex(DatabaseConstants.GEOFENCE_COORDINATE)).split(",")[1]);
					String dealerID=cursor.getString(cursor.getColumnIndex(DatabaseConstants.GEOFENCE_DEALER_ID));
					String expTime=cursor.getString(cursor.getColumnIndex(DatabaseConstants.GEOFENCE_EXPIRATION_TIME));
					String lotTime=cursor.getString(cursor.getColumnIndex(DatabaseConstants.GEOFENCE_LOITERRING_TIME));
					int radius=cursor.getInt(cursor.getColumnIndex(DatabaseConstants.GEOFENCE_RADIUS));
					int tansType=cursor.getInt(cursor.getColumnIndex(DatabaseConstants.GEOFENCE_TRANSISTION_TYPE));
					GeoLocationStore store=new GeoLocationStore(lat, lng, radius, dealerID, tansType);
					store.setExpirationDuration(Long.parseLong(expTime));
					store.setLoiterringTime(Long.parseLong(lotTime));
					list.add(store);
				}
				while (cursor.moveToNext());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	public void fetchAllGeofencesAndSendToServer(){


		String sql = "SELECT * FROM " + DatabaseConstants.GEOFENCE_TABLE + " WHERE  " + DatabaseConstants.GEOFENCE_STATUS + " = " + GEOFENCESTATUS.GEOFENCE_ENTERED;
		try
		{
			Cursor cursor = mmDb.rawQuery(sql, null);
			if (cursor.moveToFirst())
			{
				do
				{
					String dealerID = cursor.getString(cursor.getColumnIndex(DatabaseConstants.GEOFENCE_DEALER_ID));
					ConsumerForEverythingElse.getInstance().addToQueue(new NotifyDealer(dealerID));
				}
				while (cursor.moveToNext());
				
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	public void deleteFromGeofenceTable(String dealerId)
	{
		long val=mmDb.delete(DatabaseConstants.GEOFENCE_TABLE,DatabaseConstants.GEOFENCE_DEALER_ID + " = ? ", new String[]{dealerId});
		Log.d(TAG,"delefe from geofence table with id"+dealerId +"and value is "+ val);
	}
	
	public void deleteAllFromGeofenceTable()
	{
		long val=mmDb.delete(DatabaseConstants.GEOFENCE_TABLE, null, null);
		Log.d(TAG,"deletion  form  Location table total rows  "+val+"");
	}
	
	public void deleteAll()
	{
		deleteAllDealerData();
		deleteAllFromGeofenceTable();
		deleteAllLocationTable();
		deleteAllFromPhotoTable();
	}
	
	public void fetchAllLocationAndSendToServer()
	{
		String sql = "SELECT * FROM " + DatabaseConstants.LOCATION_TABLE + " WHERE  " + DatabaseConstants.LOCATION_SERVER_SYNCED_STATUS + " = " + "0";
		try
		{
			Cursor cursor = mmDb.rawQuery(sql, null);
			if (cursor.moveToFirst())
			{
				do
				{
					JSONObject loc=new JSONObject(cursor.getString(cursor.getColumnIndex(DatabaseConstants.LOCATION_COORDINATE)));
					long sts = Long.parseLong(cursor.getString(cursor.getColumnIndex(DatabaseConstants.STS)));
					ConsumerLocation.getInstance().addToQueue(new SendLocationToServer(new MyLocation(loc), sts));
				}
				while (cursor.moveToNext());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void fetchAllPhotosAndSendToServer()
	{
		String sql = "SELECT * FROM " + DatabaseConstants.PHOTO_TABLE;
		try
		{
			Cursor cursor = mmDb.rawQuery(sql, null);
			if (cursor.moveToFirst())
			{
				do
				{
					String PodType = cursor.getString(cursor.getColumnIndex(DatabaseConstants.PHOTO_POD_TYPE));
					String dealerId = cursor.getString(cursor.getColumnIndex(DatabaseConstants.PHOTO_DEALER_ID));
					String filePath = cursor.getString(cursor.getColumnIndex(DatabaseConstants.PHOTO_FILE_PATH));
					String grId=cursor.getString(cursor.getColumnIndex(Constants.GRID));
					ConsumerForEverythingElse.getInstance().addToQueue(new UploadPhotoTask(PodType, dealerId, filePath,grId));
				}
				while (cursor.moveToNext());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	
	public  void insertIntoPhotoTable(String FilePath,String PodType,String dealerID,String grId)
	{
		ContentValues cv=new ContentValues();
		cv.put(DatabaseConstants.PHOTO_DEALER_ID, dealerID);
		cv.put(DatabaseConstants.PHOTO_FILE_PATH, FilePath);
		cv.put(DatabaseConstants.PHOTO_POD_TYPE, PodType);
		cv.put(DatabaseConstants.STS, System.currentTimeMillis()/1000);
		cv.put(DatabaseConstants.PHOTO_GR_ID, grId);
		long val=mmDb.insertWithOnConflict(DatabaseConstants.PHOTO_TABLE,null, cv,SQLiteDatabase.CONFLICT_REPLACE);
		Log.d(TAG,"inserted into Photo Table with id"+val);
	}
	
	public void deleteFromPhotoTable(String filePath)
	{
		long val=mmDb.delete(DatabaseConstants.PHOTO_TABLE,DatabaseConstants.PHOTO_FILE_PATH + " = ? ", new String[]{filePath});
		Log.d(TAG,"delefe from geofence table with id"+filePath +"and value is "+ val);
	}
	
	public void deleteAllFromPhotoTable()
	{
		long val=mmDb.delete(DatabaseConstants.PHOTO_TABLE, null, null);
		Log.d(TAG,"deletion  form  Photo table total rows  "+val+"");
	}


}
