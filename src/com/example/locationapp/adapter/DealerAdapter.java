package com.example.locationapp.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.actionbarsetup.R;
import com.example.locationapp.data.Dealer;
import com.example.locationapp.data.Dealer.DealerState;

public class DealerAdapter extends BaseAdapter
{

	ArrayList<Dealer> dealerData;

	private Context context;

	public DealerAdapter(Context context, ArrayList<Dealer> dealerData)
	{
		this.context = context;
		this.dealerData = dealerData;
	}

	@Override
	public int getCount()
	{

		return dealerData.size();
	}

	@Override
	public Object getItem(int position)
	{
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position)
	{
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder = null;
		if (convertView == null)
		{
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.dealer_list_item, null);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.counter = (TextView) convertView.findViewById(R.id.counter);
			holder.imgView = (ImageView) convertView.findViewById(R.id.imgStatus);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}

		holder.name.setText(dealerData.get(position).getName());
		holder.counter.setText(position + 1 + "");

		switch (dealerData.get(position).getState())
		{
		case DealerState.NORMAL:
			holder.imgView.setBackgroundResource(R.drawable.grey);
			break;
		case DealerState.WITHIN_RADIUS:
			holder.imgView.setBackground(context.getResources().getDrawable(R.drawable.yellow));
			break;
		case DealerState.POD_COLLECTED:
			holder.imgView.setBackground(context.getResources().getDrawable(R.drawable.green));
			break;

		}
		return convertView;
	}

	static class ViewHolder
	{
		TextView name;

		ImageView imgView;

		TextView counter;

	}

	public void setData(ArrayList<Dealer> dealerData)
	{
		this.dealerData = dealerData;
	}

}
