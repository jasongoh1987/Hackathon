package com.example.videosubtitling.translation;

import android.os.AsyncTask;

import com.example.videosubtitling.LanguageSettingsManager;

public class BackgroundTranslationTask extends AsyncTask<String, Void, String> {
	private BingTranslation mBingImageSearch = new BingTranslation();

	@Override
	protected String doInBackground(String... params) {
		String languagePref = LanguageSettingsManager.readLanguagePref();

		String translatedString = mBingImageSearch.getTranslation(params[0],
				BingTranslation.ENGLISH, getBingLanguageCode(languagePref));

		return translatedString;
	}

	private String getBingLanguageCode(String languagePref) {

		String result = LanguageSettingsManager.MALAY;
		if (LanguageSettingsManager.ENGLISH.equals(languagePref)) {
			result = "en";
		} else if (LanguageSettingsManager.MALAY.equals(languagePref)) {
			result = "ms";
		} else if (LanguageSettingsManager.Arabic.equals(languagePref)) {
			result = "ar";
		} else if (LanguageSettingsManager.Bulgarian.equals(languagePref)) {
			result = "bg";
		} else if (LanguageSettingsManager.Catalan.equals(languagePref)) {
			result = "ca";
		} else if (LanguageSettingsManager.Czech.equals(languagePref)) {
			result = "cs";
		} else if (LanguageSettingsManager.Danish.equals(languagePref)) {
			result = "da";
		} else if (LanguageSettingsManager.German.equals(languagePref)) {
			result = "de";
		} else if (LanguageSettingsManager.Greek.equals(languagePref)) {
			result = "el";
		} else if (LanguageSettingsManager.Spanish.equals(languagePref)) {
			result = "es";
		} else if (LanguageSettingsManager.Estonian.equals(languagePref)) {
			result = "et";
		} else if (LanguageSettingsManager.Persian.equals(languagePref)) {
			result = "fa";
		} else if (LanguageSettingsManager.Finnish.equals(languagePref)) {
			result = "fi";
		} else if (LanguageSettingsManager.French.equals(languagePref)) {
			result = "fr";
		} else if (LanguageSettingsManager.Hebrew.equals(languagePref)) {
			result = "he";
		} else if (LanguageSettingsManager.Hindi.equals(languagePref)) {
			result = "hi";
		} else if (LanguageSettingsManager.Haitian.equals(languagePref)) {
			result = "ht";
		} else if (LanguageSettingsManager.Hungarian.equals(languagePref)) {
			result = "hu";
		} else if (LanguageSettingsManager.Indonesian.equals(languagePref)) {
			result = "id";
		} else if (LanguageSettingsManager.Italian.equals(languagePref)) {
			result = "it";
		} else if (LanguageSettingsManager.Japanese.equals(languagePref)) {
			result = "ja";
		} else if (LanguageSettingsManager.Korean.equals(languagePref)) {
			result = "ko";
		} else if (LanguageSettingsManager.Lithuanian.equals(languagePref)) {
			result = "lt";
		} else if (LanguageSettingsManager.Latvian.equals(languagePref)) {
			result = "lv";
		}
		return result;
	}
}
