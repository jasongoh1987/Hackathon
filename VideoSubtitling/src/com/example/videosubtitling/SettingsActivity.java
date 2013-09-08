package com.example.videosubtitling;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {
	private Preference mTranslateToLanguagePref;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.layout.activity_settings);

		mTranslateToLanguagePref = (Preference) findPreference(getString(R.string.translate_to_language));
		if (mTranslateToLanguagePref != null) {
			mTranslateToLanguagePref
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {

						public boolean onPreferenceClick(Preference preference) {
							Intent translateTolanguagePref = new Intent(
									getApplicationContext(),
									TranslateToLanguageSettingsActivity.class);
							startActivity(translateTolanguagePref);
							return true;
						}

					});
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		mTranslateToLanguagePref.setSummary(LanguageSettingsManager
				.readLanguagePref());
	}
}
