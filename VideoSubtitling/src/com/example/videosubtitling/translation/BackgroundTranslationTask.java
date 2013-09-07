package com.example.videosubtitling.translation;

import android.os.AsyncTask;

public class BackgroundTranslationTask extends AsyncTask<String, Void, String> {
	private BingTranslation mBingImageSearch = new BingTranslation();

	@Override
	protected String doInBackground(String... params) {
		String translatedString = mBingImageSearch.getTranslation(params[0],
				BingTranslation.ENGLISH, BingTranslation.MALAY);

		return translatedString;
	}
}
