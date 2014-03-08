package com.library;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		DBHandler db = new DBHandler(context);
		db.updateAllowed(db.getUserDetails().get("username").toString());
		Toast.makeText(context, "You allowed again", Toast.LENGTH_LONG).show();
	}

}
