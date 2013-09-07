package com.example.videosubtitling;

import java.io.IOException;
import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity
{
	private static final String TAG = "AudioRecordTest";
	private static final String AUDIO_FILE_NAME = "/audiorecordtest.3gp";

	private static String mFileName = null;
	private MediaRecorder mRecorder = null;
	private MediaPlayer mPlayer = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set recording output path
		mFileName = Environment.getExternalStorageDirectory().getAbsolutePath()
		    + AUDIO_FILE_NAME;
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
	public void onClick(View view)
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
}
