package com.ltcminer.miner;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Console {
	final int MSG_UIUPDATE = 1;
	final int MSG_STATUPDATE = 2;
	final int MSG_CONSOLE_UPDATE = 7;
	StringBuilder sb = new StringBuilder();
	boolean c_new = false;
	String[] console_a;
	int console_start = 0;
	int console_end = 0;
	Handler sHandler = new Handler();

	public Console(Handler h) {
		Log.i("LC", "Console: Console()");
		sHandler = h;
	}

	public void write(String s) {
		Message msg = new Message();
		Bundle bundle = new Bundle();

		Log.i("LC", "Console: write():" + s);
		if (s != null) {
			if (console_a == null) {
				console_a = new String[20];
				console_a[0] = s;
				console_end++;
			} else {
				console_a[console_end] = s;
				if (console_end == console_start) {
					console_start++;
					if (console_start == console_a.length)
						console_start = 0;
				}
				console_end++;
				if (console_end == console_a.length)
					console_end = 0;
			}
		}
		msg.arg1 = MSG_CONSOLE_UPDATE;
		bundle.putString("console", getConsole());
		msg.setData(bundle);
		sHandler.sendMessage(msg);
	}

	public String getConsole() {
		if (console_a == null)
			return "";

		sb = new StringBuilder();
		if (console_start < console_end) {
			for (int i = console_start; i < console_end; i++)
				sb.append(console_a[i] + '\n');
		} else {
			for (int i = console_start; i < console_a.length; i++)
				sb.append(console_a[i] + '\n');
			for (int i = 0; i < console_end; i++)
				sb.append(console_a[i] + '\n');
		}
		return sb.toString();
	}
}
