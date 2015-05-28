package com.example.locationapp.ui;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.actionbarsetup.R;
import com.example.locationapp.Utils.Constants;
import com.example.locationapp.Utils.LocationSharedPreference;
import com.example.locationapp.Utils.Utils;
import com.example.locationapp.adapter.DealerAdapter;
import com.example.locationapp.data.Dealer;
import com.example.locationapp.data.Dealer.DealerState;

public class DealerFragment extends Fragment implements OnItemClickListener
{

	ListView listView;
	
	TextView versionName;

	ArrayList<Dealer> dealerData = new ArrayList<Dealer>();

	DealerAdapter adapter;

	LocationSharedPreference prefs;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable
	ViewGroup container, @Nullable
	Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.device_layout, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		prefs = LocationSharedPreference.getInstance();
		bindViews();
		bindListeners();
		adapter = new DealerAdapter(getActivity(), dealerData);
		listView.setAdapter(adapter);
		LocationActivity activity=(LocationActivity) getActivity();
		activity.enableUpButton(false);
		
	}

	private void bindViews()
	{
		listView = (ListView) getView().findViewById(R.id.list);
		versionName=(TextView)getView().findViewById(R.id.versionName);
		try
		{
			String verName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
			versionName.setText(verName);
		}
		catch (NameNotFoundException e)
		{
			
			e.printStackTrace();
		}

	}

	private void bindListeners()
	{
		listView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		Log.d("DeviceFragment", "Item Pressed" + position);

		if (dealerData.get(position).getState() == DealerState.POD_COLLECTED)
		{
			Toast.makeText(getActivity(), "POD has already been collected", Toast.LENGTH_SHORT).show();
		}
		else
		{
			((LocationActivity) getActivity()).attachPodFragment(dealerData.get(position).getId());
		}
	}

	private BroadcastReceiver mRefreshDealerData = new BroadcastReceiver()
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{
			dealerData.clear();
			fillDealerData();
			adapter.setData(dealerData);
			adapter.notifyDataSetChanged();
		}
	};

	private void fillDealerData()
	{
		ConcurrentHashMap<String, Dealer> mDealer = LocationApp.getInstance().dealerMap;

		
		for (java.util.Map.Entry<String, Dealer> entry : mDealer.entrySet())
		{
			dealerData.add(entry.getValue());
			Log.d("Dealer data",entry.getValue().getState()+"");
		}
	}

	public void onResume()
	{
		super.onResume();
		getActivity().registerReceiver(mRefreshDealerData, new IntentFilter(Constants.REFRESH_DEALER_DATA));
		dealerData.clear();
		fillDealerData();
		if (adapter != null)
		{
			adapter.setData(dealerData);
			adapter.notifyDataSetChanged();
		}

	}

	@Override
	public void onPause()
	{
		getActivity().unregisterReceiver(mRefreshDealerData);
		super.onPause();
	}
}
