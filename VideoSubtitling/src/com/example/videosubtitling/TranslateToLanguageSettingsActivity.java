package com.example.videosubtitling;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

public class TranslateToLanguageSettingsActivity extends PreferenceActivity {
	public static final String TRANSLATE_TO_LANGUAGE_PREF_KEY = "translateToLanguage";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.layout.language_settings);

		Preference englishPref = (Preference) findPreference(LanguageSettingsManager.ENGLISH);
		if (englishPref != null) {
			englishPref
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {

						public boolean onPreferenceClick(Preference preference) {
							LanguageSettingsManager
									.setLanguagePref(LanguageSettingsManager.ENGLISH);
							finish();
							return true;
						}

					});
		}

		Preference malayPref = (Preference) findPreference(LanguageSettingsManager.MALAY);
		if (malayPref != null) {
			malayPref
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {

						public boolean onPreferenceClick(Preference preference) {
							LanguageSettingsManager
									.setLanguagePref(LanguageSettingsManager.MALAY);
							finish();
							return true;
						}

					});
		}
	}
}
