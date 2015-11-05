package io.github.stalker2010.post;

import java.io.*;
import android.os.*;
import java.util.*;

public final class StorageUtils
{
	private static final StorageUtils instance = new StorageUtils();
	public File dir;
	private StorageUtils()
	{
		dir = new File(Environment.getExternalStorageDirectory(), "Post Machine");
		if (!dir.exists())
		{
			if (isExternalStorageWritable())
			{
				dir.mkdirs();
			}
		}
	}
	public static StorageUtils get()
	{
		return instance;
	}
	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable()
	{
		String state = Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED.equals(state);
	}

	/* Checks if external storage is available to at least read */
	public boolean isExternalStorageReadable()
	{
		String state = Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED.equals(state) ||
			Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
	}
	public File[] list()
	{
		if (isExternalStorageReadable())
		{
			return dir.listFiles();
		}
		else
		{
			return new File[0];
		}
	}
	public boolean save(String name, String content) {
		if (!isExternalStorageWritable()) {
			return false;
		}
		try
		{
			FileWriter w = new FileWriter(fileByName(name), false);
			w.append(content);
			w.flush();
			w.close();
			return true;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	public String read(String name) {
		if (!isExternalStorageReadable()) {
			return null;
		}
		try
		{
			File f = fileByName(name);
			BufferedReader reader = new BufferedReader(new FileReader(f));
			StringBuilder sb = new StringBuilder(Long.valueOf(f.length()).intValue());
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
			reader.close();
			return sb.toString();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	public File fileByName(String name) {
		return new File(dir, name);
	}
}
