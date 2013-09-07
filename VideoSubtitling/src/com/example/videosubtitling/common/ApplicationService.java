package com.example.videosubtitling.common;

import android.app.Application;

public class ApplicationService extends Application
{
	public static ApplicationService sApplication;

	public ApplicationService()
	{
		sApplication = this;
	}

	public static ApplicationService getApplicationInstance()
	{
		return sApplication;
	}

}
