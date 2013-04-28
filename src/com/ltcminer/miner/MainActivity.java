package com.ltcminer.miner;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ltcminer.miner.MinerService.LocalBinder;

public class MainActivity extends Activity implements Constants {
	boolean mBound = false;
	MinerService mService;

	public int curScreenPos = 0;

	public ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i("LC", "Main: onServiceConnected()");
			LocalBinder binder = (LocalBinder) service;
			mService = binder.getService();
			invalidateOptionsMenu();
			mBound = true;
			Log.i("LC", "Main: Service Connected");
		}

		public void onServiceDisconnected(ComponentName name) {
			mBound = false;
		}
	};

	public void startMining() {
		Log.i("LC", "Main: startMining()");
		mService.startMiner();
	}

	public void stopMining() {
		Log.i("LC", "Main: stopMining()");
		mService.stopMiner();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("LC", "Main: in onCreate()");
		setTitle("LTCMiner");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem start = menu.findItem(R.id.menu_start);
		MenuItem stop = menu.findItem(R.id.menu_stop);
		if (mService == null) {
			start.setVisible(true);
			start.setEnabled(false);
			stop.setVisible(false);
		} else {
			boolean running = mService.running;
			start.setVisible(!running);
			start.setEnabled(true);
			stop.setVisible(running);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_start:
			startMining();
			invalidateOptionsMenu();
			break;
		case R.id.menu_stop:
			stopMining();
			invalidateOptionsMenu();
			break;
		case R.id.menu_settings:
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			break;
		}
		return true;
	}
}
