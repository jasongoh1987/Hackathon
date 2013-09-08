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
		}
		return result;
	}
}
