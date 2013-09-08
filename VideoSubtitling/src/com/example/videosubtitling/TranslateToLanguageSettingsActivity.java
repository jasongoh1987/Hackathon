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
		Preference arabicPref = (Preference) findPreference(LanguageSettingsManager.Arabic);
		if (arabicPref != null) {
			arabicPref
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {

						public boolean onPreferenceClick(Preference preference) {
							LanguageSettingsManager
									.setLanguagePref(LanguageSettingsManager.Arabic);
							finish();
							return true;
						}

					});
		}
		Preference BulgarianPref = (Preference) findPreference(LanguageSettingsManager.Bulgarian);
		if (BulgarianPref != null) {
			BulgarianPref
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {

						public boolean onPreferenceClick(Preference preference) {
							LanguageSettingsManager
									.setLanguagePref(LanguageSettingsManager.Bulgarian);
							finish();
							return true;
						}

					});
		}
		Preference CatalanPref = (Preference) findPreference(LanguageSettingsManager.Catalan);
		if (CatalanPref != null) {
			CatalanPref
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {

						public boolean onPreferenceClick(Preference preference) {
							LanguageSettingsManager
									.setLanguagePref(LanguageSettingsManager.Catalan);
							finish();
							return true;
						}

					});
		}
		Preference CzechPref = (Preference) findPreference(LanguageSettingsManager.Czech);
		if (CzechPref != null) {
			CzechPref
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {

						public boolean onPreferenceClick(Preference preference) {
							LanguageSettingsManager
									.setLanguagePref(LanguageSettingsManager.Czech);
							finish();
							return true;
						}

					});
		}
		Preference DanishPref = (Preference) findPreference(LanguageSettingsManager.Danish);
		if (DanishPref != null) {
			DanishPref
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {

						public boolean onPreferenceClick(Preference preference) {
							LanguageSettingsManager
									.setLanguagePref(LanguageSettingsManager.Danish);
							finish();
							return true;
						}

					});
		}
		Preference GermanPref = (Preference) findPreference(LanguageSettingsManager.German);
		if (GermanPref != null) {
			GermanPref
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {

						public boolean onPreferenceClick(Preference preference) {
							LanguageSettingsManager
									.setLanguagePref(LanguageSettingsManager.German);
							finish();
							return true;
						}

					});
		}
		Preference GreekPref = (Preference) findPreference(LanguageSettingsManager.Greek);
		if (GreekPref != null) {
			GreekPref
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {

						public boolean onPreferenceClick(Preference preference) {
							LanguageSettingsManager
									.setLanguagePref(LanguageSettingsManager.Greek);
							finish();
							return true;
						}

					});
		}
		Preference SpanishPref = (Preference) findPreference(LanguageSettingsManager.Spanish);
		if (SpanishPref != null) {
			SpanishPref
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {

						public boolean onPreferenceClick(Preference preference) {
							LanguageSettingsManager
									.setLanguagePref(LanguageSettingsManager.Spanish);
							finish();
							return true;
						}

					});
		}

		Preference EstonianPref = (Preference) findPreference(LanguageSettingsManager.Estonian);
		if (EstonianPref != null) {
			EstonianPref
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {

						public boolean onPreferenceClick(Preference preference) {
							LanguageSettingsManager
									.setLanguagePref(LanguageSettingsManager.Estonian);
							finish();
							return true;
						}

					});
		}

		Preference PersianPref = (Preference) findPreference(LanguageSettingsManager.Persian);
		if (PersianPref != null) {
			PersianPref
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {

						public boolean onPreferenceClick(Preference preference) {
							LanguageSettingsManager
									.setLanguagePref(LanguageSettingsManager.Persian);
							finish();
							return true;
						}

					});
		}

		Preference FinnishPref = (Preference) findPreference(LanguageSettingsManager.Finnish);
		if (FinnishPref != null) {
			FinnishPref
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {

						public boolean onPreferenceClick(Preference preference) {
							LanguageSettingsManager
									.setLanguagePref(LanguageSettingsManager.Finnish);
							finish();
							return true;
						}

					});
		}

		Preference FrenchPref = (Preference) findPreference(LanguageSettingsManager.French);
		if (FrenchPref != null) {
			FrenchPref
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {

						public boolean onPreferenceClick(Preference preference) {
							LanguageSettingsManager
									.setLanguagePref(LanguageSettingsManager.French);
							finish();
							return true;
						}

					});
		}

		Preference HebrewPref = (Preference) findPreference(LanguageSettingsManager.Hebrew);
		if (HebrewPref != null) {
			HebrewPref
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {

						public boolean onPreferenceClick(Preference preference) {
							LanguageSettingsManager
									.setLanguagePref(LanguageSettingsManager.Hebrew);
							finish();
							return true;
						}

					});
		}

		Preference HindiPref = (Preference) findPreference(LanguageSettingsManager.Hindi);
		if (HindiPref != null) {
			HindiPref
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {

						public boolean onPreferenceClick(Preference preference) {
							LanguageSettingsManager
									.setLanguagePref(LanguageSettingsManager.Hindi);
							finish();
							return true;
						}

					});
		}

		Preference HaitianPref = (Preference) findPreference(LanguageSettingsManager.Haitian);
		if (HaitianPref != null) {
			HaitianPref
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {

						public boolean onPreferenceClick(Preference preference) {
							LanguageSettingsManager
									.setLanguagePref(LanguageSettingsManager.Haitian);
							finish();
							return true;
						}

					});
		}

		Preference HungarianPref = (Preference) findPreference(LanguageSettingsManager.Hungarian);
		if (HungarianPref != null) {
			HungarianPref
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {

						public boolean onPreferenceClick(Preference preference) {
							LanguageSettingsManager
									.setLanguagePref(LanguageSettingsManager.Hungarian);
							finish();
							return true;
						}

					});
		}

		Preference IndonesianPref = (Preference) findPreference(LanguageSettingsManager.Indonesian);
		if (IndonesianPref != null) {
			IndonesianPref
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {

						public boolean onPreferenceClick(Preference preference) {
							LanguageSettingsManager
									.setLanguagePref(LanguageSettingsManager.Indonesian);
							finish();
							return true;
						}

					});
		}

		Preference ItalianPref = (Preference) findPreference(LanguageSettingsManager.Italian);
		if (ItalianPref != null) {
			ItalianPref
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {

						public boolean onPreferenceClick(Preference preference) {
							LanguageSettingsManager
									.setLanguagePref(LanguageSettingsManager.Italian);
							finish();
							return true;
						}

					});
		}

		Preference JapanesePref = (Preference) findPreference(LanguageSettingsManager.Japanese);
		if (JapanesePref != null) {
			JapanesePref
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {

						public boolean onPreferenceClick(Preference preference) {
							LanguageSettingsManager
									.setLanguagePref(LanguageSettingsManager.Japanese);
							finish();
							return true;
						}

					});
		}

		Preference KoreanPref = (Preference) findPreference(LanguageSettingsManager.Korean);
		if (KoreanPref != null) {
			KoreanPref
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {

						public boolean onPreferenceClick(Preference preference) {
							LanguageSettingsManager
									.setLanguagePref(LanguageSettingsManager.Korean);
							finish();
							return true;
						}

					});
		}

		Preference LithuanianPref = (Preference) findPreference(LanguageSettingsManager.Lithuanian);
		if (LithuanianPref != null) {
			LithuanianPref
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {

						public boolean onPreferenceClick(Preference preference) {
							LanguageSettingsManager
									.setLanguagePref(LanguageSettingsManager.Lithuanian);
							finish();
							return true;
						}

					});
		}

		Preference LatvianPref = (Preference) findPreference(LanguageSettingsManager.Latvian);
		if (SpanishPref != null) {
			SpanishPref
					.setOnPreferenceClickListener(new OnPreferenceClickListener() {

						public boolean onPreferenceClick(Preference preference) {
							LanguageSettingsManager
									.setLanguagePref(LanguageSettingsManager.Latvian);
							finish();
							return true;
						}

					});
		}

	}
}
