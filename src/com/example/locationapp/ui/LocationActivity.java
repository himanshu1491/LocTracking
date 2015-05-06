package com.example.locationapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.actionbarsetup.R;
import com.example.locationapp.Utils.Constants;
import com.example.locationapp.Utils.Utils;

public class LocationActivity extends ActionBarActivity
{
	DealerFragment dealerFragment;

	PODFragment podFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setLogo(R.drawable.ic_launcher);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		initFragment();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		
	}

	private void initFragment()
	{
		FragmentManager fm=getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		if (fm.findFragmentByTag(DealerFragment.class.getName()) == null)
		{
			dealerFragment = new DealerFragment();
		}
		else
		{
			dealerFragment = (DealerFragment) fm.findFragmentByTag(DealerFragment.class.getName());
			ft.remove(dealerFragment).commit();
			fm.popBackStack();
			ft=fm.beginTransaction();
			dealerFragment = new DealerFragment();
		}
		if (!isFinishing())
		{
			ft.replace(R.id.container, dealerFragment, DealerFragment.class.getName()).commitAllowingStateLoss();
		}
		Utils.registerGCMID(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (requestCode)
		{
		case Constants.REQUESTCODE_CAMERA:
			if (podFragment != null)
			{
				podFragment.onActivityResult(requestCode, resultCode, data);
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void attachPodFragment(String id)
	{
		FragmentManager fm=getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		if (fm.findFragmentByTag(PODFragment.class.getName()) == null)
		{
			podFragment = new PODFragment();
		}
		else
		{
			podFragment = (PODFragment) fm.findFragmentByTag(PODFragment.class.getName());
			ft.remove(podFragment).commit();
			fm.popBackStack();
			ft=fm.beginTransaction();
			podFragment = new PODFragment();
		}
		
		Bundle b = new Bundle();
		b.putString("dealerId", id);
		podFragment.setArguments(b);
		ft.addToBackStack(null);
		if(podFragment.isAdded())
		{
			return;
		}
		if (!isFinishing())
		{
			ft.replace(R.id.container, podFragment, PODFragment.class.getName()).commit();
		}
	}

}
