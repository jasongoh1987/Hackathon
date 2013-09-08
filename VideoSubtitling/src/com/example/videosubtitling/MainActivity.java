package com.example.videosubtitling;

import java.io.File;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.videosubtitling.speechtotext.AppInfo;
import com.example.videosubtitling.translation.BackgroundTranslationTask;
import com.nuance.nmdp.speechkit.Prompt;
import com.nuance.nmdp.speechkit.Recognition;
import com.nuance.nmdp.speechkit.Recognizer;
import com.nuance.nmdp.speechkit.SpeechError;
import com.nuance.nmdp.speechkit.SpeechKit;
import com.threemusketeer.videosubtitling.R;

public class MainActivity extends Activity {
	private static final String TAG = "AudioRecordTest";
	private static final String ENVIRONMENT_PATH = Environment
			.getExternalStorageDirectory().getAbsolutePath() + File.separator;
	private static final String AUDIO_FILE_NAME = "audiorecordtest.3gp";

	private static final int LISTENING_DIALOG = 0;
	private Recognizer mCurrentRecognizer;
	private Recognizer.Listener mRecognizerListener;
	private Handler mHandler;

	private String mFileName = null;
	private String mVideoAbsolutePath = null; // TODO:
												// allow
												// user
												// to
												// specify
	// the video
	private static final int VIDEO_CHOOOSE_ID = 9;

	private MediaRecorder mRecorder = null;
	private MediaPlayer mPlayer = null;
	private VideoView mVideoView;
	private ProgressBar mProgressBar;
	private TextView mCapturedText;
	private TextView mTranslatedText;
	private TextView mTranslatedTextLabel;
	private ImageView mStartAnalyzingButton;
	private ImageView mStopAnalyzingButton;

	private static SpeechKit sSpeechKit;

	private PlayVideoAsyncTask mPlayVideoTask;

	// Allow other activities to access the SpeechKit instance.
	static SpeechKit getSpeechKit() {
		return sSpeechKit;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#42619C")));

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
					.defineAudioPrompt(com.threemusketeer.videosubtitling.R.raw.beep);
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
		mTranslatedTextLabel = (TextView) findViewById(R.id.translated_text_label);
		mStartAnalyzingButton = (ImageView) findViewById(R.id.start_recording);
		mStopAnalyzingButton = (ImageView) findViewById(R.id.stop_recording);

		// Set stop analyzing button visibility to gone
		mStopAnalyzingButton.setVisibility(View.GONE);

		mVideoView.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (mVideoAbsolutePath != null) {
					onVideoTouch();
				} else {
					chooseVideo();
				}
				return false;
			}
		});

		mPlayVideoTask = new PlayVideoAsyncTask(mVideoAbsolutePath);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case VIDEO_CHOOOSE_ID:
			if (resultCode == RESULT_OK) {
				Uri selectedVideo = data.getData();
				String[] filePathColumn = { MediaStore.Video.Media.DATA };
				Cursor cursor = getContentResolver().query(selectedVideo,
						filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String filePath = cursor.getString(columnIndex);
				cursor.close();
				System.out.println(filePath);
				mVideoAbsolutePath = filePath;
				if (mPlayVideoTask != null) {
					mPlayVideoTask.selectVideo(mVideoAbsolutePath);
				}
			}
		}
	}

	private void chooseVideo() {
		Intent videoChooser = new Intent(Intent.ACTION_GET_CONTENT);
		videoChooser.setType("video/*");
		startActivityForResult(videoChooser, VIDEO_CHOOOSE_ID);
	}

	/**
	 * Show's the start analyzing button and hides the stop analyzing button.
	 */
	private void showStartAnalyzingButton() {
		mStartAnalyzingButton.setVisibility(View.VISIBLE);
		mStopAnalyzingButton.setVisibility(View.GONE);
	}

	/**
	 * Show's the stop analyzing button and hides the start analyzing button.
	 */
	private void showStopAnalyzingButton() {
		mStartAnalyzingButton.setVisibility(View.GONE);
		mStopAnalyzingButton.setVisibility(View.VISIBLE);
	}

	private void startRecognizeSpeech() {
		showStopAnalyzingButton();

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

	protected void onResume() {
		super.onResume();
		// Set translated text label language
		String translatedTextFormat = getString(R.string.translated_language_label);
		String translatedTextLabel = String.format(translatedTextFormat,
				LanguageSettingsManager.readLanguagePref());
		mTranslatedTextLabel.setText(translatedTextLabel);
	}

	private void stopRecognizeSpeech() {
		showStartAnalyzingButton();
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

				// Show toast message to user
				if (suggestion == null)
					suggestion = "";
				String errorText = detail + "\n" + suggestion;
				Toast.makeText(getApplicationContext(), errorText,
						Toast.LENGTH_LONG).show();

				// for debugging purpose: printing out the speechkit session id
				if (getSpeechKit() != null) {
					android.util.Log.d("Nuance SampleVoiceApp",
							"Recognizer.Listener.onError: session id ["
									+ getSpeechKit().getSessionId() + "]");
				}
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

				showStartAnalyzingButton();
			}
		};
	}

	private void setResult(String result) {
		if (mCapturedText != null) {
			mCapturedText.setText(result);
			if (!result.isEmpty()) {
				translateCapturedTextToPreferredLanguage(result);
			} else if (mTranslatedText != null) {
				mTranslatedText.setText("");
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
			e.printStackTrace();
		} catch (ExecutionException e) {
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
		case R.id.choose_image:
			chooseVideo();
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
			selectVideo(videoPath);
		}

		public void selectVideo(String videoPath) {
			if (videoPath != null) {
				mVideoView.setVideoURI(Uri.parse(videoPath));
				mProgressBar.setProgress(0);
				mProgressBar.setMax(100);
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			mVideoView.setOnPreparedListener(new OnPreparedListener() {

				public void onPrepared(MediaPlayer mp) {
					mDuration = mVideoView.getDuration();
				}
			});

			do {
				try {
					mCurrent = mVideoView.getCurrentPosition();
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
