package com.example.locationapp.data;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.locationapp.Utils.Constants;

public class Dealer
{
	private String name;

	private Double coordinateLat;

	private Double coordinateLng;

	private String Id;

	private String address;

	private int state = DealerState.NORMAL;

	/**
	 * @param name
	 * @param coordinateLat
	 * @param coordinateLng
	 * @param id
	 */
	public Dealer(String name, Double coordinateLat, Double coordinateLng, String id, String address)
	{
		this.name = name;
		this.coordinateLat = coordinateLat;
		this.coordinateLng = coordinateLng;
		this.Id = id;
		this.setAddress(address);
	}

	public Dealer(JSONObject dealer)
	{
		try
		{
			this.name = dealer.getString(Constants.DEALER_NAME);

			this.coordinateLat = Double.parseDouble(dealer.getString(Constants.DEALER_LOC).split(",")[0]);
			this.coordinateLng = Double.parseDouble(dealer.getString(Constants.DEALER_LOC).split(",")[1]);
			this.setAddress(dealer.getString(Constants.DEALER_ADD));
			this.Id = dealer.getString(Constants.DEALER_ID);

		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @param coordinateLat
	 *            the coordinateLat to set
	 */
	public void setCoordinateLat(Double coordinateLat)
	{
		this.coordinateLat = coordinateLat;
	}

	/**
	 * @param coordinateLng
	 *            the coordinateLng to set
	 */
	public void setCoordinateLng(Double coordinateLng)
	{
		this.coordinateLng = coordinateLng;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id)
	{
		Id = id;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return the coordinateLat
	 */
	public Double getCoordinateLat()
	{
		return coordinateLat;
	}

	/**
	 * @return the coordinateLng
	 */
	public Double getCoordinateLng()
	{
		return coordinateLng;
	}

	/**
	 * @return the id
	 */
	public String getId()
	{
		return Id;
	}

	/**
	 * @return the state
	 */
	public int getState()
	{
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(int state)
	{
		this.state = state;
	}

	/**
	 * @return the address
	 */
	public String getAddress()
	{
		return address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address)
	{
		this.address = address;
	}

	public static class DealerState
	{
		public static final int NORMAL = 1;

		public static final int WITHIN_RADIUS = 2;

		public static final int POD_COLLECTED = 3;
	}
}
