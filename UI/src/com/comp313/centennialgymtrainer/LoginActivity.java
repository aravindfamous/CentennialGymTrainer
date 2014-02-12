package com.comp313.centennialgymtrainer;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class LoginActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
	
	}
	
	
	 @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	       
	        getMenuInflater().inflate(R.menu.login, menu);
	        return true;
	    }
	

}
