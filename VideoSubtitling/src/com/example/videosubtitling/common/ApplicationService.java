package com.example.videosubtitling.common;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class ApplicationService extends Application {
	public static ApplicationService sApplication;
	private ProgressDialogHandler mProgressDialogLooperThread;
	private ProgressDialog mProgressDialog;

	public ApplicationService() {
		sApplication = this;
	}

	public static ApplicationService getApplicationInstance() {
		return sApplication;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mProgressDialogLooperThread = new ProgressDialogHandler(getMainLooper());
	}

	/*
	 * Show progress dialog with message
	 * 
	 * @param context Activity context
	 * 
	 * @param message Message to be displayed
	 */
	public void showProgressDialog(Context context, String message) {
		mProgressDialogLooperThread.showMessage(context, message);
	}

	/*
	 * Dismiss progress dialog that being show, if any
	 */
	public void dismissProgressDialog() {
		mProgressDialogLooperThread.dismissProgressDialog();
	}

	/*
	 * Thread that implements Looper to show or dismiss progress dialog
	 */
	private class ProgressDialogHandler extends Handler {
		public static final int SHOW_PROGRESS_DIALOG = 1;
		public static final int DISMISS_PROGRESS_DIALOG = 0;
		private Context mContext;
		private String mMessage;

		public ProgressDialogHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				if (mProgressDialog != null && mProgressDialog.isShowing()) {
					mProgressDialog.setMessage(mMessage);
				} else {
					try {
						mProgressDialog = ProgressDialog.show(mContext, "",
								mMessage);
					} catch (Exception e) {
					}
				}
			} else {
				if (mProgressDialog != null) {
					try {
						mProgressDialog.dismiss();
					} catch (IllegalArgumentException e) {

					}
				}
			}

		}

		/*
		 * Show progress dialog with message
		 * 
		 * @param context UI context for the progress dialog to be shown
		 * 
		 * @param message Message to be displayed on progress dialog
		 */
		public void showMessage(Context context, String message) {
			mContext = context;
			mMessage = message;

			sendEmptyMessage(SHOW_PROGRESS_DIALOG);
		}

		/*
		 * Dismiss progress dialog, if any
		 */
		public void dismissProgressDialog() {
			sendEmptyMessage(DISMISS_PROGRESS_DIALOG);
		}
	}
}
