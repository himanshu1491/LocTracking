package com.example.locationapp.ui;

import java.io.File;

import android.app.Activity;
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
import com.example.locationapp.Utils.LocationSharedPreference;
import com.example.locationapp.Utils.LocationThreadPoolExecutor;
import com.example.locationapp.Utils.Utils;
import com.example.locationapp.http.UploadPhotoTask;

public class PODFragment extends Fragment implements OnClickListener
{

	Button fullPodButton, halfPodButton;

	Intent in = null;

	File file = null;

	String dealerId;

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
	}

	private void bindViews()
	{
		halfPodButton = (Button) getView().findViewById(R.id.HalfPod);
		fullPodButton = (Button) getView().findViewById(R.id.fullPod);
	}

	private void listeners()
	{
		halfPodButton.setOnClickListener(this);
		fullPodButton.setOnClickListener(this);
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
			}
			in = Utils.getNativeCameraAppIntent(true, file);
			prefs.saveData(Constants.DEALER_ID_UPLOAD_POD, dealerId);
			startActivityForResult(in, Constants.REQUESTCODE_CAMERA);
			break;
		case R.id.HalfPod:

			file = Utils.createNewFile("cam_");
			if (file == null)
			{
				Toast.makeText(getActivity(), "Space not available", Toast.LENGTH_LONG).show();
			}
			in = Utils.getNativeCameraAppIntent(true, file);
			prefs.saveData(Constants.DEALER_ID_UPLOAD_POD, dealerId);
			startActivityForResult(in, Constants.REQUESTCODE_CAMERA);
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == Constants.REQUESTCODE_CAMERA && resultCode == Activity.RESULT_OK)
		{
			String filePath = prefs.getData(Constants.FILE_PATH_CAMERA, null);
			prefs.removeData(Constants.FILE_PATH_CAMERA);
			if (filePath != null)
			{
				File file = new File(filePath);

				if ((file != null) && (file.exists()))
				{
					File destinationFile = Utils.createNewFile("cam_compress");
					prefs.saveData(Constants.FILE_PATH_CAMERA_COMPRESS, destinationFile.toString());
					Utils.compressImage(filePath, destinationFile.toString());
					file.delete();
					LocationThreadPoolExecutor.getInstance().execute(new UploadPhotoTask());
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
		getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
	}

}
