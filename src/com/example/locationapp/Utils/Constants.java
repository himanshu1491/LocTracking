package com.example.locationapp.Utils;

import android.os.Environment;

public class Constants
{
	public static final String GCM_PUSH_ID = "137346078681";

	public static final String LOCATION_SHARED_PREF = "location_pref";

	public static final String GCM_ID = "gcmid";

	public static String SENDTOSERVER = "xsendtosrver";

	public static String MEDIA_DIRECTORY_ROOT = Environment.getExternalStorageDirectory() + "/LocationApp";

	public static String AUTH_TOKEN = "AuthToken";

	public static String GRID = "grId";

	public static final int REQUESTCODE_CAMERA = 3;

	public static final String FILE_PATH_CAMERA = "filepathcamera";

	public static final String HTTP_STRING = "http://";

	public static final String DEV_STAGING_HOST = "ap1.rtport.com/web/app_dev.php/api/v1";

	public static final String URL_GCM = "/generateToken";

	public static final String UPDATE_LOC = "/updateLocation";

	public static final String URL_NOTIFY_DEALER = "/notifyDealer";

	public static final int MAX_DIMENSION_MEDIUM_FULL_SIZE_PX = 800;

	public static final int MAX_BUFFER_SIZE_KB = 100;

	public static final String FILE_PATH_CAMERA_COMPRESS = "cam_compress_";

	public static final String DEALER_ID_UPLOAD_POD = "dealer_id_pod";

	public static final String UPLOAD_PHOTO = "/savePod";

	public static final String TOKEN = "token";

	public static final String DEALER_DETAILS = "dealerDetails";

	public static final String DEALER_NAME = "name";

	public static final String DEALER_LOC = "latLong";

	public static final String DEALER_ADD = "address";

	public static final String DEALER_ID = "consigneeId";

	public static final int DEALER_RADIUS_VALUE = 100;

	public static final String DEALER_RADIUS = "radius";

	public static final String DEALER_POD_STATUS = "podStatus";

	public static final String START_TRACKING = "startTracking";

	public static final String REFRESH_DEALER_DATA = "refresh_dealer_data";

	public static final String POD_TYPE = "podType";

	public static final class PODTYPE
	{
		public static final String POD_PARTIAL = "2";

		public static final String POD_FULL = "1";
		
		public static final String POD_REJECTED="3";
	}

	public static final String LOCATION = "location";

	public static final String STS = "sts";

	public static final String SYSTEM_ON = "systemOn";

	public static final String LOCATION_SYNC_TIME = "syncTime";

	public static final int LOCATION_INTERVAL = 5;

	public static final class DatabaseConstants
	{
		public static final String NAME = "DB_RTPORT";

		public static final int VERSION = 1;

		public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ";

		public static final String DEALER_TABLE = "dealer";

		public static final String _ID = "_id";

		public static final String DEALER_DATA = "data";

		public static final String DEALER_STATUS = "dealerStatus";

		public static final String LOCATION_TABLE = "location";

		public static final String LOCATION_COORDINATE = "locCordinate";

		public static final String STS = "systemTimeStamp";

		public static final String LOCATION_SERVER_SYNCED_STATUS = "sendToServer";

		public static final String GEOFENCE_TABLE = "geofence";

		public static final String GEOFENCE_COORDINATE = "Cordinate";

		public static final String GEOFENCE_RADIUS = "radius";

		public static final String GEOFENCE_EXPIRATION_TIME = "expTime";

		public static final String GEOFENCE_LOITERRING_TIME = "loiteringTime";

		public static final String GEOFENCE_TRANSISTION_TYPE = "transitionType";

		public static final String GEOFENCE_DEALER_ID = "geoFenceDealerID";

		public static final String GEOFENCE_STATUS = "geofenceEntered";

		public static final String PHOTO_TABLE = "photoTable";

		public static final String PHOTO_DEALER_ID = "dealerId";

		public static final String PHOTO_POD_TYPE = "podType";

		public static final String PHOTO_FILE_PATH = "filePath";

		public static final String PHOTO_GR_ID = "grID";

	}

	public static final class GEOFENCESTATUS
	{
		public static final int GEOFENCE_NOTENTERED = 0;

		public static final int GEOFENCE_ENTERED = 1;

		public static final int GEOFENCE_ENTERED_SERVER_SEND = 2;

	}

	public static final class Mylocation
	{
		public static final String SPEED = "speed";

		public static final String LATITUTE = "lat";

		public static final String LONGITUTE = "lng";

		public static final String ACCURACY = "accuracy";

		public static final String ALTITUTE = "altitute";
	}

}
