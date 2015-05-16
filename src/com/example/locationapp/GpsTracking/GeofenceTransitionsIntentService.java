package com.example.locationapp.GpsTracking;

import java.util.ArrayList;
import java.util.List;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.example.actionbarsetup.R;
import com.example.locationapp.GeoFence.GeoFenceManager;
import com.example.locationapp.GeoFence.GeofenceErrorMessages;
import com.example.locationapp.Utils.Constants;
import com.example.locationapp.Utils.Constants.GEOFENCESTATUS;
import com.example.locationapp.Utils.LocationThreadPoolExecutor;
import com.example.locationapp.data.Dealer;
import com.example.locationapp.data.Dealer.DealerState;
import com.example.locationapp.database.LocationDB;
import com.example.locationapp.http.NotifyDealer;
import com.example.locationapp.ui.LocationActivity;
import com.example.locationapp.ui.LocationApp;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

public class GeofenceTransitionsIntentService extends IntentService
{
	protected static final String TAG = "geofences";

	public GeofenceTransitionsIntentService()
	{
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

		if (geofencingEvent.hasError())
		{
			String errorMessage = GeofenceErrorMessages.getErrorString(this, geofencingEvent.getErrorCode());
			Log.e(TAG, errorMessage);
			return;
		}

		int geofenceTransition = geofencingEvent.getGeofenceTransition();

		// Test that the reported transition was of interest.
		if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL)
		{

			// Get the geofences that were triggered. A single event can trigger multiple geofences.
			List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

			// Get the transition details as a String.
			String geofenceTransitionDetails = getGeofenceTransitionDetails(this, geofenceTransition, triggeringGeofences);

			// Send notification and log the transition details.
			sendNotification(geofenceTransitionDetails);
			getApplicationContext().sendBroadcast(new Intent(Constants.REFRESH_DEALER_DATA));
			Log.i(TAG, geofenceTransitionDetails);
		}
		else
		{
			// Log the error.
			Log.e(TAG, getString(R.string.geofence_transition_invalid_type, geofenceTransition));
		}
	}

	/**
	 * Gets transition details and returns them as a formatted string.
	 * 
	 * @param context
	 *            The app context.
	 * @param geofenceTransition
	 *            The ID of the geofence transition.
	 * @param triggeringGeofences
	 *            The geofence(s) triggered.
	 * @return The transition details formatted as String.
	 */
	private String getGeofenceTransitionDetails(Context context, int geofenceTransition, List<Geofence> triggeringGeofences)
	{

		String geofenceTransitionString = getTransitionString(geofenceTransition);

		// Get the Ids of each geofence that was triggered.
		ArrayList triggeringGeofencesIdsList = new ArrayList();
		for (Geofence geofence : triggeringGeofences)
		{
			triggeringGeofencesIdsList.add(geofence.getRequestId());
			Dealer dealer = LocationApp.getInstance().getDealerDetails(geofence.getRequestId());
			if (dealer != null)
			{
				dealer.setState(DealerState.WITHIN_RADIUS);
				LocationApp.getInstance().putDealerDetailsInMap(dealer);
				LocationThreadPoolExecutor.getInstance().execute(new NotifyDealer(dealer.getId()));
				LocationDB.getInstance().updateGeoFenceTable(dealer.getId(),GEOFENCESTATUS.GEOFENCE_ENTERED);
				LocationDB.getInstance().insertIntoDealerTable(dealer);
				
			}
		}
		
		GeoFenceManager.getInstance().removeGeofence(triggeringGeofencesIdsList);

		String triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList);

		return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
	}

	/**
	 * Posts a notification in the notification bar when a transition is detected. If the user clicks the notification, control goes to the MainActivity.
	 */
	private void sendNotification(String notificationDetails)
	{
		// Create an explicit content Intent that starts the main Activity.
		Intent notificationIntent = new Intent(getApplicationContext(), LocationActivity.class);

		// Construct a task stack.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

		// Add the main Activity to the task stack as the parent.
		stackBuilder.addParentStack(LocationActivity.class);

		// Push the content Intent onto the stack.
		stackBuilder.addNextIntent(notificationIntent);

		// Get a PendingIntent containing the entire back stack.
		PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

		// Get a notification builder that's compatible with platform versions >= 4
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

		// Define the notification settings.
		builder.setSmallIcon(R.drawable.ic_launcher)
				// In a real app, you may want to use a library like Volley
				// to decode the Bitmap.
				.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher)).setColor(Color.RED).setContentTitle(notificationDetails).setContentText("yo")
				.setContentIntent(notificationPendingIntent);

		// Dismiss notification once the user touches it.
		builder.setAutoCancel(true);

		// Get an instance of the Notification manager
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		// Issue the notification
		mNotificationManager.notify(0, builder.build());
	}

	/**
	 * Maps geofence transition types to their human-readable equivalents.
	 * 
	 * @param transitionType
	 *            A transition type constant defined in Geofence
	 * @return A String indicating the type of transition
	 */
	private String getTransitionString(int transitionType)
	{
		switch (transitionType)
		{
		case Geofence.GEOFENCE_TRANSITION_ENTER:
			return ("enter");
		case Geofence.GEOFENCE_TRANSITION_EXIT:
			return ("exit");
		default:
			return ("enter");
		}
	}
}
