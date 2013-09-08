package com.example.videosubtitling;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.example.videosubtitling.common.ApplicationService;

public class LanguageSettingsManager {
	public static final String ENGLISH = "English";
	public static final String MALAY = "Malay";

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
