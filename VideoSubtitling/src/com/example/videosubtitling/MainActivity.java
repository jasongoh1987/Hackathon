package com.example.videosubtitling;

import java.io.IOException;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.example.videosubtitling.speechtotext.AppInfo;
import com.nuance.nmdp.speechkit.Prompt;
import com.nuance.nmdp.speechkit.Recognition;
import com.nuance.nmdp.speechkit.Recognizer;
import com.nuance.nmdp.speechkit.SpeechError;
import com.nuance.nmdp.speechkit.SpeechKit;

public class MainActivity extends Activity {
	private static final String TAG = "AudioRecordTest";
	private static final String AUDIO_FILE_NAME = "/audiorecordtest.3gp";

	private static final int LISTENING_DIALOG = 0;
	private Recognizer mCurrentRecognizer;
	private Recognizer.Listener mRecognizerListener;
	private Handler mHandler;

	private static String mFileName = null;
	private MediaRecorder mRecorder = null;
	private MediaPlayer mPlayer = null;

	private static SpeechKit sSpeechKit;

	// Allow other activities to access the SpeechKit instance.
	static SpeechKit getSpeechKit() {
		return sSpeechKit;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setVolumeControlStream(AudioManager.STREAM_MUSIC); // So that the 'Media
		// Volume' applies
		// to this activity

		mHandler = new Handler();
		mRecognizerListener = createListener();

		// If this Activity is being recreated due to a config change (e.g.
		// screen rotation), check for the saved SpeechKit instance.
		sSpeechKit = (SpeechKit) getLastNonConfigurationInstance();
		if (sSpeechKit == null) {
			sSpeechKit = SpeechKit.initialize(getApplication()
					.getApplicationContext(), AppInfo.SpeechKitAppId,
					AppInfo.SpeechKitServer, AppInfo.SpeechKitPort,
					AppInfo.SpeechKitSsl, AppInfo.SpeechKitApplicationKey);
			sSpeechKit.connect();
			// TODO: Keep an eye out for audio prompts not working on the Droid
			// 2 or other 2.2 devices.
			Prompt beep = sSpeechKit
					.defineAudioPrompt(com.example.videosubtitling.R.raw.beep);
			sSpeechKit.setDefaultRecognizerPrompts(beep, Prompt.vibration(100),
					null, null);
		}

		// Set recording output path
		mFileName = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ AUDIO_FILE_NAME;
	}

	private void startRecognizeSpeech() {
		setResults(new Recognition.Result[0]);

		// if (v == dictationButton) {
		mCurrentRecognizer = getSpeechKit().createRecognizer(
				Recognizer.RecognizerType.Dictation,
				Recognizer.EndOfSpeechDetection.Long, "en_US",
				mRecognizerListener, mHandler);
		// } else {
		// mCurrentRecognizer = getSpeechKit().createRecognizer(
		// Recognizer.RecognizerType.Search,
		// Recognizer.EndOfSpeechDetection.Short, "en_US",
		// mRecognizerListener, mHandler);
		// }
		mCurrentRecognizer.start();
	}

	private void stopRecognizeSpeech() {
		mCurrentRecognizer.stopRecording();
	}

	private Recognizer.Listener createListener() {
		return new Recognizer.Listener() {
			@Override
			public void onRecordingBegin(Recognizer recognizer) {

				// Create a repeating task to update the audio level
				Runnable r = new Runnable() {
					public void run() {
						if (mCurrentRecognizer != null) {
							mHandler.postDelayed(this, 500);
						}
					}
				};
				r.run();
			}

			@Override
			public void onRecordingDone(Recognizer recognizer) {

			}

			@Override
			public void onError(Recognizer recognizer, SpeechError error) {
				if (recognizer != mCurrentRecognizer)
					return;

				mCurrentRecognizer = null;

				// Display the error + suggestion in the edit box
				String detail = error.getErrorDetail();
				String suggestion = error.getSuggestion();

				if (suggestion == null)
					suggestion = "";
				setResult(detail + "\n" + suggestion);
				// for debugging purpose: printing out the speechkit session id
				android.util.Log.d("Nuance SampleVoiceApp",
						"Recognizer.Listener.onError: session id ["
								+ getSpeechKit().getSessionId() + "]");
			}

			@Override
			public void onResults(Recognizer recognizer, Recognition results) {

				mCurrentRecognizer = null;
				int count = results.getResultCount();
				Recognition.Result[] rs = new Recognition.Result[count];
				for (int i = 0; i < count; i++) {
					rs[i] = results.getResult(i);
				}
				setResults(rs);
				// for debugging purpose: printing out the speechkit session id
				android.util.Log.d("Nuance SampleVoiceApp",
						"Recognizer.Listener.onResults: session id ["
								+ getSpeechKit().getSessionId() + "]");
			}
		};
	}

	private void setResult(String result) {
		TextView t = (TextView) findViewById(R.id.translated_text);
		if (t != null)
			t.setText(result);
	}

	private void setResults(Recognition.Result[] results) {
		if (results.length > 0) {
			setResult(results[0].getText());
		} else {
			setResult("");
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (sSpeechKit != null) {
			sSpeechKit.release();
			sSpeechKit = null;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mRecorder != null) {
			mRecorder.release();
			mRecorder = null;
		}

		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
		}
	}

	/**
	 * Method to handle respective button clicks.
	 */
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.start_recording:
			// startAudioRecording();
			startRecognizeSpeech();
			break;

		case R.id.stop_recording:
			// stopAudioRecording();
			stopRecognizeSpeech();
			break;

		case R.id.start_playing:
			startAudioPlayback();
			break;

		case R.id.stop_playing:
			stopAudioPlayback();
			break;

		default:
			break;
		}
	}

	/**
	 * Start audio recording
	 */
	private void startAudioRecording() {
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setOutputFile(mFileName);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		try {
			mRecorder.prepare();
		} catch (IOException e) {
			Log.e(TAG, "prepare() failed");
		}

		mRecorder.start();
	}

	/**
	 * Stop audio recording
	 */
	private void stopAudioRecording() {
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
	}

	/**
	 * Start audio playback
	 */
	private void startAudioPlayback() {
		mPlayer = new MediaPlayer();
		try {
			mPlayer.setDataSource(mFileName);
			mPlayer.prepare();
			mPlayer.start();
		} catch (IOException e) {
			Log.e(TAG, "prepare() failed");
		}
	}

	/**
	 * Stop audio playback
	 */
	private void stopAudioPlayback() {
		mPlayer.release();
		mPlayer = null;
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		// Save the SpeechKit instance, because we know the Activity will be
		// immediately recreated.
		SpeechKit sk = sSpeechKit;
		sSpeechKit = null; // Prevent onDestroy() from releasing SpeechKit
		return sk;
	}
}
