package com.example.locationapp.http;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;

public class StringLocEntity extends StringEntity
{
	private ProgressListener listener = null;;

	public StringLocEntity(String s, ProgressListener listener) throws UnsupportedEncodingException
	{
		super(s);
		this.listener = listener;
	}

	@Override
	public void writeTo(OutputStream outstream) throws IOException
	{

		super.writeTo(new CountingOutputStream(outstream, listener, getContentLength()));
	}

	public static class CountingOutputStream extends FilterOutputStream
	{
		private ProgressListener listener = null;

		private long transferred = 0l;

		private long totalSize = 0l;

		public CountingOutputStream(OutputStream out, ProgressListener listener, long totalSize)
		{
			super(out);
			this.listener = listener;
			this.totalSize = totalSize;

		}

		@Override
		public void write(byte[] buffer, int offset, int len) throws IOException
		{
			super.write(buffer, offset, len);
			this.transferred += len;
			this.listener.progress((int) ((this.transferred / (float) totalSize) * 100));
		}

		@Override
		public void write(int oneByte) throws IOException
		{
			super.write(oneByte);
			this.transferred++;
			this.listener.progress((int) ((this.transferred / (float) totalSize) * 100));
		}
	}

	public static interface ProgressListener
	{
		void progress(long num);
	}

}
