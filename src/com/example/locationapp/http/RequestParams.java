package com.example.locationapp.http;

import java.util.ArrayList;

import org.apache.http.HttpEntity;

import android.preference.PreferenceActivity.Header;

public class RequestParams
{

	ArrayList<Header> header;

	HttpEntity entity;

	boolean isAddToken;

	String url;

	private RequestParams(RequestBuilder requestBuilder)
	{
		this.header = requestBuilder.header;
		this.entity = requestBuilder.entity;
		this.isAddToken = requestBuilder.addToken;
		this.url = requestBuilder.url;
	}

	/**
	 * @return the header
	 */
	public ArrayList<Header> getHeader()
	{
		return header;
	}

	/**
	 * @return the entity
	 */
	public HttpEntity getEntity()
	{
		return entity;
	}

	/**
	 * @return the isAddToken
	 */
	public boolean isAddToken()
	{
		return isAddToken;
	}

	/**
	 * @return the url
	 */
	public String getUrl()
	{
		return url;
	}

	/**
	 * @return the method
	 */

	public static class RequestBuilder
	{
		ArrayList<Header> header;

		HttpEntity entity;

		boolean addToken;

		String url;

		public RequestBuilder setheader(ArrayList<Header> header)
		{
			this.header = header;
			return this;
		}

		public RequestBuilder setEntity(HttpEntity entity)
		{
			this.entity = entity;
			return this;
		}

		public RequestBuilder AddToken(boolean isAddToken)
		{
			this.addToken = isAddToken;
			return this;
		}

		public RequestBuilder setUrl(String url)
		{
			this.url = url;
			return this;
		}

		public RequestParams build()
		{
			RequestParams params = new RequestParams(this);
			return params;
		}

	}

}
