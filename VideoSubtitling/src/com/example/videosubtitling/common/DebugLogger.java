package com.example.videosubtitling.common;

import android.content.pm.ApplicationInfo;
import android.util.Log;

public class DebugLogger
{
	private static boolean sIsDebugBuild = (ApplicationService
	    .getApplicationInstance().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) == ApplicationInfo.FLAG_DEBUGGABLE;

	public static void log(Class<?> classToLog, String message)
	{
		if (sIsDebugBuild)
		{
			Log.d(DebugLogger.class.getName(), message);
		}
	}
}
