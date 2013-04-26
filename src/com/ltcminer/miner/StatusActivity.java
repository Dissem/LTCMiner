package com.ltcminer.miner;

import java.text.DecimalFormat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

public class StatusActivity extends MainActivity {
	private static int updateDelay = 1000;
	String unit = " h/s";

	Handler statusHandler = new Handler() {
	};

	final Runnable rConsole = new Runnable() {
		public void run() {
			// Log.i("LC",
			// "StatusActivity:updateConsole:"+mService.console.getConsole());
			TextView txt_console = (TextView) findViewById(R.id.status_console);
			txt_console.setText(mService.cString);
			txt_console.invalidate();
		}
	};

	final Runnable rSpeed = new Runnable() {
		public void run() {
			// Log.i("LC", "StatusActivity:updateSpeed");
			TextView tv_speed = (TextView) findViewById(R.id.status_speed);
			DecimalFormat df = new DecimalFormat("#.##");
			tv_speed.setText(df.format(mService.speed * 1000) + unit);
		}
	};
	final Runnable rAccepted = new Runnable() {
		public void run() {
			// Log.i("LC", "StatusActivity:updateAccepted");
			TextView txt_accepted = (TextView) findViewById(R.id.status_accepted);
			txt_accepted.setText(String.valueOf(mService.accepted));
		}
	};
	final Runnable rRejected = new Runnable() {
		public void run() {
			// Log.i("LC", "StatusActivity:updateRejected");
			TextView txt_rejected = (TextView) findViewById(R.id.status_rejected);
			txt_rejected.setText(String.valueOf(mService.rejected));
		}
	};
	final Runnable rStatus = new Runnable() {
		public void run() {
			// Log.i("LC", "StatusActivity:updateStatus");
			TextView txt_status = (TextView) findViewById(R.id.status_text);
			txt_status.setText(mService.status);
		}
	};

	Thread updateThread = new Thread() {
		public void run() {
			Log.i("LC", "StatusActivity: Update thread started");
			// wait for service to bind
			while (mBound == false) {
				try {
					sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					Log.i("LC", "StatusActivity:updateThread: Interrupted");
				}
			}

			invalidateOptionsMenu();

			while (mBound == true) {
				try {
					sleep(updateDelay);
				} catch (InterruptedException e) {
					Log.i("LC", "StatusActivity:updateThread: Interrupted");
				}

				statusHandler.post(rConsole);
				statusHandler.post(rSpeed);
				statusHandler.post(rAccepted);
				statusHandler.post(rRejected);
				statusHandler.post(rStatus);
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences settings = getSharedPreferences(PREF_TITLE, 0);
		setContentView(R.layout.activity_status);
		Log.i("LC", "Status: onCreate");

		Intent intent = new Intent(getApplicationContext(), MinerService.class);
		startService(intent);
		bindService(intent, super.mConnection, Context.BIND_AUTO_CREATE);

		updateThread.start();

		// Launch news on first run
		if (settings.getBoolean(PREF_NEWS_RUN_ONCE, false) == false) {
			intent = new Intent(getApplicationContext(), SettingsActivity.class);
			startActivity(intent);
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub

		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		if (updateThread.isAlive() == true) {
			updateThread.interrupt();
		}

		Log.i("LC", "Main: in onStop()");
		try {
			unbindService(mConnection);
		} catch (RuntimeException e) {
			Log.i("LC", "RuntimeException:" + e.getMessage());
			// unbindService generates a runtime exception sometimes
			// the service is getting unbound before unBindService is called
			// when the window is dismissed by the user, this is the fix
		}

		super.onStop();
	}

}
