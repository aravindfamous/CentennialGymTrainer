package com.gymcoach;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.library.SimpleSideDrawer;
import com.library.UserFunctions;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DashboardActivity extends Activity {
	UserFunctions userFunctions;
    Button btnGeneratePlan;
    SimpleSideDrawer nav;
    TextView Home;
    
	@SuppressWarnings("deprecation")
	private int apiLevel = Integer.valueOf(android.os.Build.VERSION.SDK);
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
            nav.toggleLeftDrawer();
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        userFunctions = new UserFunctions();

        if(userFunctions.isUserLoggedIn(getApplicationContext())) {
        	setTitle(userFunctions.getName(getApplicationContext()));
        	/*
        	 * FutureFeature
        	if(userFunctions.getAllowed(getApplicationContext()) == 1) {
        		updateAllowed();
        	}
        	*/
        	//if verified
        	if(userFunctions.isVerified(getApplicationContext())) {
        		//if has plan
	        	if(userFunctions.hasPlan(getApplicationContext())) {
	        		
	        		if(apiLevel >= 11) {
	        			
	        			setContentView(R.layout.dashboardverified);
	        			getActionBar().setDisplayHomeAsUpEnabled(true);
		        		nav = new SimpleSideDrawer(this);
		        		nav.setLeftBehindContentView(R.layout.activity_behind_left_simple);
		        		Home = (TextView) findViewById(R.id.Home);
		        		Home.setText("This app will help you get ripped or lose fat, click the bar at the top to access side Menu :) Have Fun!");
		        		
	        		} else {
	        			//below api level 11
	        		}
	
	        	} else {	
	        		if(apiLevel >= 11) {
	        			
	        			setContentView(R.layout.dashboard);
	        			getActionBar().setDisplayHomeAsUpEnabled(true);
		        		nav = new SimpleSideDrawer(this);
		        		nav.setLeftBehindContentView(R.layout.activity_behind_left_simple);
		        		
		        		btnGeneratePlan = (Button) findViewById(R.id.btnGenerateAPlan);
		        		btnGeneratePlan.setOnClickListener(new View.OnClickListener() {
		                    
		                    public void onClick(View arg0) {
		                        // TODO Auto-generated method stub
		                        Intent login = new Intent(getApplicationContext(), PIRegisterActivity.class);
		                        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		                        startActivity(login);
		                        // Closing dashboard screen
		                        finish();
		                    }
		                });
		        		
	        		} else {
	        			//below api level 11
	        		}
	        	}
	        	
        	} else {
        		
        		setContentView(R.layout.dashboardnotverified);
    			getActionBar().setDisplayHomeAsUpEnabled(true);
        		nav = new SimpleSideDrawer(this);
        		nav.setLeftBehindContentView(R.layout.activity_behind_left_simple);
        		
        		Button btnSendVerification = (Button) findViewById(R.id.btnSendVerification);
        		Button btnChangeEmail = (Button) findViewById(R.id.btnChangeEmail);
        		final EditText etEmail = (EditText) findViewById(R.id.etEmail);
        		
        		btnSendVerification.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						String email = userFunctions.getEmail(getApplicationContext());
						userFunctions.verification(email, 1);
					}
				});
        		
        		btnChangeEmail.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						String email = userFunctions.getEmail(getApplicationContext());
						String email2 = etEmail.getText().toString();
						userFunctions.verification(email, 2, email2);
						userFunctions.logoutUser(getApplicationContext());
						Intent login = new Intent(getApplicationContext(), LoginActivity.class);
			            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			            startActivity(login);
			            // Closing dashboard screen
			            finish();
					}
				});
        	}
        	
        	listeners();
        	
        } else {
            // user is not logged in show login screen
            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(login);
            // Closing dashboard screen
            finish();
        } 
    }
    
    public void listeners() {
    	//All Set onClick listeners
        Button btnBehindLogout = (Button) findViewById(R.id.btnBehindLogout);
        TextView tvHome = (TextView) findViewById(R.id.tvHome);
        TextView tvExerciseCurrentDay = (TextView) findViewById(R.id.tvExerciseCurrentDay);
        TextView tvDiet = (TextView) findViewById(R.id.tvDiet);

        btnBehindLogout.setOnClickListener(new View.OnClickListener() {
        	@Override
            public void onClick(View arg0) {
                userFunctions.logoutUser(getApplicationContext());
                Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(login); 
                // Closing dashboard screen
                finish();
            }
        });
        
        
        tvHome.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				nav.toggleLeftDrawer();
			}
		});
         
        tvExerciseCurrentDay.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent exercise = new Intent(getApplicationContext(), ExerciseActivity.class);
					exercise.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(exercise); 
                // Closing dashboard screen
                finish();
				}
			});
        
        tvDiet.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent diet = new Intent(getApplicationContext(), DietActivity.class);
				diet.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(diet); 
            // Closing dashboard screen
            finish();
			}
		});
    }
    
    //Future Feature
    /*
    public void updateAllowed() {
    	Calendar calendar = Calendar.getInstance();
    	// 9 AM 
    	calendar.set(Calendar.HOUR_OF_DAY, 9);
    	calendar.set(Calendar.MINUTE, 0);
    	calendar.set(Calendar.SECOND, 0);
    	
    	Intent intentAlarm = new Intent(this, BReceiver.class);
        
        // create the object
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //set the alarm for particular time
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 0, PendingIntent.getBroadcast(this,1,  intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
    }
    */
}
