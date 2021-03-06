package com.example.locationapp.ui;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.actionbarsetup.R;
import com.example.locationapp.Utils.Constants;
import com.example.locationapp.Utils.Constants.PODTYPE;
import com.example.locationapp.Utils.ConsumerForEverythingElse;
import com.example.locationapp.Utils.LocationSharedPreference;
import com.example.locationapp.Utils.Utils;
import com.example.locationapp.data.Dealer;
import com.example.locationapp.data.Dealer.DealerState;
import com.example.locationapp.database.LocationDB;
import com.example.locationapp.http.UploadPhotoTask;

public class PODFragment extends Fragment implements OnClickListener
{

	Button fullPodButton, halfPodButton,rejectedPodButton;

	Intent in = null;

	File file = null;

	String dealerId;

	JSONObject data = null;

	LocationSharedPreference prefs = LocationSharedPreference.getInstance();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.pod_layout, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{

		super.onActivityCreated(savedInstanceState);
		dealerId = getArguments().getString("dealerId");
		bindViews();
		listeners();
		LocationActivity activity = (LocationActivity) getActivity();
		activity.enableUpButton(true);
	}

	private void bindViews()
	{
		halfPodButton = (Button) getView().findViewById(R.id.HalfPod);
		fullPodButton = (Button) getView().findViewById(R.id.fullPod);
		rejectedPodButton=(Button)getView().findViewById(R.id.rejectPod);
	}

	private void listeners()
	{
		halfPodButton.setOnClickListener(this);
		fullPodButton.setOnClickListener(this);
		rejectedPodButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.fullPod:
			file = Utils.createNewFile("cam_");
			if (file == null)
			{
				Toast.makeText(getActivity(), "Space not available", Toast.LENGTH_LONG).show();
				return;
			}
			in = Utils.getNativeCameraAppIntent(true, file);
			data = new JSONObject();
			try
			{
				data.put(Constants.DEALER_ID, dealerId);
				data.put(Constants.POD_TYPE, PODTYPE.POD_FULL);
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
			prefs.saveData(Constants.FILE_PATH_CAMERA, file.toString());
			prefs.saveData(Constants.DEALER_ID_UPLOAD_POD, data.toString());
			getActivity().startActivityForResult(in, Constants.REQUESTCODE_CAMERA);
			break;
		case R.id.HalfPod:

			file = Utils.createNewFile("cam_");
			if (file == null)
			{
				Toast.makeText(getActivity(), "Space not available", Toast.LENGTH_LONG).show();
				return;
			}
			in = Utils.getNativeCameraAppIntent(true, file);

			data = new JSONObject();
			try
			{
				data.put(Constants.DEALER_ID, dealerId);
				data.put(Constants.POD_TYPE, PODTYPE.POD_PARTIAL);
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
			prefs.saveData(Constants.DEALER_ID_UPLOAD_POD, data.toString());
			prefs.saveData(Constants.FILE_PATH_CAMERA, file.toString());
			getActivity().startActivityForResult(in, Constants.REQUESTCODE_CAMERA);
			break;
			
		case R.id.rejectPod:
			Dealer d=LocationApp.getInstance().getDealerDetails(dealerId);
			d.setState(DealerState.POD_COLLECTED);
			LocationApp.getInstance().putDealerDetailsInMap(d);
			
			LocationDB.getInstance().insertIntoPhotoTable("", PODTYPE.POD_REJECTED, dealerId, d.getGrID());
			ConsumerForEverythingElse.getInstance().addToQueue(new UploadPhotoTask(PODTYPE.POD_REJECTED, dealerId, null, d.getGrID()));
			getActivity().getSupportFragmentManager().popBackStack();
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
	}

}
