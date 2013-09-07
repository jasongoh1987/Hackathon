package com.example.videosubtitling;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.videosubtitling.speechtotext.AppInfo;
import com.example.videosubtitling.translation.BackgroundTranslationTask;
import com.nuance.nmdp.speechkit.Prompt;
import com.nuance.nmdp.speechkit.Recognition;
import com.nuance.nmdp.speechkit.Recognizer;
import com.nuance.nmdp.speechkit.SpeechError;
import com.nuance.nmdp.speechkit.SpeechKit;

public class MainActivity extends Activity {
	private static final String TAG = "AudioRecordTest";
	private static final String ENVIRONMENT_PATH = Environment
			.getExternalStorageDirectory().getAbsolutePath() + File.separator;
	private static final String AUDIO_FILE_NAME = "audiorecordtest.3gp";

	private static final int LISTENING_DIALOG = 0;
	private Recognizer mCurrentRecognizer;
	private Recognizer.Listener mRecognizerListener;
	private Handler mHandler;

	private static String mFileName = null;
	private String mVideoFileName = "vid.mp4"; // TODO: allow user to specify
												// the video

	private MediaRecorder mRecorder = null;
	private MediaPlayer mPlayer = null;
	private VideoView mVideoView;
	private ProgressBar mProgressBar;
	private TextView mCapturedText;
	private TextView mTranslatedText;

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

		mVideoView = (VideoView) findViewById(R.id.video_view);
		mProgressBar = (ProgressBar) findViewById(R.id.video_progress_bar);
		mCapturedText = (TextView) findViewById(R.id.captured_text);
		mTranslatedText = (TextView) findViewById(R.id.translated_text);

		mVideoView.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				onVideoTouch();
				return false;
			}
		});

		// Set recording output path
		mFileName = ENVIRONMENT_PATH + AUDIO_FILE_NAME;

		// Play video
		String videoPath = ENVIRONMENT_PATH + mVideoFileName;
		new PlayVideoAsyncTask(videoPath).execute();
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

	private void setResult(final String result) {
		if (mCapturedText != null) {
			System.out.println(result);
			mCapturedText.setText(result);
			if (!result.isEmpty()) {
				translateCapturedTextToPreferredLanguage(result);
			}
		}
	}

	private void translateCapturedTextToPreferredLanguage(String textToTranslate) {
		try {
			final String translatedText = new BackgroundTranslationTask()
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
							new String[] { textToTranslate }).get();
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					mTranslatedText.setText(translatedText);
				}

			});
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setResults(Recognition.Result[] results) {
		System.out.println(results.length);
		if (results.length > 0) {
			setResult(results[0].getText());
		} else {
			setResult("");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			break;
		}
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

	@Override
	protected void onDestroy() {
		if (sSpeechKit != null) {
			sSpeechKit.release();
			sSpeechKit = null;
		}

		// clear progress bar progress
		mProgressBar.setProgress(100);

		super.onDestroy();
	}

	/**
	 * Method to handle respective button clicks.
	 */
	public void onButtonClick(View view) {
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
	 * Method to handle video's behavior (pause/resume video) upon user touch.
	 */
	private void onVideoTouch() {
		if (mVideoView.isPlaying()) {
			mVideoView.pause();
		} else {
			mVideoView.start();
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

	/**
	 * Class to play the video based on the path passed in.
	 */
	private class PlayVideoAsyncTask extends AsyncTask<Void, Integer, Void> {
		private int mDuration = 0;
		private int mCurrent = 0;

		PlayVideoAsyncTask(String videoPath) {
			mVideoView.setVideoURI(Uri.parse(videoPath));

			mProgressBar.setProgress(0);
			mProgressBar.setMax(100);
		}

		@Override
		protected Void doInBackground(Void... params) {

			mVideoView.start();
			mVideoView.setOnPreparedListener(new OnPreparedListener() {

				public void onPrepared(MediaPlayer mp) {
					mDuration = mVideoView.getDuration();
				}
			});

			do {
				mCurrent = mVideoView.getCurrentPosition();
				try {
					publishProgress((int) (mCurrent * 100 / mDuration));
					if (mProgressBar.getProgress() >= 100) {
						break;
					}
				} catch (Exception e) {
				}
			} while (mProgressBar.getProgress() <= 100);

			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			mProgressBar.setProgress(values[0]);
		}
	}
}
