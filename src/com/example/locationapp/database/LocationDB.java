package com.example.locationapp.database;

import static com.example.locationapp.Utils.Constants.DatabaseConstants.DEALER_DATA;
import static com.example.locationapp.Utils.Constants.DatabaseConstants._ID;

import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.locationapp.Utils.Constants.DatabaseConstants;
import com.example.locationapp.data.Dealer;
import com.example.locationapp.ui.LocationApp;

public class LocationDB extends SQLiteOpenHelper
{

	public static volatile LocationDB _instance;

	private SQLiteDatabase mmDb = null;

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
		String sql = DatabaseConstants.CREATE_TABLE +  DatabaseConstants.DEALER_TABLE  +  " ( "  + _ID + " PRIMARY KEY ," + DEALER_DATA + " TEXT" + ")";
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
		Log.d("Inserted with DB row id",val+"");
	}
	
	public void deleteFromDealerTable(Dealer dealer)
	{
		mmDb.delete(DatabaseConstants.DEALER_TABLE, _ID+" = ", new String[]{dealer.getId()});
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
		mmDb.delete(DatabaseConstants.DEALER_TABLE, null, null);
	}

}
