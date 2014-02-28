package com.gymcoach;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.library.UserFunctions;

public class VerifyActivity extends Activity {

	UserFunctions userFunctions;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.verifyyouremail);
		userFunctions = new UserFunctions();
		Thread logout = new Thread() {
            public void run() {
                 
                try {
                    // Thread will sleep for 5 seconds
                    sleep(5*1000);
                    userFunctions.logoutUser(getApplicationContext());
                    Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                    login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(login);
                    finish();
                    
                } catch (Exception e) {
                 
                }
            }
        };
        // start thread
        logout.start();
	}
}
