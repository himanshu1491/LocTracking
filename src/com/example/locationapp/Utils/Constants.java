package com.example.locationapp.Utils;

import android.os.Environment;

public class Constants
{
	public static final String GCM_PUSH_ID = "137346078681";

	public static final String LOCATION_SHARED_PREF = "location_pref";

	public static final String GCM_ID = "gcmid";

	public static String SENDTOSERVER = "xsendtosrver";

	public static String MEDIA_DIRECTORY_ROOT = Environment.getExternalStorageDirectory() + "/LocationApp";

	public static String AUTH_TOKEN="AuthToken";

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

	public static final int DEALER_RADIUS = 100;

	public static final String START_TRACKING = "startTracking";

	public static final String REFRESH_DEALER_DATA = "refresh_dealer_data";
	
	public static final String POD_TYPE="podType";
	
	public static final class PODTYPE
	{
		public static final String POD_PARTIAL = "partial";

		public static final String POD_FULL = "full";
	}
	public static final String LOCATION="location";
	
	public static final String STS="sts";

}
