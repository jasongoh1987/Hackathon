package com.example.videosubtitling;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.example.videosubtitling.common.ApplicationService;

public class LanguageSettingsManager {
	public static final String ENGLISH = "English";
	public static final String MALAY = "Malay";
	public static final String Arabic = "Arabic";
	public static final String Bulgarian = "Bulgarian";
	public static final String Catalan = "Catalan";
	public static final String Czech = "Czech";
	public static final String Danish = "Danish";
	public static final String German = "German";
	public static final String Greek = "Greek";
	public static final String Spanish = "Spanish";
	public static final String Estonian = "Estonian";
	public static final String Persian = "Persian";
	public static final String Finnish = "Finnish";
	public static final String French = "French";
	public static final String Hebrew = "Hebrew";
	public static final String Hindi = "Hindi";
	public static final String Haitian = "Haitian";
	public static final String Hungarian = "Hungarian";
	public static final String Indonesian = "Indonesian";
	public static final String Italian = "Italian";
	public static final String Japanese = "Japanese";
	public static final String Korean = "Korean";
	public static final String Lithuanian = "Lithuanian";
	public static final String Latvian = "Latvian";


	public static void setLanguagePref(String languageCode) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ApplicationService
						.getApplicationInstance());
		Editor edit = sharedPreferences.edit();
		edit.putString(
				TranslateToLanguageSettingsActivity.TRANSLATE_TO_LANGUAGE_PREF_KEY,
				languageCode);
		edit.commit();
	}

	public static String readLanguagePref() {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ApplicationService
						.getApplicationInstance());
		return sharedPreferences
				.getString(
						TranslateToLanguageSettingsActivity.TRANSLATE_TO_LANGUAGE_PREF_KEY,
						MALAY);
	}

}
