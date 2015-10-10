package io.github.stalker2010.post;

import android.app.Application;
import android.content.Context;
import io.github.stalker2010.post.vm.*;
import java.util.*;
import java.lang.ref.*;

public class PostApplication extends Application
{
	public static String code = "0\n>\n? 29 4\n>\n? 4\n>\n? 4\n<\n<\n0\n>\n? 11\n>\n? 13\n1\n<\n? 16\n<\n? 18\n1\n<\n? 10\n<\n? 23\n<\n? 23\n>\n> 1\n>\n0\n>\n? 31\n1\n>\n? 38 36\n<\n? 29 36\n!";
	public static List<Document> vms = new ArrayList<Document>();
	private static volatile WeakReference<Document> current = new WeakReference<Document>(null);
	public static VM current() {
		return current.get().vm;
	}
	public static Document currentDoc() {
		return current.get();
	}
	public static void current(Document vm) {
		current = new WeakReference<Document>(vm);
	}
	
	@Override public void onCreate()
	{
		super.onCreate();
	}
}
