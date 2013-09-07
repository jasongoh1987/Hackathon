package com.example.videosubtitling;

import java.io.File;
import java.io.IOException;
import android.app.Activity;
import android.graphics.Path;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.VideoView;

public class MainActivity extends Activity
{
	private static final String TAG = "AudioRecordTest";
	private static final String ENVIRONMENT_PATH = Environment
	    .getExternalStorageDirectory().getAbsolutePath() + File.separator;
	private static final String AUDIO_FILE_NAME = "audiorecordtest.3gp";

	private String mVideoFileName = "vid.mp4"; // TODO: allow user to specify
	                                           // the video
	private String mFileName = null;
	private MediaRecorder mRecorder = null;
	private MediaPlayer mPlayer = null;
	private VideoView mVideoView;
	private ProgressBar mProgressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mVideoView = (VideoView) findViewById(R.id.video_view);
		mProgressBar = (ProgressBar) findViewById(R.id.video_progress_bar);

		mVideoView.setOnTouchListener(new View.OnTouchListener()
		{

			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onPause()
	{
		super.onPause();
		if (mRecorder != null)
		{
			mRecorder.release();
			mRecorder = null;
		}

		if (mPlayer != null)
		{
			mPlayer.release();
			mPlayer = null;
		}
	}

	/**
	 * Method to handle respective button clicks.
	 */
	public void onButtonClick(View view)
	{
		switch (view.getId())
		{
		case R.id.start_recording:
			startAudioRecording();
			break;

		case R.id.stop_recording:
			stopAudioRecording();
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
	private void onVideoTouch()
	{
		if (mVideoView.isPlaying())
		{
			mVideoView.pause();
		}
		else
		{
			mVideoView.start();
		}
	}

	/**
	 * Start audio recording
	 */
	private void startAudioRecording()
	{
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setOutputFile(mFileName);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		try
		{
			mRecorder.prepare();
		}
		catch (IOException e)
		{
			Log.e(TAG, "prepare() failed");
		}

		mRecorder.start();
	}

	/**
	 * Stop audio recording
	 */
	private void stopAudioRecording()
	{
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
	}

	/**
	 * Start audio playback
	 */
	private void startAudioPlayback()
	{
		mPlayer = new MediaPlayer();
		try
		{
			mPlayer.setDataSource(mFileName);
			mPlayer.prepare();
			mPlayer.start();
		}
		catch (IOException e)
		{
			Log.e(TAG, "prepare() failed");
		}
	}

	/**
	 * Stop audio playback
	 */
	private void stopAudioPlayback()
	{
		mPlayer.release();
		mPlayer = null;
	}

	/**
	 * Class to play the video based on the path passed in.
	 */
	private class PlayVideoAsyncTask extends AsyncTask<Void, Integer, Void>
	{
		int mDuration = 0;
		int mCurrent = 0;

		PlayVideoAsyncTask(String videoPath)
		{
			mVideoView.setVideoURI(Uri.parse(videoPath));

			mProgressBar.setProgress(0);
			mProgressBar.setMax(100);
		}

		@Override
		protected Void doInBackground(Void... params)
		{

			mVideoView.start();
			mVideoView.setOnPreparedListener(new OnPreparedListener()
			{

				public void onPrepared(MediaPlayer mp)
				{
					mDuration = mVideoView.getDuration();
				}
			});

			do
			{
				mCurrent = mVideoView.getCurrentPosition();
				System.out.println("duration - " + mDuration + " current- "
				    + mCurrent);
				try
				{
					publishProgress((int) (mCurrent * 100 / mDuration));
					if (mProgressBar.getProgress() >= 100)
					{
						break;
					}
				}
				catch (Exception e)
				{
				}
			} while (mProgressBar.getProgress() <= 100);

			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values)
		{
			super.onProgressUpdate(values);
			System.out.println(values[0]);
			mProgressBar.setProgress(values[0]);
		}
	}
}
